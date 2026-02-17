package io.github.zhuiye486;

import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;

import basemod.BaseMod;
import basemod.interfaces.EditCardsSubscriber;
import io.github.zhuiye486.cards.Strike;

@SpireInitializer
public class SlayTheDungeon implements EditCardsSubscriber {
    public SlayTheDungeon() {
    }

    public static void initialize() {
        SlayTheDungeon instance = new SlayTheDungeon();
        BaseMod.subscribe(instance);
        System.out.println("SlayTheDungeon initialized.");
    }

    @Override
    public void receiveEditCards() {
        BaseMod.addCard(new Strike());
    }
}
