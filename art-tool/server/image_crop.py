"""卡牌图裁剪为 500×380。"""

from __future__ import annotations

from typing import Any

from PIL import Image

OUTPUT_WIDTH = 500
OUTPUT_HEIGHT = 380
CROP_ASPECT = OUTPUT_WIDTH / OUTPUT_HEIGHT


def default_crop_rect(width: int, height: int) -> dict[str, Any]:
    """在源图中取最大内接 500:380 矩形，居中。"""
    img_aspect = width / height if height else 1
    if img_aspect > CROP_ASPECT:
        crop_h = height
        crop_w = int(round(crop_h * CROP_ASPECT))
    else:
        crop_w = width
        crop_h = int(round(crop_w / CROP_ASPECT))

    crop_w = min(crop_w, width)
    crop_h = min(crop_h, height)
    x = max(0, (width - crop_w) // 2)
    y = max(0, (height - crop_h) // 2)
    return {
        "x": x,
        "y": y,
        "width": crop_w,
        "height": crop_h,
        "applied": False,
        "sourceWidth": width,
        "sourceHeight": height,
    }


def normalize_crop(crop: dict[str, Any], width: int, height: int) -> dict[str, Any]:
    x = int(max(0, min(crop.get("x", 0), width - 1)))
    y = int(max(0, min(crop.get("y", 0), height - 1)))
    w = int(max(1, crop.get("width", width)))
    h = int(max(1, crop.get("height", height)))
    w = min(w, width - x)
    h = min(h, height - y)
    return {
        "x": x,
        "y": y,
        "width": w,
        "height": h,
        "applied": bool(crop.get("applied", False)),
        "sourceWidth": width,
        "sourceHeight": height,
    }


def render_card_art(image: Image.Image, crop: dict[str, Any] | None) -> Image.Image:
    """输出精确 500×380 的卡牌美术图。"""
    src = image.convert("RGBA")
    if crop and crop.get("applied"):
        box = (
            int(crop["x"]),
            int(crop["y"]),
            int(crop["x"]) + int(crop["width"]),
            int(crop["y"]) + int(crop["height"]),
        )
        src = src.crop(box)

    if src.size != (OUTPUT_WIDTH, OUTPUT_HEIGHT):
        src = src.resize((OUTPUT_WIDTH, OUTPUT_HEIGHT), Image.Resampling.LANCZOS)
    return src
