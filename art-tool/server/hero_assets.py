"""角色立绘路径映射（与 Heroes.java heroNames 一致）。"""

from __future__ import annotations

from pathlib import Path

# Java Master 包名 -> img/char 文件主名（同 Heroes.heroNames）
HERO_KEY_TO_STEM: dict[str, str] = {
    "Abomination": "Abomination",
    "Antiquarian": "Antiquarian",
    "Arbalest": "Arbalest",
    "BountyHunter": "Bounty_Hunter",
    "Crusader": "Crusader",
    "Flagellant": "Flagellant",
    "GraveRobber": "Grave_Robber",
    "Hellion": "Hellion",
    "HighWayMan": "Highwayman",
    "Houndmaster": "Houndmaster",
    "Jester": "Jester",
    "Leper": "Leper",
    "ManAtArms": "Man_At_Arms",
    "Musketeer": "Musketeer",
    "Occultist": "Occultist",
    "PlagueDoctor": "Plague_Doctor",
    "ShieldBreaker": "Shieldbreaker",
    "Vestal": "Vestal",
}

CARD_TYPE_FRAME: dict[str, str] = {
    "ATTACK": "attack",
    "SKILL": "skill",
    "POWER": "power",
    "CURSE": "skill",
    "STATUS": "skill",
}


def hero_image_filename(hero_key: str) -> str | None:
    stem = HERO_KEY_TO_STEM.get(hero_key)
    if not stem:
        return None
    return f"{stem}.png"


def hero_image_path(project_root: Path, char_dir: str, hero_key: str) -> Path | None:
    filename = hero_image_filename(hero_key)
    if not filename:
        return None
    path = project_root / char_dir / filename
    return path if path.exists() else None


def hero_image_url(hero_key: str, project_root: Path, char_dir: str) -> str | None:
    if not hero_image_path(project_root, char_dir, hero_key):
        return None
    filename = hero_image_filename(hero_key)
    return f"/api/resources/char/{filename}" if filename else None


def frame_basename(card_type: str) -> str:
    return CARD_TYPE_FRAME.get(card_type, "skill")
