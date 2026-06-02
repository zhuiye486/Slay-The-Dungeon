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
import io.github.zhuiye486.cards.Affliction.Abusive;
import io.github.zhuiye486.cards.Affliction.Fearful;
import io.github.zhuiye486.cards.Affliction.Hopeless;
import io.github.zhuiye486.cards.Affliction.Irrational;
import io.github.zhuiye486.cards.Affliction.Masochistic;
import io.github.zhuiye486.cards.Affliction.Paranoid;
import io.github.zhuiye486.cards.Affliction.Selfish;
import io.github.zhuiye486.cards.CriticalHit;
import io.github.zhuiye486.cards.Dodge;
import io.github.zhuiye486.cards.Escape;
import io.github.zhuiye486.cards.Food;
import io.github.zhuiye486.cards.FromBeyond;
import io.github.zhuiye486.cards.HolyWater;
import io.github.zhuiye486.cards.Hound;
import io.github.zhuiye486.cards.Kleptomaniac;
import io.github.zhuiye486.cards.LetsGoGambling;
import io.github.zhuiye486.cards.Master.Abomination.Rake;
import io.github.zhuiye486.cards.Master.Abomination.Slam;
import io.github.zhuiye486.cards.Master.Abomination.Transform;
import io.github.zhuiye486.cards.Master.Antiquarian.FesteringVapours;
import io.github.zhuiye486.cards.Master.Antiquarian.FortifyingVapours;
import io.github.zhuiye486.cards.Master.Antiquarian.InvigoratingVapours;
import io.github.zhuiye486.cards.Master.Arbalest.SniperShot;
import io.github.zhuiye486.cards.Master.Arbalest.SnipersMark;
import io.github.zhuiye486.cards.Master.Arbalest.SuppressingFire;
import io.github.zhuiye486.cards.Master.BountyHunter.FinishHim;
import io.github.zhuiye486.cards.Master.BountyHunter.MarkForDeath;
import io.github.zhuiye486.cards.Master.BountyHunter.Uppercut;
import io.github.zhuiye486.cards.Master.Crusader.BulwarkOfFaith;
import io.github.zhuiye486.cards.Master.Crusader.InspiringCry;
import io.github.zhuiye486.cards.Master.Crusader.StunningBlow;
import io.github.zhuiye486.cards.Master.Flagellant.RainOfSorrows;
import io.github.zhuiye486.cards.Master.Flagellant.Reclaim;
import io.github.zhuiye486.cards.Master.Flagellant.Redeem;
import io.github.zhuiye486.cards.Master.GraveRobber.PoisonDart;
import io.github.zhuiye486.cards.Master.GraveRobber.ShadowFade;
import io.github.zhuiye486.cards.Master.GraveRobber.ToxinTrickery;
import io.github.zhuiye486.cards.Master.Hellion.BleedOut;
import io.github.zhuiye486.cards.Master.Hellion.IfItBleeds;
import io.github.zhuiye486.cards.Master.Hellion.IronSawn;
import io.github.zhuiye486.cards.Master.HighWayMan.DuelistsAdvance;
import io.github.zhuiye486.cards.Master.HighWayMan.OpenVein;
import io.github.zhuiye486.cards.Master.HighWayMan.PointBlankShoot;
import io.github.zhuiye486.cards.Master.Houndmaster.CryHavoc;
import io.github.zhuiye486.cards.Master.Houndmaster.HoundsHurry;
import io.github.zhuiye486.cards.Master.Houndmaster.HoundsRush;
import io.github.zhuiye486.cards.Master.Jester.BattleBallad;
import io.github.zhuiye486.cards.Master.Jester.Finale;
import io.github.zhuiye486.cards.Master.Jester.InspiringTune;
import io.github.zhuiye486.cards.Master.Leper.Intimidate;
import io.github.zhuiye486.cards.Master.Leper.Revenge;
import io.github.zhuiye486.cards.Master.Leper.Withstand;
import io.github.zhuiye486.cards.Master.ManAtArms.Bolster;
import io.github.zhuiye486.cards.Master.ManAtArms.Defender;
import io.github.zhuiye486.cards.Master.ManAtArms.Retribution;
import io.github.zhuiye486.cards.Master.Musketeer.AimedShot;
import io.github.zhuiye486.cards.Master.Musketeer.PatchUp;
import io.github.zhuiye486.cards.Master.Musketeer.SkeetShot;
import io.github.zhuiye486.cards.Master.Occultist.HandsFromTheAbyss;
import io.github.zhuiye486.cards.Master.Occultist.WeakeningCurse;
import io.github.zhuiye486.cards.Master.Occultist.WyrdReconstruction;
import io.github.zhuiye486.cards.Master.PlagueDoctor.BattlefieldMedicine;
import io.github.zhuiye486.cards.Master.PlagueDoctor.NoxiousBlast;
import io.github.zhuiye486.cards.Master.PlagueDoctor.PlagueGrenade;
import io.github.zhuiye486.cards.Master.ShieldBreaker.Captivate;
import io.github.zhuiye486.cards.Master.ShieldBreaker.Puncture;
import io.github.zhuiye486.cards.Master.ShieldBreaker.SerpentSway;
import io.github.zhuiye486.cards.Master.Vestal.DazzlingLight;
import io.github.zhuiye486.cards.Master.Vestal.DivineComfort;
import io.github.zhuiye486.cards.Master.Vestal.DivineGrace;
import io.github.zhuiye486.cards.NeverCompromise;
import io.github.zhuiye486.cards.OldRoad;
import io.github.zhuiye486.cards.RainInTheForest;
import io.github.zhuiye486.cards.Shovel;
import io.github.zhuiye486.cards.Strike;
import io.github.zhuiye486.cards.Virtue.Courageous;
import io.github.zhuiye486.cards.Virtue.Focused;
import io.github.zhuiye486.cards.Virtue.Powerful;
import io.github.zhuiye486.cards.Virtue.Stalwart;
import io.github.zhuiye486.cards.Virtue.Vigorous;
import io.github.zhuiye486.characters.Heroes;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.relics.PressureIndicator;
import io.github.zhuiye486.variables.Heal;
import io.github.zhuiye486.variables.SecondMagicNumber;

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
        BaseMod.addCard(new ShadowFade());
        BaseMod.addCard(new PoisonDart());
        BaseMod.addCard(new ToxinTrickery());
        BaseMod.addCard(new IronSawn());
        BaseMod.addCard(new BleedOut());
        BaseMod.addCard(new IfItBleeds());
        BaseMod.addCard(new DuelistsAdvance());
        BaseMod.addCard(new PointBlankShoot());
        BaseMod.addCard(new OpenVein());
        BaseMod.addCard(new HoundsRush());
        BaseMod.addCard(new CryHavoc());
        BaseMod.addCard(new HoundsHurry());
        BaseMod.addCard(new Hound());
        BaseMod.addCard(new Finale());
        BaseMod.addCard(new InspiringTune());
        BaseMod.addCard(new BattleBallad());
        BaseMod.addCard(new Withstand());
        BaseMod.addCard(new Revenge());
        BaseMod.addCard(new Intimidate());
        BaseMod.addCard(new Defender());
        BaseMod.addCard(new Bolster());
        BaseMod.addCard(new Retribution());
        BaseMod.addCard(new AimedShot());
        BaseMod.addCard(new PatchUp());
        BaseMod.addCard(new SkeetShot());
        BaseMod.addCard(new WeakeningCurse());
        BaseMod.addCard(new HandsFromTheAbyss());
        BaseMod.addCard(new WyrdReconstruction());
        BaseMod.addCard(new BattlefieldMedicine());
        BaseMod.addCard(new PlagueGrenade());
        BaseMod.addCard(new NoxiousBlast());
        BaseMod.addCard(new DivineComfort());
        BaseMod.addCard(new DivineGrace());
        BaseMod.addCard(new DazzlingLight());
        BaseMod.addCard(new FortifyingVapours());
        BaseMod.addCard(new InvigoratingVapours());
        BaseMod.addCard(new FesteringVapours());
        BaseMod.addCard(new Transform());
        BaseMod.addCard(new Slam());
        BaseMod.addCard(new LetsGoGambling());
        BaseMod.addCard(new Rake());
        BaseMod.addCard(new RainOfSorrows());
        BaseMod.addCard(new Reclaim());
        BaseMod.addCard(new Redeem());
        BaseMod.addCard(new Captivate());
        BaseMod.addCard(new Puncture());
        BaseMod.addCard(new SerpentSway());
        BaseMod.addCard(new Shovel());
        BaseMod.addCard(new Food());
        BaseMod.addCard(new HolyWater());
        BaseMod.addCard(new Irrational());
        BaseMod.addCard(new Paranoid());
        BaseMod.addCard(new Selfish());
        BaseMod.addCard(new Masochistic());
        BaseMod.addCard(new Hopeless());
        BaseMod.addCard(new Abusive());
        BaseMod.addCard(new Fearful());
        BaseMod.addCard(new Courageous());
        BaseMod.addCard(new Focused());
        BaseMod.addCard(new Powerful());
        BaseMod.addCard(new Stalwart());
        BaseMod.addCard(new Vigorous());
        BaseMod.addCard(new CriticalHit());
        BaseMod.addCard(new Dodge());
        BaseMod.addCard(new Escape());
        BaseMod.addCard(new FromBeyond());
        BaseMod.addCard(new Kleptomaniac());
        BaseMod.addCard(new LetsGoGambling());
        BaseMod.addCard(new NeverCompromise());
        BaseMod.addCard(new OldRoad());
        BaseMod.addCard(new RainInTheForest());
        BaseMod.addDynamicVariable(new Heal());
        BaseMod.addDynamicVariable(new SecondMagicNumber());
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