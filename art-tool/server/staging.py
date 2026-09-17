"""暂存区读写与图片校验。"""

from __future__ import annotations

import json
import re
from datetime import datetime, timezone
from io import BytesIO
from pathlib import Path
from typing import Any

from PIL import Image

from .card_scanner import CardEntry, safe_id_to_card_id
from .hero_assets import frame_basename
from .image_crop import default_crop_rect, normalize_crop, render_card_variants


def load_manifest(staging_dir: Path) -> dict[str, Any]:
    manifest_path = staging_dir / "manifest.json"
    if not manifest_path.exists():
        return {"entries": {}}
    with manifest_path.open(encoding="utf-8") as f:
        data = json.load(f)
    data.setdefault("entries", {})
    return data


def save_manifest(staging_dir: Path, manifest: dict[str, Any]) -> None:
    manifest_path = staging_dir / "manifest.json"
    with manifest_path.open("w", encoding="utf-8") as f:
        json.dump(manifest, f, ensure_ascii=False, indent=2)


def staging_image_path(staging_dir: Path, safe_id: str) -> Path:
    return staging_dir / "images" / f"{safe_id}.png"


def _read_image_size(file_path: Path) -> tuple[int, int]:
    with Image.open(file_path) as img:
        return img.size


def validate_png(file_path: Path, config: dict[str, Any]) -> list[str]:
    warnings: list[str] = []
    if file_path.suffix.lower() != ".png":
        warnings.append("文件扩展名不是 .png")

    try:
        with Image.open(file_path) as img:
            if img.format != "PNG":
                warnings.append(f"实际格式为 {img.format}，建议使用 PNG")
            width, height = img.size
            rec_w = config.get("recommendedWidth", 500)
            rec_h = config.get("recommendedHeight", 380)
            if width != rec_w or height != rec_h:
                warnings.append(
                    f"尺寸为 {width}×{height}，建议 {rec_w}×{rec_h}（仅提醒，不阻止提交）"
                )
    except Exception as exc:  # noqa: BLE001
        warnings.append(f"无法读取图片：{exc}")

    return warnings


def _base_entry(card: CardEntry, warnings: list[str]) -> dict[str, Any]:
    return {
        "card_id": card.id,
        "safe_id": card.safe_id,
        "class_name": card.class_name,
        "name": card.name,
        "card_type": card.card_type,
        "previous_image_file": card.image_file,
        "previous_img_path": card.img_path,
        "target_image_file": card.target_image_file,
        "target_img_path": f"SlayTheDungeonResources/img/cards/{card.target_image_file}",
        "java_file": card.java_file,
        "staged_at": datetime.now(timezone.utc).isoformat(),
        "warnings": warnings,
    }


def add_staged_image(
    staging_dir: Path,
    card: CardEntry,
    source_path: Path,
    config: dict[str, Any],
) -> dict[str, Any]:
    images_dir = staging_dir / "images"
    images_dir.mkdir(parents=True, exist_ok=True)

    dest = staging_image_path(staging_dir, card.safe_id)
    dest.write_bytes(source_path.read_bytes())

    warnings = validate_png(dest, config)
    width, height = _read_image_size(dest)
    manifest = load_manifest(staging_dir)
    entry = _base_entry(card, warnings)
    entry["crop"] = default_crop_rect(width, height)
    manifest["entries"][card.safe_id] = entry
    save_manifest(staging_dir, manifest)

    return manifest["entries"][card.safe_id]


def update_staged_crop(
    staging_dir: Path,
    safe_id: str,
    crop: dict[str, Any],
) -> dict[str, Any]:
    manifest = load_manifest(staging_dir)
    entry = manifest["entries"].get(safe_id)
    if not entry:
        raise KeyError(f"暂存不存在：{safe_id}")

    staged_file = staging_image_path(staging_dir, safe_id)
    if not staged_file.exists():
        raise FileNotFoundError("暂存图片不存在")

    width, height = _read_image_size(staged_file)
    entry["crop"] = normalize_crop(crop, width, height)
    save_manifest(staging_dir, manifest)
    return entry["crop"]


def remove_staged(staging_dir: Path, safe_id: str) -> bool:
    manifest = load_manifest(staging_dir)
    if safe_id not in manifest["entries"]:
        return False

    image_path = staging_image_path(staging_dir, safe_id)
    if image_path.exists():
        image_path.unlink()

    del manifest["entries"][safe_id]
    save_manifest(staging_dir, manifest)
    return True


def list_staged(staging_dir: Path) -> list[dict[str, Any]]:
    manifest = load_manifest(staging_dir)
    return list(manifest["entries"].values())


def is_staged(staging_dir: Path, safe_id: str) -> bool:
    return safe_id in load_manifest(staging_dir)["entries"]


def prepare_commit_images(
    staged_file: Path,
    entry: dict[str, Any],
    config: dict[str, Any],
    staging_dir: Path,
) -> tuple[bytes, bytes]:
    frame_dir = staging_dir.parent / config.get("frameMaskDir", "assets/frames")
    frame_name = frame_basename(entry.get("card_type", "SKILL"))
    mask_path = frame_dir / f"{frame_name}_mask.png"
    if not mask_path.exists():
        raise FileNotFoundError(f"卡牌 mask 不存在：{mask_path.name}")

    with Image.open(staged_file) as img:
        with Image.open(mask_path) as mask:
            large, small = render_card_variants(img, entry.get("crop"), mask)
            large_buf = BytesIO()
            small_buf = BytesIO()
            large.save(large_buf, format="PNG")
            small.save(small_buf, format="PNG")
            return large_buf.getvalue(), small_buf.getvalue()


def commit_staged(
    staging_dir: Path,
    project_root: Path,
    config: dict[str, Any],
    cards_by_safe_id: dict[str, CardEntry],
) -> dict[str, Any]:
    manifest = load_manifest(staging_dir)
    entries = manifest.get("entries", {})
    if not entries:
        return {"committed": [], "errors": [], "message": "暂存区为空"}

    images_dir = project_root / config["imagesDir"]
    images_dir.mkdir(parents=True, exist_ok=True)

    img_path_re = re.compile(r'(private static final String IMG_PATH = ")[^"]+(";)')

    committed: list[dict[str, Any]] = []
    errors: list[dict[str, str]] = []

    for safe_id, entry in list(entries.items()):
        try:
            card = cards_by_safe_id.get(safe_id)
            if not card:
                card_id = entry.get("card_id") or safe_id_to_card_id(safe_id)
                raise ValueError(f"找不到卡牌索引：{card_id}")

            staged_file = staging_image_path(staging_dir, safe_id)
            if not staged_file.exists():
                raise FileNotFoundError(f"暂存图片不存在：{staged_file.name}")

            target_name = entry["target_image_file"]
            target_path = images_dir / target_name
            large_name = target_name[:-4] + "_p.png"
            large_path = images_dir / large_name
            large_bytes, small_bytes = prepare_commit_images(
                staged_file, entry, config, staging_dir
            )
            target_path.write_bytes(small_bytes)
            large_path.write_bytes(large_bytes)

            java_path = project_root / entry["java_file"]
            content = java_path.read_text(encoding="utf-8")
            new_img_path = entry["target_img_path"]
            new_content, count = img_path_re.subn(
                rf"\1{new_img_path}\2",
                content,
                count=1,
            )
            if count != 1:
                raise ValueError(f"无法在 {java_path.name} 中更新 IMG_PATH")

            java_path.write_text(new_content, encoding="utf-8")

            staged_file.unlink()
            crop_applied = bool(entry.get("crop", {}).get("applied"))
            committed.append(
                {
                    "safe_id": safe_id,
                    "name": entry.get("name", card.name),
                    "target_image_file": target_name,
                    "large_image_file": large_name,
                    "java_file": entry["java_file"],
                    "cropped": crop_applied,
                }
            )
            del manifest["entries"][safe_id]
        except Exception as exc:  # noqa: BLE001
            errors.append({"safe_id": safe_id, "error": str(exc)})

    save_manifest(staging_dir, manifest)
    return {
        "committed": committed,
        "errors": errors,
        "message": f"成功提交 {len(committed)} 项"
        + (f"，{len(errors)} 项失败" if errors else ""),
    }
