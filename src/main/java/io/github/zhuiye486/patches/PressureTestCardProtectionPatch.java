package io.github.zhuiye486.patches;

import java.util.Iterator;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;

import io.github.zhuiye486.relics.PressureIndicator;

public class PressureTestCardProtectionPatch {
    @SpirePatch(clz = CardGroup.class, method = "getPurgeableCards")
    public static class ProtectPressureTestCards {
        @SpirePostfixPatch
        public static void postfix(CardGroup __result) {
            if (__result == null) {
                return;
            }

            Iterator<AbstractCard> iterator = __result.group.iterator();
            while (iterator.hasNext()) {
                if (PressureIndicator.isTestCard(iterator.next().cardID)) {
                    iterator.remove();
                }
            }
        }
    }
}
