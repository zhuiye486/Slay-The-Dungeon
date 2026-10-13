package io.github.zhuiye486.patches;

import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import io.github.zhuiye486.characters.Heroes;

public class ComposedCorpsePatch {
    @SpirePatch(clz = AbstractPlayer.class, method = "playDeathAnimation")
    public static class UseComposedCorpse {
        @SpirePrefixPatch
        public static void prefix(AbstractPlayer __instance) {
            if (!(__instance instanceof Heroes) || Heroes.selectedHeroes == null
                    || Heroes.selectedHeroes.size() != 4) {
                return;
            }
            Texture corpse = Heroes.composeCorpseImage(Heroes.selectedHeroes);
            if (corpse != null) {
                __instance.corpseImg = corpse;
            }
        }
    }
}
