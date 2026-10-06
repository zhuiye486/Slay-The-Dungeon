"""暂存区读写与图片校验。"""

from __future__ import annotations

import json
import re
import shutil
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
        "field_changes": {},
        "source_snapshot": {},
    }


EDITABLE_FIELDS = {
    "name", "name_en", "description", "description_en", "cost", "rarity",
    "card_type", "damage", "block", "magic_number", "heal", "upgrade_cost",
    "magic_number_2", "chance", "chance_2",
    "upgrade_damage", "upgrade_block", "upgrade_magic_number", "upgrade_heal",
    "upgrade_description", "tags",
}


def stage_card_fields(
    staging_dir: Path,
    card: CardEntry,
    changes: dict[str, Any],
) -> dict[str, Any]:
    if not card.editable:
        raise ValueError("此卡牌源码不支持基础编辑：" + "、".join(card.edit_warnings))
    invalid = set(changes) - EDITABLE_FIELDS
    if invalid:
        raise ValueError(f"不支持编辑字段：{', '.join(sorted(invalid))}")

    manifest = load_manifest(staging_dir)
    entry = manifest["entries"].get(card.safe_id) or _base_entry(card, [])
    entry.setdefault("field_changes", {}).update(changes)
    entry.setdefault("source_snapshot", {}).update(
        {name: getattr(card, name) for name in changes}
    )
    entry["staged_at"] = datetime.now(timezone.utc).isoformat()
    manifest["entries"][card.safe_id] = entry
    save_manifest(staging_dir, manifest)
    return entry


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
    entry = manifest["entries"].get(card.safe_id, _base_entry(card, warnings))
    entry["warnings"] = warnings
    entry["staged_at"] = datetime.now(timezone.utc).isoformat()
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


def _replace_constant(content: str, field: str, value: Any) -> str:
    if value is None or value == "":
        return content
    if field == "card_type":
        pattern = r"(CardType TYPE\s*=\s*CardType\.)\w+"
        replacement = rf"\g<1>{value}"
    elif field == "rarity":
        pattern = r"(CardRarity RARITY\s*=\s*CardRarity\.)\w+"
        replacement = rf"\g<1>{value}"
    else:
        pattern = rf"(int {field.upper()}\s*=\s*)-?\d+"
        replacement = rf"\g<1>{value}"
    return re.sub(pattern, replacement, content, count=1)


def apply_java_changes(content: str, changes: dict[str, Any]) -> str:
    for field in ("cost", "card_type", "rarity"):
        content = _replace_constant(content, field, changes.get(field))

    for field_name, java_name in {
        "damage": "damage", "block": "block", "magic_number": "magicNumber",
        "magic_number_2": "secondMagicNumber", "heal": "heal"
    }.items():
        value = changes.get(field_name)
        if value is None:
            continue
        capitalized = java_name[0].upper() + java_name[1:]
        content = re.sub(
            rf"((?:this\.)?{java_name}\s*=\s*(?:this\.)?base{capitalized}\s*=\s*)-?\d+"
            rf"|((?:this\.)?base{capitalized}\s*=\s*(?:this\.)?{java_name}\s*=\s*)-?\d+",
            lambda match: f"{match.group(1) or match.group(2)}{int(value)}",
            content,
            count=1,
        )

    for field_name, method in {
        "upgrade_damage": "Damage", "upgrade_block": "Block",
        "upgrade_magic_number": "MagicNumber", "upgrade_heal": "Heal",
    }.items():
        if changes.get(field_name) is not None:
            replacement = re.sub(
                rf"(upgrade{method}\s*\(\s*)-?\d+(\s*\))",
                rf"\g<1>{int(changes[field_name])}\g<2>",
                content,
                count=1,
            )
            if replacement == content:
                replacement = re.sub(
                    rf"((?:this\.)?base{method}\s*\+=\s*)-?\d+",
                    rf"\g<1>{int(changes[field_name])}",
                    content,
                    count=1,
                )
            content = replacement

    if changes.get("chance") is not None or changes.get("chance_2") is not None:
        chance = changes.get("chance")
        if chance is None:
            chance = 100.0 - float(changes["chance_2"])
        chance = max(0.0, min(100.0, float(chance))) / 100.0
        content = re.sub(
            r"((?:this\.)?chance\s*=\s*(?:this\.)?baseChance\s*=\s*)-?(?:\d+(?:\.\d*)?|\.\d+)"
            r"|((?:this\.)?baseChance\s*=\s*(?:this\.)?chance\s*=\s*)-?(?:\d+(?:\.\d*)?|\.\d+)",
            lambda match: f"{match.group(1) or match.group(2)}{chance:g}",
            content,
            count=1,
        )
    if changes.get("upgrade_cost") is not None:
        content = re.sub(
            r"(upgradeBaseCost\s*\(\s*)-?\d+(\s*\))",
            rf"\g<1>{int(changes['upgrade_cost'])}\g<2>",
            content,
            count=1,
        )

    if "tags" in changes:
        if not re.search(r"        this\.tags\.add\(", content) and changes["tags"]:
            raise ValueError("此卡牌没有可安全替换的原生标签字段")
        tag_lines = "".join(
            f"        this.tags.add({tag if '.' in tag else f'CardTags.{tag}'});\n"
            for tag in changes["tags"]
        )
        content = re.sub(
            r"(?:        this\.tags\.add\((?:CardTags|CardTagsEnum)\.\w+\);\n?)+",
            tag_lines,
            content,
            count=1,
        ) if re.search(r"        this\.tags\.add\(", content) else content
    return content


def _update_localization(path: Path, card_id: str, changes: dict[str, Any], language: str) -> None:
    if not path.exists():
        if language == "ENG" and not any(k in changes for k in ("name_en", "description_en")):
            return
        if language == "ENG":
            path.parent.mkdir(parents=True, exist_ok=True)
            data: dict[str, Any] = {}
        else:
            raise FileNotFoundError(f"本地化文件不存在：{path}")
    else:
        with path.open(encoding="utf-8") as file:
            data = json.load(file)
    item = data.setdefault(card_id, {})
    mapping = {
        "name": "NAME", "description": "DESCRIPTION", "upgrade_description": "UPGRADE_DESCRIPTION"
    } if language == "ZHS" else {
        "name_en": "NAME", "description_en": "DESCRIPTION"
    }
    for field, key in mapping.items():
        if field in changes:
            item[key] = str(changes[field])
    path.write_text(json.dumps(data, ensure_ascii=False, indent=4) + "\n", encoding="utf-8")


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
    backup_id = datetime.now(timezone.utc).strftime("%Y%m%dT%H%M%S%fZ")
    backup_dir = staging_dir / "backups" / backup_id
    backup_dir.mkdir(parents=True, exist_ok=True)
    backup_files: list[dict[str, Any]] = []

    def backup_file(path: Path) -> None:
        project_relative = str(path.relative_to(project_root)).replace("\\", "/")
        if any(item["path"] == project_relative for item in backup_files):
            return
        backup_path = backup_dir / project_relative
        item: dict[str, Any] = {"path": project_relative, "exists": path.exists()}
        if path.exists():
            backup_path.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(path, backup_path)
        backup_files.append(item)

    committed: list[dict[str, Any]] = []
    errors: list[dict[str, str]] = []

    for safe_id, entry in list(entries.items()):
        try:
            card = cards_by_safe_id.get(safe_id)
            if not card:
                card_id = entry.get("card_id") or safe_id_to_card_id(safe_id)
                raise ValueError(f"找不到卡牌索引：{card_id}")

            target_name = entry["target_image_file"]
            staged_file = staging_image_path(staging_dir, safe_id)
            target_path = images_dir / target_name
            large_name = target_name[:-4] + "_p.png"
            large_path = images_dir / large_name
            java_path = project_root / entry["java_file"]
            content = java_path.read_text(encoding="utf-8")
            changes = entry.get("field_changes", {})
            if changes and not card.editable:
                raise ValueError("此卡牌源码不支持基础编辑")

            backup_file(java_path)
            new_content = apply_java_changes(content, changes)
            if staged_file.exists():
                backup_file(target_path)
                backup_file(large_path)
                large_bytes, small_bytes = prepare_commit_images(
                    staged_file, {**entry, "card_type": changes.get("card_type", card.card_type)}, config, staging_dir
                )
                target_path.write_bytes(small_bytes)
                large_path.write_bytes(large_bytes)
                new_content, count = re.subn(
                    r'(private static final String IMG_PATH = ")[^" ]+(";)',
                    rf"\1{entry['target_img_path']}\2",
                    new_content,
                    count=1,
                )
                if count != 1:
                    raise ValueError(f"无法在 {java_path.name} 中更新 IMG_PATH")
                staged_file.unlink()

            zhs_path = project_root / config["cardsJsonPath"]
            eng_path = zhs_path.parent.parent / "ENG" / zhs_path.name
            if any(key in changes for key in ("name", "description", "upgrade_description")):
                backup_file(zhs_path)
                _update_localization(zhs_path, card.id, changes, "ZHS")
            if any(key in changes for key in ("name_en", "description_en")):
                backup_file(eng_path)
                _update_localization(eng_path, card.id, changes, "ENG")

            java_path.write_text(new_content, encoding="utf-8")
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

    if committed:
        manifest["last_commit"] = {
            "id": backup_id,
            "directory": str(backup_dir.relative_to(staging_dir)),
            "files": backup_files,
        }
    save_manifest(staging_dir, manifest)
    return {
        "committed": committed,
        "errors": errors,
        "message": f"成功提交 {len(committed)} 项"
        + (f"，{len(errors)} 项失败" if errors else ""),
    }


def undo_last_commit(staging_dir: Path, project_root: Path) -> dict[str, Any]:
    manifest = load_manifest(staging_dir)
    last_commit = manifest.get("last_commit")
    if not last_commit:
        return {"ok": False, "message": "没有可撤销的提交"}

    backup_dir = staging_dir / last_commit["directory"]
    restored = 0
    for item in last_commit.get("files", []):
        target = project_root / item["path"]
        backup = backup_dir / item["path"]
        if item.get("exists"):
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(backup, target)
        else:
            target.unlink(missing_ok=True)
        restored += 1

    manifest.pop("last_commit", None)
    save_manifest(staging_dir, manifest)
    return {"ok": True, "restored": restored, "message": "已撤销上次提交"}
