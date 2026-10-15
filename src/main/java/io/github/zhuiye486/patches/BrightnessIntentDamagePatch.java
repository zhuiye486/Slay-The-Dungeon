package io.github.zhuiye486.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import io.github.zhuiye486.relics.BrightnessIndicator;

public class BrightnessIntentDamagePatch {
    @SpirePatch(clz = AbstractMonster.class, method = "getIntentDmg")
    public static class ApplyBrightnessToIntentDamage {
        @SpirePostfixPatch
        public static int postfix(int result) {
            if (result <= 0 || AbstractDungeon.player == null) {
                return result;
            }
            BrightnessIndicator brightness = (BrightnessIndicator) AbstractDungeon.player
                    .getRelic(BrightnessIndicator.ID);
            if (brightness == null) {
                return result;
            }
            float damageMultiplier = BrightnessIndicator.getDamageTakenMultiplier(brightness.getBrightness());
            return Math.max(0, Math.round(result * damageMultiplier));
        }
    }
}
