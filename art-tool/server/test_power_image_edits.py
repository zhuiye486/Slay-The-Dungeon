import unittest

from PIL import Image

from server.image_crop import render_power_variants


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


if __name__ == "__main__":
    unittest.main()