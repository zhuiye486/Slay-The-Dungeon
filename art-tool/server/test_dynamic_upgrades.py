import unittest

from server.card_scanner import _parse_card_fields
from server.staging import apply_java_changes


class DynamicUpgradeTests(unittest.TestCase):
    def test_scans_second_magic_number_upgrade(self) -> None:
        source = """
        this.secondMagicNumber = this.baseSecondMagicNumber = 2;
        @Override
        public void upgrade() {
            if (!this.upgraded) {
                upgradeSecondMagicNumber(3);
            }
        }
        """
        fields = _parse_card_fields(source)
        self.assertEqual(fields["upgrade_magic_number_2"], 3)

    def test_scans_chance_upgrade_as_percentage_points(self) -> None:
        source = """
        this.baseChance = this.chance = 0.5f;
        @Override
        public void upgrade() {
            if (!this.upgraded) {
                this.baseChance = this.chance = 0.75f;
            }
        }
        """
        fields = _parse_card_fields(source)
        self.assertEqual(fields["upgrade_chance"], 25.0)

    def test_does_not_treat_constructor_chance_as_upgrade(self) -> None:
        source = """
        this.baseChance = this.chance = 0.5f;
        @Override
        public void upgrade() {
            if (!this.upgraded) {
                upgradeName();
            }
        }
        """
        fields = _parse_card_fields(source)
        self.assertIsNone(fields["upgrade_chance"])

    def test_updates_existing_m2_and_chance_upgrade(self) -> None:
        source = """
        this.baseChance = this.chance = 0.5f;
        public void upgrade() {
            if (!this.upgraded) {
                upgradeSecondMagicNumber(1);
                this.baseChance = this.chance = 0.75f;
            }
        }
        """
        result = apply_java_changes(source, {
            "upgrade_magic_number_2": 2,
            "upgrade_chance": 10.5,
        })
        self.assertIn("upgradeSecondMagicNumber(2);", result)
        self.assertIn("this.baseChance = this.chance = 0.605f;", result)
        self.assertNotIn("ff", result)

    def test_inserts_missing_m2_and_chance_upgrade(self) -> None:
        source = """
        this.baseChance = this.chance = 0.5f;
        public void upgrade() {
            if (!this.upgraded) {
                upgradeName();
            }
        }
        """
        result = apply_java_changes(source, {
            "upgrade_magic_number_2": 2,
            "upgrade_chance": 25,
        })
        self.assertIn("upgradeSecondMagicNumber(2);", result)
        self.assertIn("this.baseChance = this.chance = 0.75f;", result)


if __name__ == "__main__":
    unittest.main()
