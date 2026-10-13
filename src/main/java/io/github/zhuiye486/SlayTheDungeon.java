package io.github.zhuiye486;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
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
import io.github.zhuiye486.cards.Ambush;
import io.github.zhuiye486.cards.Berserk;
import io.github.zhuiye486.cards.BloodInTheMud;
import io.github.zhuiye486.cards.CriticalHit;
import io.github.zhuiye486.cards.Dodge;
import io.github.zhuiye486.cards.DogTreat;
import io.github.zhuiye486.cards.Escape;
import io.github.zhuiye486.cards.Feminization;
import io.github.zhuiye486.cards.Food;
import io.github.zhuiye486.cards.FromBeyond;
import io.github.zhuiye486.cards.HolyWater;
import io.github.zhuiye486.cards.Hound;
import io.github.zhuiye486.cards.Kleptomaniac;
import io.github.zhuiye486.cards.LastDitchEffort;
import io.github.zhuiye486.cards.LetsGoGambling;
import io.github.zhuiye486.cards.Lucky;
import io.github.zhuiye486.cards.Master.Abomination.Rake;
import io.github.zhuiye486.cards.Master.Abomination.Slam;
import io.github.zhuiye486.cards.Master.Abomination.Transform;
import io.github.zhuiye486.cards.Master.Abomination.*;
import io.github.zhuiye486.cards.Master.Antiquarian.FesteringVapours;
import io.github.zhuiye486.cards.Master.Antiquarian.FortifyingVapours;
import io.github.zhuiye486.cards.Master.Antiquarian.InvigoratingVapours;
import io.github.zhuiye486.cards.Master.Antiquarian.*;
import io.github.zhuiye486.cards.Master.Arbalest.SniperShot;
import io.github.zhuiye486.cards.Master.Arbalest.SnipersMark;
import io.github.zhuiye486.cards.Master.Arbalest.SuppressingFire;
import io.github.zhuiye486.cards.Master.Arbalest.*;
import io.github.zhuiye486.cards.Master.BountyHunter.FinishHim;
import io.github.zhuiye486.cards.Master.BountyHunter.MarkForDeath;
import io.github.zhuiye486.cards.Master.BountyHunter.Uppercut;
import io.github.zhuiye486.cards.Master.BountyHunter.*;
import io.github.zhuiye486.cards.Master.Crusader.BulwarkOfFaith;
import io.github.zhuiye486.cards.Master.Crusader.InspiringCry;
import io.github.zhuiye486.cards.Master.Crusader.StunningBlow;
import io.github.zhuiye486.cards.Master.Crusader.*;
import io.github.zhuiye486.cards.Master.Flagellant.RainOfSorrows;
import io.github.zhuiye486.cards.Master.Flagellant.Reclaim;
import io.github.zhuiye486.cards.Master.Flagellant.Redeem;
import io.github.zhuiye486.cards.Master.Flagellant.*;
import io.github.zhuiye486.cards.Master.GraveRobber.PoisonDart;
import io.github.zhuiye486.cards.Master.GraveRobber.ShadowFade;
import io.github.zhuiye486.cards.Master.GraveRobber.ToxinTrickery;
import io.github.zhuiye486.cards.Master.GraveRobber.*;
import io.github.zhuiye486.cards.Master.Hellion.BleedOut;
import io.github.zhuiye486.cards.Master.Hellion.IfItBleeds;
import io.github.zhuiye486.cards.Master.Hellion.IronSawn;
import io.github.zhuiye486.cards.Master.Hellion.*;
import io.github.zhuiye486.cards.Master.HighWayMan.DuelistsAdvance;
import io.github.zhuiye486.cards.Master.HighWayMan.OpenVein;
import io.github.zhuiye486.cards.Master.HighWayMan.PointBlankShoot;
import io.github.zhuiye486.cards.Master.HighWayMan.*;
import io.github.zhuiye486.cards.Master.Houndmaster.CryHavoc;
import io.github.zhuiye486.cards.Master.Houndmaster.HoundsHurry;
import io.github.zhuiye486.cards.Master.Houndmaster.HoundsRush;
import io.github.zhuiye486.cards.Master.Houndmaster.*;
import io.github.zhuiye486.cards.Master.Jester.BattleBallad;
import io.github.zhuiye486.cards.Master.Jester.Finale;
import io.github.zhuiye486.cards.Master.Jester.InspiringTune;
import io.github.zhuiye486.cards.Master.Jester.*;
import io.github.zhuiye486.cards.Master.Leper.Intimidate;
import io.github.zhuiye486.cards.Master.Leper.Revenge;
import io.github.zhuiye486.cards.Master.Leper.Solemnity;
import io.github.zhuiye486.cards.Master.Leper.Withstand;
import io.github.zhuiye486.cards.Master.Leper.*;
import io.github.zhuiye486.cards.Master.ManAtArms.Bolster;
import io.github.zhuiye486.cards.Master.ManAtArms.Defender;
import io.github.zhuiye486.cards.Master.ManAtArms.Retribution;
import io.github.zhuiye486.cards.Master.ManAtArms.*;
import io.github.zhuiye486.cards.Master.Musketeer.AimedShot;
import io.github.zhuiye486.cards.Master.Musketeer.PatchUp;
import io.github.zhuiye486.cards.Master.Musketeer.SkeetShot;
import io.github.zhuiye486.cards.Master.Musketeer.*;
import io.github.zhuiye486.cards.Master.Occultist.HandsFromTheAbyss;
import io.github.zhuiye486.cards.Master.Occultist.WeakeningCurse;
import io.github.zhuiye486.cards.Master.Occultist.WyrdReconstruction;
import io.github.zhuiye486.cards.Master.Occultist.*;
import io.github.zhuiye486.cards.Master.PlagueDoctor.BattlefieldMedicine;
import io.github.zhuiye486.cards.Master.PlagueDoctor.NoxiousBlast;
import io.github.zhuiye486.cards.Master.PlagueDoctor.PlagueGrenade;
import io.github.zhuiye486.cards.Master.PlagueDoctor.*;
import io.github.zhuiye486.cards.Master.ShieldBreaker.Captivate;
import io.github.zhuiye486.cards.Master.ShieldBreaker.Puncture;
import io.github.zhuiye486.cards.Master.ShieldBreaker.SerpentSway;
import io.github.zhuiye486.cards.Master.ShieldBreaker.*;
import io.github.zhuiye486.cards.Master.Vestal.DazzlingLight;
import io.github.zhuiye486.cards.Master.Vestal.DivineComfort;
import io.github.zhuiye486.cards.Master.Vestal.DivineGrace;
import io.github.zhuiye486.cards.Master.Vestal.*;
import io.github.zhuiye486.cards.NeverCompromise;
import io.github.zhuiye486.cards.OldRoad;
import io.github.zhuiye486.cards.Pray;
import io.github.zhuiye486.cards.RainInTheForest;
import io.github.zhuiye486.cards.Shovel;
import io.github.zhuiye486.cards.Topspeed;
import io.github.zhuiye486.cards.Virtue.Courageous;
import io.github.zhuiye486.cards.Virtue.Focused;
import io.github.zhuiye486.cards.Virtue.Powerful;
import io.github.zhuiye486.cards.Virtue.Stalwart;
import io.github.zhuiye486.cards.Virtue.Vigorous;
import io.github.zhuiye486.characters.Heroes;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.relics.BrightnessIndicator;
import io.github.zhuiye486.relics.PressureIndicator;
import io.github.zhuiye486.variables.Heal;
import io.github.zhuiye486.variables.SecondMagicNumber;
import io.github.zhuiye486.variables.Chance;
import io.github.zhuiye486.variables.Chance2;

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
        BaseMod.addSaveField("SlayTheDungeonSelectedHeroes", new basemod.abstracts.CustomSavableRaw() {
            @Override
            public JsonElement onSaveRaw() {
                JsonArray array = new JsonArray();
                for (Integer hero : Heroes.selectedHeroes) {
                    array.add(new JsonPrimitive(hero));
                }
                return array;
            }

            @Override
            public void onLoadRaw(JsonElement json) {
                if (json == null || !json.isJsonArray()) {
                    Heroes.selectedHeroes = new ArrayList<>();
                    return;
                }
                JsonArray array = json.getAsJsonArray();
                ArrayList<Integer> loaded = new ArrayList<>();
                for (JsonElement element : array) {
                    if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
                        loaded.add(element.getAsInt());
                    }
                }
                Heroes.selectedHeroes = loaded;
                if (Heroes.selectedHeroes.size() == 4) {
                    Heroes.composeCharacterImage(Heroes.selectedHeroes);
                }
            }
        });
        BaseMod.addSaveField("SlayTheDungeonPressureTestTriggered", new basemod.abstracts.CustomSavableRaw() {
            @Override
            public JsonElement onSaveRaw() {
                return new JsonPrimitive(PressureIndicator.isTestTriggeredForSave());
            }

            @Override
            public void onLoadRaw(JsonElement json) {
                boolean triggered = json != null
                    && json.isJsonPrimitive()
                    && json.getAsJsonPrimitive().isBoolean()
                    && json.getAsBoolean();
                PressureIndicator.loadTestTriggered(triggered);
            }
        });
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
        BaseMod.addCard(new Smite());
        BaseMod.addCard(new ZealousAccusation());
        BaseMod.addCard(new BattleHeal());
        BaseMod.addCard(new HolyLance());
        BaseMod.addCard(new SacrificialStab());
        BaseMod.addCard(new AbyssalArtillery());
        BaseMod.addCard(new VulnerabilityHex());
        BaseMod.addCard(new DaemonsPull());
        BaseMod.addCard(new Manacles());
        BaseMod.addCard(new BeastBile());
        BaseMod.addCard(new Absolution());
        BaseMod.addCard(new Rage());
        BaseMod.addCard(new TargetWhistle());
        BaseMod.addCard(new GuardDog());
        BaseMod.addCard(new LickWounds());
        BaseMod.addCard(new Blackjack());
        BaseMod.addCard(new MaceBash());
        BaseMod.addCard(new Judgement());
        BaseMod.addCard(new Illumination());
        BaseMod.addCard(new HandOfLight());
        BaseMod.addCard(new DirkStab());
        BaseMod.addCard(new Harvest());
        BaseMod.addCard(new Solo());
        BaseMod.addCard(new SliceOff());
        BaseMod.addCard(new BlindingGas());
        BaseMod.addCard(new Incision());
        BaseMod.addCard(new EmboldeningVapours());
        BaseMod.addCard(new DisorientingBlast());
        BaseMod.addCard(new PickToTheFace());
        BaseMod.addCard(new Lunge());
        BaseMod.addCard(new FlashingDaggers());
        BaseMod.addCard(new ThrownDagger());
        BaseMod.addCard(new Bola());
        BaseMod.addCard(new Blindfire());
        BaseMod.addCard(new BattlefieldBandage());
        BaseMod.addCard(new RallyingFlare());
        BaseMod.addCard(new Punish());
        BaseMod.addCard(new Exsanguinate());
        BaseMod.addCard(new Endure());
        BaseMod.addCard(new Suffer());
        BaseMod.addCard(new WickedSlice());
        BaseMod.addCard(new PistolShot());
        BaseMod.addCard(new GrapeshotBlast());
        BaseMod.addCard(new TrackingShot());
        BaseMod.addCard(new Chop());
        BaseMod.addCard(new Hew());
        BaseMod.addCard(new Purge());
        BaseMod.addCard(new Pierce());
        BaseMod.addCard(new Impale());
        BaseMod.addCard(new AddersKiss());
        BaseMod.addCard(new Expose());
        BaseMod.addCard(new Crush());
        BaseMod.addCard(new Rampart());
        BaseMod.addCard(new Bellow());
        BaseMod.addCard(new Command());
        BaseMod.addCard(new WickedHack());
        BaseMod.addCard(new BarbaricYawp());
        BaseMod.addCard(new Breakthrough());
        BaseMod.addCard(new AdrenalineRush());
        BaseMod.addCard(new CollectBounty());
        BaseMod.addCard(new ComeHither());
        BaseMod.addCard(new Flashbang());
        BaseMod.addCard(new Caltrops());
        BaseMod.addCard(new NervousStab());
        BaseMod.addCard(new GetDown());
        BaseMod.addCard(new Flashpowder());
        BaseMod.addCard(new ProtectMe());
        BaseMod.addCard(new Smokescreen());
        BaseMod.addCard(new CallTheShot());
        BaseMod.addCard(new Buckshot());
        BaseMod.addCard(new Sidearm());
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
        BaseMod.addCard(new Solemnity());
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
        BaseMod.addCard(new Lucky());
        BaseMod.addCard(new BloodInTheMud());
        BaseMod.addCard(new LastDitchEffort());
        BaseMod.addCard(new Ambush());
        BaseMod.addCard(new DogTreat());
        BaseMod.addCard(new Topspeed());
        BaseMod.addCard(new Pray());
        BaseMod.addCard(new Feminization());
        BaseMod.addCard(new Berserk());
        BaseMod.addDynamicVariable(new Heal());
        BaseMod.addDynamicVariable(new SecondMagicNumber());
        BaseMod.addDynamicVariable(new Chance());
        BaseMod.addDynamicVariable(new Chance2());
    }

    @Override
    public void receiveEditRelics() {
        BaseMod.addRelic(new PressureIndicator(), RelicType.SHARED);
        BaseMod.addRelic(new BrightnessIndicator(), RelicType.SHARED);
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

    @SpirePatch(clz = com.megacrit.cardcrawl.dungeons.AbstractDungeon.class, method = "initializeCardPools")
    public static class FilterMasterCardPoolsPatch {
        @SpirePostfixPatch
        public static void Postfix() {
            Heroes.filterCardPoolsForSelection();
        }
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