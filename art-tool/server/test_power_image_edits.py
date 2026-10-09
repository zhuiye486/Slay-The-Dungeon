import json
import unittest
from pathlib import Path
from tempfile import TemporaryDirectory

from PIL import Image

from server.image_crop import render_power_variants
from server.staging import commit_staged, prepare_power_java_update, undo_last_commit


class PowerImageEditTests(unittest.TestCase):
    def setUp(self) -> None:
        self.image = Image.new("RGBA", (2, 2))
        self.image.putdata([
            (255, 0, 0, 255),
            (0, 255, 0, 255),
            (0, 0, 255, 255),
            (255, 255, 0, 255),
        ])

    def test_rotation_changes_corner_orientation(self) -> None:
        large, small = render_power_variants(self.image, {"rotation": 90})
        self.assertEqual(large.size, (128, 128))
        self.assertEqual(small.size, (48, 48))
        self.assertEqual(large.getpixel((0, 0)), (0, 0, 255, 255))
        self.assertEqual(large.getpixel((127, 0)), (255, 0, 0, 255))

    def test_horizontal_flip_changes_corner_orientation(self) -> None:
        large, _ = render_power_variants(self.image, {"flipX": True})
        self.assertEqual(large.getpixel((0, 0)), (0, 255, 0, 255))
        self.assertEqual(large.getpixel((127, 0)), (255, 0, 0, 255))

    def test_arbitrary_rotation_keeps_square_output_and_transparent_corners(self) -> None:
        large, small = render_power_variants(self.image, {"rotation": 37})
        self.assertEqual(large.size, (128, 128))
        self.assertEqual(small.size, (48, 48))
        self.assertLess(large.getchannel("A").getextrema()[0], 255)
        self.assertNotEqual(large.getpixel((0, 0)), self.image.getpixel((0, 0)))

    def test_power_commit_updates_paths_even_when_source_uses_placeholder(self) -> None:
        source = '''public class CrusadePower {
    public static final String POWER_ID = ModHelper.makePath("Crusade");
    ImageMaster.loadImage("SlayTheDungeonResources/img/powers/LuckyPower128.png");
    ImageMaster.loadImage("SlayTheDungeonResources/img/powers/LuckyPower48.png");
}
'''
        with TemporaryDirectory() as temp_dir:
            powers_dir = Path(temp_dir)
            source_path = powers_dir / "CrusadePower.java"
            source_path.write_text(source, encoding="utf-8")

            java_path, updated = prepare_power_java_update(
                powers_dir,
                "Crusade",
                "CrusadePower128.png",
                "CrusadePower48.png",
            )

        self.assertEqual(java_path.name, "CrusadePower.java")
        self.assertIn("SlayTheDungeonResources/img/powers/CrusadePower128.png", updated)
        self.assertIn("SlayTheDungeonResources/img/powers/CrusadePower48.png", updated)
        self.assertNotIn("LuckyPower", updated)

    def test_power_commit_replaces_vanilla_load_region(self) -> None:
        source = '''public class CommandPower {
    public static final String POWER_ID = ModHelper.makePath("Command");
    public CommandPower() {
        this.loadRegion("accuracy");
    }
}
'''
        with TemporaryDirectory() as temp_dir:
            powers_dir = Path(temp_dir)
            (powers_dir / "CommandPower.java").write_text(source, encoding="utf-8")

            _, updated = prepare_power_java_update(
                powers_dir,
                "Command",
                "CommandPower128.png",
                "CommandPower48.png",
            )

        self.assertIn("CommandPower128.png", updated)
        self.assertIn("CommandPower48.png", updated)
        self.assertNotIn('this.loadRegion("accuracy")', updated)

    def test_power_commit_updates_source_and_undo_restores_it(self) -> None:
        with TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            staging_dir = root / "art-tool" / "staging"
            powers_dir = root / "src/main/resources/SlayTheDungeonResources/img/powers"
            java_dir = root / "src/main/java/io/github/zhuiye486/powers"
            staged_images = staging_dir / "images"
            powers_dir.mkdir(parents=True)
            java_dir.mkdir(parents=True)
            staged_images.mkdir(parents=True)

            java_path = java_dir / "CrusadePower.java"
            original_source = '''public class CrusadePower {
    public static final String POWER_ID = ModHelper.makePath("Crusade");
    ImageMaster.loadImage("SlayTheDungeonResources/img/powers/LuckyPower128.png");
    ImageMaster.loadImage("SlayTheDungeonResources/img/powers/LuckyPower48.png");
}
'''
            java_path.write_text(original_source, encoding="utf-8")
            Image.new("RGBA", (8, 8), (255, 0, 0, 255)).save(
                staged_images / "power__Crusade.png", format="PNG"
            )
            manifest = {
                "entries": {
                    "power__Crusade": {
                        "asset_type": "power",
                        "safe_id": "power__Crusade",
                        "name": "Crusade",
                        "target_image_file": "CrusadePower128.png",
                        "target_small_image_file": "CrusadePower48.png",
                        "crop": {},
                    }
                }
            }
            (staging_dir / "manifest.json").write_text(
                json.dumps(manifest), encoding="utf-8"
            )

            result = commit_staged(
                staging_dir,
                root,
                {
                    "imagesDir": "src/main/resources/SlayTheDungeonResources/img/cards",
                    "powersDir": "src/main/resources/SlayTheDungeonResources/img/powers",
                },
                {},
            )

            self.assertEqual(result["errors"], [])
            self.assertIn("CrusadePower128.png", java_path.read_text(encoding="utf-8"))
            self.assertTrue((powers_dir / "CrusadePower128.png").exists())
            self.assertTrue((powers_dir / "CrusadePower48.png").exists())

            undo_result = undo_last_commit(staging_dir, root)

            self.assertTrue(undo_result["ok"])
            self.assertEqual(java_path.read_text(encoding="utf-8"), original_source)
            self.assertFalse((powers_dir / "CrusadePower128.png").exists())
            self.assertFalse((powers_dir / "CrusadePower48.png").exists())


if __name__ == "__main__":
    unittest.main()