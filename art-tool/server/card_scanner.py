"""卡牌索引扫描：解析 Java 源码与 cards.json。"""

from __future__ import annotations

import json
import re
from dataclasses import asdict, dataclass, field
from pathlib import Path
from typing import Any

HERO_ZHS: dict[str, str] = {
    "Abomination": "憎恶者",
    "Antiquarian": "古物商",
    "Arbalest": "弩手",
    "BountyHunter": "赏金猎人",
    "Crusader": "十字军",
    "Flagellant": "苦修者",
    "GraveRobber": "盗墓贼",
    "Hellion": "狂战士",
    "HighWayMan": "公路侠",
    "Houndmaster": "驯犬师",
    "Jester": "小丑",
    "Leper": "麻风病人",
    "ManAtArms": "盾卫",
    "Musketeer": "火枪手",
    "Occultist": "神秘学者",
    "PlagueDoctor": "瘟疫医生",
    "ShieldBreaker": "破盾者",
    "Vestal": "修女",
    "Affliction": "负面状态",
    "Virtue": "美德",
}

TYPE_ZHS: dict[str, str] = {
    "ATTACK": "攻击",
    "SKILL": "技能",
    "POWER": "能力",
    "CURSE": "诅咒",
    "STATUS": "状态",
}


@dataclass
class CardEntry:
    id: str
    safe_id: str
    class_name: str
    name: str
    description: str
    hero: str
    hero_key: str
    card_type: str
    card_type_zhs: str
    cost: int | None
    img_path: str
    image_file: str
    is_placeholder: bool
    java_file: str
    target_image_file: str
    shared_with: list[str] = field(default_factory=list)


def card_id_to_safe_id(card_id: str) -> str:
    return card_id.replace(":", "__")


def safe_id_to_card_id(safe_id: str) -> str:
    return safe_id.replace("__", ":", 1)


def card_id_to_target_filename(card_id: str, class_name: str | None = None) -> str:
    """Return target filename using PascalCase class name and no mod prefix.

    Prefer an explicit `class_name` if provided; otherwise fall back to
    the part after the colon in `card_id`.
    """
    name = class_name or card_id.split(":", 1)[-1]
    parts = re.split(r"[^0-9A-Za-z]+", name)
    pascal = "".join(p.capitalize() for p in parts if p)
    return f"{pascal}.png"


def infer_hero(java_rel: str) -> tuple[str, str]:
    parts = Path(java_rel).parts
    if "Master" in parts:
        idx = parts.index("Master")
        if idx + 1 < len(parts):
            key = parts[idx + 1]
            return HERO_ZHS.get(key, key), key
    if "Affliction" in parts:
        return HERO_ZHS["Affliction"], "Affliction"
    if "Virtue" in parts:
        return HERO_ZHS["Virtue"], "Virtue"
    return "通用", "Common"


def load_cards_json(path: Path) -> dict[str, dict[str, str]]:
    with path.open(encoding="utf-8") as f:
        return json.load(f)


def scan_cards(project_root: Path, config: dict[str, Any]) -> list[CardEntry]:
    mod_id = config["modId"]
    cards_dir = project_root / config["cardsJavaDir"]
    cards_json_path = project_root / config["cardsJsonPath"]
    placeholders = set(config.get("placeholderImages", []))

    localization = load_cards_json(cards_json_path)
    entries: list[CardEntry] = []

    img_re = re.compile(r'IMG_PATH = "([^"]+)"')
    type_re = re.compile(r"CardType TYPE = CardType\.(\w+)")
    cost_re = re.compile(r"int COST = (-?\d+)")

    for java_file in sorted(cards_dir.rglob("*.java")):
        if java_file.name.startswith("Abstract"):
            continue

        content = java_file.read_text(encoding="utf-8")
        if "IMG_PATH" not in content:
            continue

        img_match = img_re.search(content)
        if not img_match:
            continue

        class_name = java_file.stem
        card_id = f"{mod_id}:{class_name}"
        safe_id = card_id_to_safe_id(card_id)
        java_rel = str(java_file.relative_to(project_root)).replace("\\", "/")

        rel_to_cards = java_file.relative_to(cards_dir)
        hero_zhs, hero_key = infer_hero(str(rel_to_cards))

        type_match = type_re.search(content)
        card_type = type_match.group(1) if type_match else "UNKNOWN"

        cost_match = cost_re.search(content)
        cost = int(cost_match.group(1)) if cost_match else None

        loc_key = card_id
        loc = localization.get(loc_key, {})
        name = loc.get("NAME", class_name)
        description = loc.get("DESCRIPTION", "")

        img_path = img_match.group(1)
        image_file = Path(img_path).name
        target_image_file = card_id_to_target_filename(card_id, class_name)

        entries.append(
            CardEntry(
                id=card_id,
                safe_id=safe_id,
                class_name=class_name,
                name=name,
                description=description,
                hero=hero_zhs,
                hero_key=hero_key,
                card_type=card_type,
                card_type_zhs=TYPE_ZHS.get(card_type, card_type),
                cost=cost,
                img_path=img_path,
                image_file=image_file,
                is_placeholder=image_file in placeholders,
                java_file=java_rel,
                target_image_file=target_image_file,
            )
        )

    by_image: dict[str, list[str]] = {}
    for entry in entries:
        by_image.setdefault(entry.image_file, []).append(entry.id)

    for entry in entries:
        entry.shared_with = [cid for cid in by_image[entry.image_file] if cid != entry.id]

    entries.sort(key=lambda e: (e.hero_key, e.name))
    return entries


def entries_to_dicts(entries: list[CardEntry]) -> list[dict[str, Any]]:
    return [asdict(e) for e in entries]
