package io.github.zhuiye486.events;

import java.lang.reflect.Field;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.InfiniteSpeechBubble;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;

public interface ChooseHeroesWithNeow {
    
    @SpirePatch(clz = NeowEvent.class, method = "buttonEffect")
    public static class TriggerChooseHeroesEventPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractEvent e, int buttonPressed) {
            System.out.println("Patch check: player name = " + AbstractDungeon.player.name + ", buttonPressed = " + buttonPressed);
            if (AbstractDungeon.player.chosenClass == SlayTheDungeonColorEnum.Heroes) {
                System.out.println("Patch triggered for Heroes");
                try {
                    ChooseHeroesEventsTrigger.removeSpeechBubbles();
                    ChooseHeroesEventsTrigger.triggerChooseHeroesEvent();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
}
