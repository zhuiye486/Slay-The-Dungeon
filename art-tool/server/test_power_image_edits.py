import json
import unittest
from pathlib import Path
from tempfile import TemporaryDirectory

from PIL import Image

from server.image_crop import render_power_variants
from server.staging import (
    commit_staged,
    prepare_power_java_update,
    prepare_relic_java_update,
    undo_last_commit,
    update_staged_crop,
)


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

    def test_relic_commit_updates_img_path(self) -> None:
        source = '''public class PressureIndicator {
    private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/ExampleRelic.png";
}
'''
        with TemporaryDirectory() as temp_dir:
            java_path = Path(temp_dir) / "PressureIndicator.java"
            java_path.write_text(source, encoding="utf-8")
            updated = prepare_relic_java_update(java_path, "PressureIndicator.png")

        self.assertIn("SlayTheDungeonResources/img/relics/PressureIndicator.png", updated)
        self.assertNotIn("ExampleRelic.png", updated)

    def test_relic_commit_updates_image_and_source(self) -> None:
        with TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            staging_dir = root / "art-tool" / "staging"
            relics_dir = root / "src/main/resources/SlayTheDungeonResources/img/relics"
            java_dir = root / "src/main/java/io/github/zhuiye486/relics"
            staged_images = staging_dir / "images"
            relics_dir.mkdir(parents=True)
            java_dir.mkdir(parents=True)
            staged_images.mkdir(parents=True)

            java_path = java_dir / "PressureIndicator.java"
            original_source = '''public class PressureIndicator {
    private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/ExampleRelic.png";
}
'''
            java_path.write_text(original_source, encoding="utf-8")
            Image.new("RGBA", (8, 8), (255, 0, 0, 255)).save(
                staged_images / "relic__PressureIndicator.png", format="PNG"
            )
            (staging_dir / "manifest.json").write_text(json.dumps({"entries": {
                "relic__PressureIndicator": {
                    "asset_type": "relic",
                    "safe_id": "relic__PressureIndicator",
                    "name": "压力指示器",
                    "java_file": "src/main/java/io/github/zhuiye486/relics/PressureIndicator.java",
                    "target_image_file": "PressureIndicator.png",
                    "crop": {},
                }
            }}), encoding="utf-8")

            result = commit_staged(
                staging_dir,
                root,
                {"imagesDir": "src/main/resources/SlayTheDungeonResources/img/cards"},
                {},
            )

            self.assertEqual(result["errors"], [])
            self.assertTrue((relics_dir / "PressureIndicator.png").exists())
            self.assertIn("PressureIndicator.png", java_path.read_text(encoding="utf-8"))
            self.assertTrue(undo_last_commit(staging_dir, root)["ok"])
            self.assertEqual(java_path.read_text(encoding="utf-8"), original_source)
            self.assertFalse((relics_dir / "PressureIndicator.png").exists())

    def test_relic_crop_preserves_rotation_and_flip(self) -> None:
        with TemporaryDirectory() as temp_dir:
            staging_dir = Path(temp_dir)
            images_dir = staging_dir / "images"
            images_dir.mkdir()
            Image.new("RGBA", (100, 80), (255, 0, 0, 255)).save(
                images_dir / "relic__PressureIndicator.png", format="PNG"
            )
            (staging_dir / "manifest.json").write_text(json.dumps({"entries": {
                "relic__PressureIndicator": {"asset_type": "relic"}
            }}), encoding="utf-8")

            crop = update_staged_crop(staging_dir, "relic__PressureIndicator", {
                "x": 10,
                "y": 5,
                "width": 70,
                "height": 70,
                "applied": True,
                "rotation": 90,
                "flipX": True,
            })

        self.assertEqual(crop["rotation"], 90)
        self.assertTrue(crop["flipX"])

    def test_relic_crop_endpoint_accepts_staged_relic(self) -> None:
        with TemporaryDirectory() as temp_dir:
            staging_dir = Path(temp_dir)
            images_dir = staging_dir / "images"
            images_dir.mkdir()
            Image.new("RGBA", (100, 80), (255, 0, 0, 255)).save(
                images_dir / "relic__PressureIndicator.png", format="PNG"
            )
            (staging_dir / "manifest.json").write_text(json.dumps({"entries": {
                "relic__PressureIndicator": {"asset_type": "relic"}
            }}), encoding="utf-8")

            from server import main
            original_staging_dir = main.STAGING_DIR
            try:
                main.STAGING_DIR = staging_dir
                response = main.save_crop("relic__PressureIndicator", {
                    "x": 10,
                    "y": 5,
                    "width": 70,
                    "height": 70,
                    "applied": True,
                    "rotation": 90,
                    "flipX": True,
                })
            finally:
                main.STAGING_DIR = original_staging_dir

        self.assertTrue(response["ok"])
        self.assertEqual(response["crop"]["rotation"], 90)
        self.assertTrue(response["crop"]["flipX"])

    def test_card_crop_endpoint_stages_original_image(self) -> None:
        from server import main

        card = next(iter(main.cards_by_safe_id.values()))
        with TemporaryDirectory() as temp_dir:
            original_staging_dir = main.STAGING_DIR
            try:
                main.STAGING_DIR = Path(temp_dir)
                response = main.save_crop(card.safe_id, {
                    "x": 10,
                    "y": 10,
                    "width": 100,
                    "height": 76,
                    "applied": True,
                })
                entry = json.loads(
                    (main.STAGING_DIR / "manifest.json").read_text(encoding="utf-8")
                )["entries"][card.safe_id]
            finally:
                main.STAGING_DIR = original_staging_dir

        self.assertTrue(response["ok"])
        self.assertTrue(entry["crop"]["applied"])
        self.assertEqual(entry["target_image_file"], card.target_image_file)

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