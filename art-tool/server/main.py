"""FastAPI 入口。"""

from __future__ import annotations

import json
import shutil
import tempfile
from pathlib import Path
from typing import Any

from fastapi import Body, FastAPI, File, HTTPException, Query, UploadFile
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import FileResponse
from fastapi.staticfiles import StaticFiles

from .card_scanner import CardEntry, entries_to_dicts, scan_cards
from .hero_assets import hero_image_filename, hero_image_path, hero_image_url
from .project_paths import resolve_project_root
from .staging import (
    add_staged_image,
    commit_staged,
    is_staged,
    list_staged,
    load_manifest,
    remove_staged,
    staging_image_path,
    update_staged_crop,
    validate_png,
)

ART_TOOL_ROOT = Path(__file__).resolve().parent.parent
CONFIG_PATH = ART_TOOL_ROOT / "config.json"
STAGING_DIR = ART_TOOL_ROOT / "staging"
WEB_DIR = ART_TOOL_ROOT / "web"
ASSETS_DIR = ART_TOOL_ROOT / "assets"


def load_config() -> dict[str, Any]:
    with CONFIG_PATH.open(encoding="utf-8") as f:
        return json.load(f)


config = load_config()
project_root = resolve_project_root(ART_TOOL_ROOT, config["projectRoot"])
char_images_dir = project_root / config["charImagesDir"]
cards_cache: list[CardEntry] = []
cards_by_safe_id: dict[str, CardEntry] = {}


def refresh_cards() -> list[CardEntry]:
    global cards_cache, cards_by_safe_id
    cards_cache = scan_cards(project_root, config)
    cards_by_safe_id = {c.safe_id: c for c in cards_cache}
    return cards_cache


refresh_cards()

app = FastAPI(title="Slay The Dungeon 卡牌美术工具", version="0.1.0")
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)


def card_with_staging(card: CardEntry) -> dict[str, Any]:
    data = {
        "id": card.id,
        "safe_id": card.safe_id,
        "class_name": card.class_name,
        "name": card.name,
        "description": card.description,
        "hero": card.hero,
        "hero_key": card.hero_key,
        "card_type": card.card_type,
        "card_type_zhs": card.card_type_zhs,
        "cost": card.cost,
        "img_path": card.img_path,
        "image_file": card.image_file,
        "is_placeholder": card.is_placeholder,
        "java_file": card.java_file,
        "target_image_file": card.target_image_file,
        "shared_with": card.shared_with,
        "is_staged": is_staged(STAGING_DIR, card.safe_id),
        "shared_count": len(card.shared_with) + 1,
        "hero_image_file": None,
        "hero_image_url": hero_image_url(
            card.hero_key, project_root, config["charImagesDir"]
        ),
    }
    if data["hero_image_url"]:
        data["hero_image_file"] = hero_image_filename(card.hero_key)
    if data["is_staged"]:
        manifest_entry = load_manifest(STAGING_DIR)["entries"].get(card.safe_id, {})
        data["staging_warnings"] = manifest_entry.get("warnings", [])
        data["crop"] = manifest_entry.get("crop")
    return data


@app.get("/api/health")
def health() -> dict[str, str]:
    return {"status": "ok"}


@app.post("/api/rescan")
def rescan() -> dict[str, Any]:
    entries = refresh_cards()
    return {"count": len(entries)}


@app.get("/api/meta")
def meta() -> dict[str, Any]:
    fresh = load_config()
    heroes = sorted({c.hero for c in cards_cache}, key=lambda h: (h == "通用", h))
    types = sorted({c.card_type_zhs for c in cards_cache})
    staged_count = len(load_manifest(STAGING_DIR)["entries"])
    placeholder_count = sum(1 for c in cards_cache if c.is_placeholder)
    return {
        "heroes": heroes,
        "types": types,
        "total": len(cards_cache),
        "staged_count": staged_count,
        "placeholder_count": placeholder_count,
        "recommended_size": {
            "width": fresh.get("recommendedWidth", 500),
            "height": fresh.get("recommendedHeight", 380),
        },
        "frameSize": fresh.get("frameSize", 1024),
        "frameArtPosition": fresh.get("frameArtPosition", {"x": 262, "y": 286}),
    }


@app.get("/api/cards")
def list_cards(
    hero: str | None = Query(default=None),
    card_type: str | None = Query(default=None, alias="type"),
    placeholder: str | None = Query(default=None),
    staged: str | None = Query(default=None),
    q: str | None = Query(default=None),
) -> dict[str, Any]:
    items = cards_cache

    if hero:
        items = [c for c in items if c.hero == hero]
    if card_type:
        items = [c for c in items if c.card_type_zhs == card_type]
    if placeholder == "true":
        items = [c for c in items if c.is_placeholder]
    elif placeholder == "false":
        items = [c for c in items if not c.is_placeholder]
    if staged == "true":
        items = [c for c in items if is_staged(STAGING_DIR, c.safe_id)]
    elif staged == "false":
        items = [c for c in items if not is_staged(STAGING_DIR, c.safe_id)]
    if q:
        query = q.lower()
        items = [
            c
            for c in items
            if query in c.name.lower()
            or query in c.class_name.lower()
            or query in c.description.lower()
        ]

    return {"cards": [card_with_staging(c) for c in items], "count": len(items)}


@app.get("/api/cards/{safe_id}")
def get_card(safe_id: str) -> dict[str, Any]:
    card = cards_by_safe_id.get(safe_id)
    if not card:
        raise HTTPException(status_code=404, detail="卡牌不存在")
    return card_with_staging(card)


@app.get("/api/image/card/{safe_id}")
def get_card_image(safe_id: str) -> FileResponse:
    card = cards_by_safe_id.get(safe_id)
    if not card:
        raise HTTPException(status_code=404, detail="卡牌不存在")

    staged = staging_image_path(STAGING_DIR, safe_id)
    if staged.exists():
        return FileResponse(staged, media_type="image/png")

    image_path = project_root / config["imagesDir"] / card.image_file
    if not image_path.exists():
        raise HTTPException(status_code=404, detail="图片不存在")
    return FileResponse(image_path, media_type="image/png")


@app.get("/api/staging")
def get_staging() -> dict[str, Any]:
    return {"entries": list_staged(STAGING_DIR), "count": len(list_staged(STAGING_DIR))}


@app.post("/api/staging/{safe_id}")
async def upload_staging(safe_id: str, file: UploadFile = File(...)) -> dict[str, Any]:
    card = cards_by_safe_id.get(safe_id)
    if not card:
        raise HTTPException(status_code=404, detail="卡牌不存在")

    suffix = Path(file.filename or "").suffix.lower()
    if suffix and suffix != ".png":
        raise HTTPException(status_code=400, detail="仅支持 PNG 文件")

    with tempfile.NamedTemporaryFile(delete=False, suffix=".png") as tmp:
        shutil.copyfileobj(file.file, tmp)
        tmp_path = Path(tmp.name)

    try:
        entry = add_staged_image(STAGING_DIR, card, tmp_path, config)
        return {"ok": True, "entry": entry}
    finally:
        tmp_path.unlink(missing_ok=True)


@app.delete("/api/staging/{safe_id}")
def delete_staging(safe_id: str) -> dict[str, Any]:
    if not remove_staged(STAGING_DIR, safe_id):
        raise HTTPException(status_code=404, detail="暂存不存在")
    return {"ok": True}


@app.post("/api/staging/validate")
async def validate_upload(file: UploadFile = File(...)) -> dict[str, Any]:
    with tempfile.NamedTemporaryFile(delete=False, suffix=".png") as tmp:
        shutil.copyfileobj(file.file, tmp)
        tmp_path = Path(tmp.name)
    try:
        warnings = validate_png(tmp_path, config)
        return {"warnings": warnings}
    finally:
        tmp_path.unlink(missing_ok=True)


@app.get("/api/image/hero/{hero_key}")
def get_hero_image(hero_key: str) -> FileResponse:
    char_dir = config.get("charImagesDir", "src/main/resources/SlayTheDungeonResources/img/char")
    path = hero_image_path(project_root, char_dir, hero_key)
    if not path:
        raise HTTPException(status_code=404, detail=f"角色立绘不存在：{hero_key}")
    return FileResponse(path, media_type="image/png")


@app.put("/api/staging/{safe_id}/crop")
def save_crop(safe_id: str, crop: dict = Body(...)) -> dict[str, Any]:
    if safe_id not in cards_by_safe_id:
        raise HTTPException(status_code=404, detail="卡牌不存在")
    if not is_staged(STAGING_DIR, safe_id):
        raise HTTPException(status_code=400, detail="请先上传图片到暂存区")
    try:
        saved = update_staged_crop(STAGING_DIR, safe_id, crop)
        return {"ok": True, "crop": saved}
    except (KeyError, FileNotFoundError) as exc:
        raise HTTPException(status_code=404, detail=str(exc)) from exc


@app.post("/api/commit")
def commit() -> dict[str, Any]:
    refresh_cards()
    result = commit_staged(STAGING_DIR, project_root, config, cards_by_safe_id)
    refresh_cards()
    return result


app.mount("/assets", StaticFiles(directory=ASSETS_DIR), name="assets")
if char_images_dir.is_dir():
    app.mount(
        "/api/resources/char",
        StaticFiles(directory=char_images_dir),
        name="char-resources",
    )
app.mount("/", StaticFiles(directory=WEB_DIR, html=True), name="web")
