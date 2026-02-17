package io.github.zhuiye486;

import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.core.Settings.GameLanguage;
import com.megacrit.cardcrawl.localization.CardStrings;

import basemod.BaseMod;
import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import io.github.zhuiye486.cards.Strike;

@SpireInitializer
public class SlayTheDungeon implements EditCardsSubscriber, EditStringsSubscriber {
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

    @Override
    public void receiveEditStrings() {
        String lang;
        if (Settings.language == GameLanguage.ZHS) {
            lang = "ZHS";
        } else {
            lang = "ENG";
        }
        BaseMod.loadCustomStringsFile(CardStrings.class, "ExampleResources/localization/" + lang + "/cards.json");
    }
}
