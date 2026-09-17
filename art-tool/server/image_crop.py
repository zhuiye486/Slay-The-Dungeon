"""卡牌图按裁剪框和卡牌 mask 生成双尺寸 PNG。"""

from __future__ import annotations

from typing import Any

from PIL import Image

OUTPUT_WIDTH = 500
OUTPUT_HEIGHT = 380
CROP_ASPECT = OUTPUT_WIDTH / OUTPUT_HEIGHT
SMALL_WIDTH = 250
SMALL_HEIGHT = 190


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


def apply_mask(image: Image.Image, mask: Image.Image) -> Image.Image:
    """使用 mask 的 alpha 通道裁掉卡牌边缘，同时保留图片透明度。"""
    art = image.convert("RGBA")
    mask_alpha = mask.convert("RGBA").getchannel("A")
    alpha = art.getchannel("A")
    alpha = Image.composite(alpha, Image.new("L", art.size, 0), mask_alpha)
    art.putalpha(alpha)
    return art


def render_card_variants(
    image: Image.Image,
    crop: dict[str, Any] | None,
    mask: Image.Image,
) -> tuple[Image.Image, Image.Image]:
    """生成 500×380 大图和 250×190 小图。"""
    large = apply_mask(render_card_art(image, crop), mask)
    small = large.resize((SMALL_WIDTH, SMALL_HEIGHT), Image.Resampling.LANCZOS)
    return large, small
