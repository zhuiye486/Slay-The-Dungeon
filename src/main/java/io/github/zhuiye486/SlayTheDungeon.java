package io.github.zhuiye486;

import java.nio.charset.StandardCharsets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.core.Settings.GameLanguage;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.Keyword;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;

import basemod.BaseMod;
import basemod.helpers.RelicType;
import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.EditCharactersSubscriber;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditRelicsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import io.github.zhuiye486.cards.Arbalest.SniperShot;
import io.github.zhuiye486.cards.Arbalest.SnipersMark;
import io.github.zhuiye486.cards.Arbalest.SuppressingFire;
import io.github.zhuiye486.cards.BountyHunter.FinishHim;
import io.github.zhuiye486.cards.BountyHunter.MarkForDeath;
import io.github.zhuiye486.cards.BountyHunter.Uppercut;
import io.github.zhuiye486.cards.Crusader.BulwarkOfFaith;
import io.github.zhuiye486.cards.Crusader.InspiringCry;
import io.github.zhuiye486.cards.Crusader.StunningBlow;
import io.github.zhuiye486.cards.Strike;
import io.github.zhuiye486.characters.Heroes;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.relics.PressureIndicator;
import io.github.zhuiye486.variables.Heal;

@SpireInitializer
public class SlayTheDungeon implements EditCardsSubscriber, EditStringsSubscriber, EditCharactersSubscriber, PostInitializeSubscriber, EditKeywordsSubscriber, EditRelicsSubscriber {
    private static final String CHARACTER_BUTTON = "SlayTheDungeonResources/img/char/Character_Button.png";
    private static final String CHARACTER_PORTRAIT = "SlayTheDungeonResources/img/char/Character_Portrait.png";
    private static final String BG_ATTACK_512 = "SlayTheDungeonResources/img/512/bg_attack.png";
    private static final String BG_SKILL_512 = "SlayTheDungeonResources/img/512/bg_skill.png";
    private static final String BG_POWER_512 = "SlayTheDungeonResources/img/512/bg_power.png";
    private static final String SMALL_ORB = "SlayTheDungeonResources/img/512/card_small_orb.png";
    private static final String BG_ATTACK_1024 = "SlayTheDungeonResources/img/1024/bg_attack.png";
    private static final String BG_SKILL_1024 = "SlayTheDungeonResources/img/1024/bg_skill.png";
    private static final String BG_POWER_1024 = "SlayTheDungeonResources/img/1024/bg_power.png";
    private static final String BIG_ORB = "SlayTheDungeonResources/img/1024/card_orb.png";
    private static final String ENERGY_ORB = "SlayTheDungeonResources/img/512/card_orb.png";
    
    public static final Color SLAY_THE_DUNGEON_COLOR = new Color(104.0f / 225.0f, 0.0f, 0.2f / 225.0f, 1.0f);
    

    public SlayTheDungeon() {
        BaseMod.subscribe(this);
        BaseMod.addColor(SlayTheDungeonColorEnum.DEEP_RED, SLAY_THE_DUNGEON_COLOR, SLAY_THE_DUNGEON_COLOR, SLAY_THE_DUNGEON_COLOR,
                SLAY_THE_DUNGEON_COLOR, SLAY_THE_DUNGEON_COLOR, SLAY_THE_DUNGEON_COLOR, SLAY_THE_DUNGEON_COLOR,
                BG_ATTACK_512, BG_SKILL_512, BG_POWER_512, ENERGY_ORB,
                BG_ATTACK_1024, BG_SKILL_1024, BG_POWER_1024, BIG_ORB, SMALL_ORB);
    }

    public static void initialize() {
        new SlayTheDungeon();
        System.out.println("SlayTheDungeon initialized.");
    }

    @Override
    public void receiveEditCards() {
        BaseMod.addCard(new Strike());
        BaseMod.addCard(new SniperShot());
        BaseMod.addCard(new SnipersMark());
        BaseMod.addCard(new SuppressingFire());
        BaseMod.addCard(new Uppercut());
        BaseMod.addCard(new FinishHim());
        BaseMod.addCard(new MarkForDeath());
        BaseMod.addCard(new BulwarkOfFaith());
        BaseMod.addCard(new InspiringCry());
        BaseMod.addCard(new StunningBlow());
        BaseMod.addDynamicVariable(new Heal());
    }

    @Override
    public void receiveEditRelics() {
        BaseMod.addRelic(new PressureIndicator(), RelicType.SHARED);
    }

    @Override
    public void receiveEditStrings() {
        String lang;
        if (Settings.language == GameLanguage.ZHS) {
            lang = "ZHS";
        } else {
            lang = "ENG";
        }
        BaseMod.loadCustomStringsFile(CardStrings.class, "SlayTheDungeonResources/localization/" + lang + "/cards.json");
        BaseMod.loadCustomStringsFile(CharacterStrings.class, "SlayTheDungeonResources/localization/" + lang + "/characters.json");
        BaseMod.loadCustomStringsFile(EventStrings.class, "SlayTheDungeonResources/localization/" + lang + "/events.json");
        BaseMod.loadCustomStringsFile(PowerStrings.class, "SlayTheDungeonResources/localization/" + lang + "/powers.json");
        BaseMod.loadCustomStringsFile(RelicStrings.class, "SlayTheDungeonResources/localization/" + lang + "/relics.json");
    }

    @Override
    public void receiveEditCharacters() {
        BaseMod.addCharacter(new Heroes(CardCrawlGame.playerName, SlayTheDungeonColorEnum.Heroes), CHARACTER_BUTTON, CHARACTER_PORTRAIT, SlayTheDungeonColorEnum.Heroes);
    }

    @Override
    public void receivePostInitialize() {
        // Trigger ChooseHeros event after character selection and before game start
        // Note: This is a placeholder; actual triggering may need to be in a patch or different hook
        // For now, assume it's called when the game starts
    }

    @Override
    public void receiveEditKeywords() {
        Gson gson = new Gson();
        String lang;
        if (Settings.language == GameLanguage.ZHS) {
            lang = "ZHS";
        } else {
            lang = "ENG";
        }

        String json = Gdx.files.internal("SlayTheDungeonResources/localization/" + lang + "/keywords.json").readString(String.valueOf(StandardCharsets.UTF_8));
        Keyword[] keywords = gson.fromJson(json, Keyword[].class);
        if (keywords != null){
            for (Keyword keyword : keywords){
                BaseMod.addKeyword("slaythedungeon", keyword.NAMES[0], keyword.NAMES, keyword.DESCRIPTION);
            }
        }   
    }

    

}