package io.github.zhuiye486.patches;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

@SpirePatch(clz = AbstractCard.class, method = SpirePatch.CLASS)
public class AbstractCardFields {
    public static SpireField<Boolean> isHealModified = new SpireField<>(() -> false);
    public static SpireField<Boolean> upgradedHeal = new SpireField<>(() -> false);

    @SpirePatch(clz = AbstractCard.class, method = "displayUpgrades")
    public static class DisplayUpgradesPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractCard __instance) {
            if (AbstractCardFields.upgradedHeal.get(__instance)) {
                __instance.heal = __instance.baseHeal;
                AbstractCardFields.isHealModified.set(__instance, true);
            }
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "getDynamicValue")
    private static class GetDynamicValuePatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractCard __instance, String key, String __result) {
            if (key.equals("SlayTheDungeon:H")) {
                if (__instance.heal > __instance.baseHeal) {
                    __result = "[#7fff00]" + Integer.toString(__instance.heal) + "[]";
                }

                __result = "[#ff6563]" + Integer.toString(__instance.heal) + "[]";
            }
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "renderDynamicVariable")
    private static class RenderDynamicVariablePatch {
        @SpireInsertPatch(rloc = 2519 - 2471, localvars = {"num", "c"})
        public static void Insert(AbstractCard __instance, String key, int num, Color c) {
            if (key.equals("SlayTheDungeon:H")) {
                if (__instance.isHealModified) {
                    num = __instance.heal;
                    if (__instance.heal > __instance.baseHeal) {
                        c = Settings.GREEN_TEXT_COLOR;
                    } else {
                        c = Settings.RED_TEXT_COLOR;
                    }
                }
            }
        }
    }
}



