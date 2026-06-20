"""解析主项目根目录。"""

from __future__ import annotations

from pathlib import Path


def resolve_project_root(art_tool_root: Path, configured: str) -> Path:
    candidate = (art_tool_root / configured).resolve()
    marker = candidate / "src/main/resources/SlayTheDungeonResources"
    if marker.is_dir():
        return candidate

    parent = art_tool_root.parent.resolve()
    if (parent / "src/main/resources/SlayTheDungeonResources").is_dir():
        return parent

    if (art_tool_root / "src/main/resources/SlayTheDungeonResources").is_dir():
        return art_tool_root.resolve()

    return candidate
