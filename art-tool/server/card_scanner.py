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
    "HighWayMan": "强盗",
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
    editable: bool = True
    edit_warnings: list[str] = field(default_factory=list)
    name_en: str = ""
    description_en: str = ""
    rarity: str = ""
    damage: int | None = None
    block: int | None = None
    magic_number: int | None = None
    heal: int | None = None
    upgrade_cost: int | None = None
    upgrade_damage: int | None = None
    upgrade_block: int | None = None
    upgrade_magic_number: int | None = None
    upgrade_heal: int | None = None
    upgrade_description: str = ""
    tags: list[str] = field(default_factory=list)


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
    pascal = "".join(
        part[:1].upper() + part[1:]
        for part in parts
        if part
    )
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


def load_optional_cards_json(path: Path) -> dict[str, dict[str, str]]:
    if not path.exists():
        return {}
    return load_cards_json(path)


def _number_assignment(content: str, field_name: str) -> int | None:
    capitalized = field_name[0].upper() + field_name[1:]
    match = re.search(
        rf"(?:this\.)?{field_name}\s*=\s*(?:this\.)?base{capitalized}\s*=\s*(-?\d+)"
        rf"|(?:this\.)?base{capitalized}\s*=\s*(?:this\.)?{field_name}\s*=\s*(-?\d+)",
        content,
    )
    if not match:
        return None
    return int(match.group(1) or match.group(2))


def _upgrade_delta(content: str, method_name: str) -> int | None:
    match = re.search(rf"upgrade{method_name}\s*\(\s*(-?\d+)\s*\)", content)
    return int(match.group(1)) if match else None


def _parse_card_fields(content: str) -> dict[str, Any]:
    fields: dict[str, Any] = {}
    for name, pattern in {
            "cost": r"int COST\s*=\s*(-?\d+)",
            "card_type": r"CardType TYPE\s*=\s*CardType\.(\w+)",
            "rarity": r"CardRarity RARITY\s*=\s*CardRarity\.(\w+)",
    }.items():
        match = re.search(pattern, content)
        if name == "cost":
            fields[name] = int(match.group(1)) if match else None
        else:
            fields[name] = match.group(1) if match else ""

    fields["damage"] = _number_assignment(content, "damage")
    fields["block"] = _number_assignment(content, "block")
    fields["magic_number"] = _number_assignment(content, "magicNumber")
    fields["heal"] = _number_assignment(content, "heal")
    fields["upgrade_cost"] = next(
        (int(value) for value in re.findall(r"upgradeBaseCost\s*\(\s*(-?\d+)\s*\)", content)),
        None,
    )
    fields["upgrade_damage"] = _upgrade_delta(content, "Damage")
    fields["upgrade_block"] = _upgrade_delta(content, "Block")
    fields["upgrade_magic_number"] = _upgrade_delta(content, "MagicNumber")
    fields["upgrade_heal"] = _upgrade_delta(content, "Heal")
    fields["tags"] = re.findall(
        r"(?:this\.)?tags\.add\(\s*((?:CardTags|CardTagsEnum)\.\w+)\s*\)", content
    )
    return fields


def scan_cards(project_root: Path, config: dict[str, Any]) -> list[CardEntry]:
    mod_id = config["modId"]
    cards_dir = project_root / config["cardsJavaDir"]
    cards_json_path = project_root / config["cardsJsonPath"]
    placeholders = set(config.get("placeholderImages", []))

    localization = load_cards_json(cards_json_path)
    english_path = cards_json_path.parent.parent / "ENG" / cards_json_path.name
    english_localization = load_optional_cards_json(english_path)
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
        loc_en = english_localization.get(loc_key, {})
        name = loc.get("NAME", class_name)
        description = loc.get("DESCRIPTION", "")
        fields = _parse_card_fields(content)
        edit_warnings: list[str] = []
        if fields["cost"] is None or not fields["card_type"] or not fields["rarity"]:
            edit_warnings.append("缺少标准 COST、TYPE 或 RARITY 字段")
        if "void upgrade()" not in content:
            edit_warnings.append("未找到标准 upgrade() 方法")

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
                editable=not edit_warnings,
                edit_warnings=edit_warnings,
                name_en=loc_en.get("NAME", ""),
                description_en=loc_en.get("DESCRIPTION", ""),
                rarity=fields["rarity"],
                damage=fields["damage"],
                block=fields["block"],
                magic_number=fields["magic_number"],
                heal=fields["heal"],
                upgrade_cost=fields["upgrade_cost"],
                upgrade_damage=fields["upgrade_damage"],
                upgrade_block=fields["upgrade_block"],
                upgrade_magic_number=fields["upgrade_magic_number"],
                upgrade_heal=fields["upgrade_heal"],
                upgrade_description=loc.get("UPGRADE_DESCRIPTION", ""),
                tags=fields["tags"],
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
