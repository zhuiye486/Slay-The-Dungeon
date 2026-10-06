package io.github.zhuiye486.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import io.github.zhuiye486.relics.BrightnessIndicator;

public class BrightnessGoldPatch {
    @SpirePatch(clz = AbstractPlayer.class, method = "gainGold", paramtypez = {int.class})
    public static class ScaleGoldByBrightness {
        @SpirePrefixPatch
        public static void prefix(AbstractPlayer player, @ByRef int[] amount) {
            if (amount == null || amount.length == 0 || amount[0] <= 0) {
                return;
            }
            BrightnessIndicator brightness = (BrightnessIndicator) player.getRelic(BrightnessIndicator.ID);
            if (brightness == null) {
                return;
            }
            float lootMultiplier = (150.0f - brightness.getBrightness()) / 100.0f;
            amount[0] = Math.max(0, Math.round(amount[0] * lootMultiplier));
        }
    }
}