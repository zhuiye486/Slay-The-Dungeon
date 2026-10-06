package io.github.zhuiye486.characters;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.cutscenes.CutscenePanel;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.Vampires;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.screens.CharSelectInfo;

import basemod.abstracts.CustomPlayer;
import io.github.zhuiye486.SlayTheDungeon;
import io.github.zhuiye486.cards.Strike;
import io.github.zhuiye486.relics.BrightnessIndicator;
import io.github.zhuiye486.relics.PressureIndicator;

// 继承CustomPlayer类
public class Heroes extends CustomPlayer {
    // 静态字段存储选择的英雄
    public static ArrayList<Integer> selectedHeroes = new ArrayList<>();
    // 静态实例引用
    public static Heroes instance = null;
    public static Texture composedCorpseTexture = null;
    // 火堆的人物立绘（行动前）
    private static final String CHARACTER_SHOULDER_1 = "SlayTheDungeonResources/img/char/shoulder1.png";
    // 火堆的人物立绘（行动后）
    private static final String CHARACTER_SHOULDER_2 = "SlayTheDungeonResources/img/char/shoulder2.png";
    // 人物死亡图像
    private static final String CORPSE = "SlayTheDungeonResources/img/char/Corpse.png";
    // 战斗界面左下角能量图标的每个图层
    private static final String[] ORB_TEXTURES = new String[]{
            "SlayTheDungeonResources/img/UI/orb/layer5.png",
            "SlayTheDungeonResources/img/UI/orb/layer4.png",
            "SlayTheDungeonResources/img/UI/orb/layer3.png",
            "SlayTheDungeonResources/img/UI/orb/layer2.png",
            "SlayTheDungeonResources/img/UI/orb/layer1.png",
            "SlayTheDungeonResources/img/UI/orb/layer_locker.png",
            "SlayTheDungeonResources/img/UI/orb/layer5d.png",
            "SlayTheDungeonResources/img/UI/orb/layer4d.png",
            "SlayTheDungeonResources/img/UI/orb/layer3d.png",
            "SlayTheDungeonResources/img/UI/orb/layer2d.png",
            "SlayTheDungeonResources/img/UI/orb/layer1d.png"
    };
    // 每个图层的旋转速度
    private static final float[] LAYER_SPEED = new float[]{-40.0F, -32.0F, 20.0F, -20.0F, 0.0F, -10.0F, -8.0F, 5.0F, -5.0F, 0.0F};
    // 人物的本地化文本，如卡牌的本地化文本一样，如何书写见下
    private static final CharacterStrings characterStrings = CardCrawlGame.languagePack.getCharacterString("SlayTheDungeon:Heros");
    private static final String[] heroNames = new String[]{
        "Abomination",
        "Antiquarian",
        "Arbalest",
        "Bounty_Hunter",
        "Crusader",
        "Flagellant",
        "Grave_Robber",
        "Hellion",
        "Houndmaster",
        "Highwayman",
        "Jester",
        "Leper",
        "Man_At_Arms",
        "Musketeer",
        "Occultist",
        "Plague_Doctor",
        "Shieldbreaker",
        "Vestal",
    };

    private static Texture composeHeroTexture(java.util.List<Integer> heroes, String suffix) {
        if (heroes == null || heroes.size() != 4) {
            System.out.println("Invalid heroes list for composition.");
            return null;
        }

        Pixmap[] heroPixmaps = new Pixmap[4];
        int totalWidth = 0;
        int height = 0;
        float heroImageScale = 0.5f; // 0.5f 表示缩小到 50%
        int[] scaledWidths = new int[4];
        int[] scaledHeights = new int[4];

        // Load hero images and calculate scaled sizes
        for (int i = 0; i < 4; i++) {
            int heroIndex = heroes.get(i) - 1; // 0-based
            String heroName = heroNames[heroIndex];
            String path = "SlayTheDungeonResources/img/char/" + heroName + suffix;
            try {
                Texture texture = new Texture(Gdx.files.internal(path));
                texture.getTextureData().prepare();
                heroPixmaps[i] = texture.getTextureData().consumePixmap();
                scaledWidths[i] = Math.max(1, (int) (heroPixmaps[i].getWidth() * heroImageScale));
                scaledHeights[i] = Math.max(1, (int) (heroPixmaps[i].getHeight() * heroImageScale));
                totalWidth += scaledWidths[i];
                height = Math.max(height, scaledHeights[i]);
            } catch (Exception e) {
                System.out.println("Failed to load hero image: " + path);
                e.printStackTrace();
                for (Pixmap pix : heroPixmaps) {
                    if (pix != null) {
                        pix.dispose();
                    }
                }
                return null;
            }
        }

        Pixmap composed = new Pixmap(totalWidth, height, Pixmap.Format.RGBA8888);
        int currentX = 0;
        for (int i = 0; i < heroPixmaps.length; i++) {
            Pixmap pix = heroPixmaps[i];
            int targetWidth = scaledWidths[i];
            int targetHeight = scaledHeights[i];
            int targetY = height - targetHeight; // bottom align
            composed.drawPixmap(pix, 0, 0, pix.getWidth(), pix.getHeight(), currentX, targetY, targetWidth, targetHeight);
            currentX += targetWidth;
            pix.dispose();
        }

        Texture composedTexture = new Texture(composed);
        composed.dispose();
        return composedTexture;
    }

    public static void composeCharacterImage(java.util.List<Integer> heroes) {
        if (instance == null) {
            System.out.println("composeCharacterImage skipped because Heroes.instance is null.");
            return;
        }

        Texture composedTexture = composeHeroTexture(heroes, ".png");
        if (composedTexture == null) {
            return;
        }
        System.out.println("Composed hero texture created.");
        if (instance.img != null) {
            instance.img.dispose();
        }
        instance.img = composedTexture;
        System.out.println("Composed texture set to player img.");
    }

    public static Texture composeCorpseImage(java.util.List<Integer> heroes) {
        if (heroes == null || heroes.size() != 4) {
            System.out.println("Invalid heroes list for corpse image composition.");
            return null;
        }
        if (composedCorpseTexture != null) {
            composedCorpseTexture.dispose();
            composedCorpseTexture = null;
        }
        composedCorpseTexture = composeHeroTexture(heroes, "_dead.png");
        if (composedCorpseTexture != null) {
            System.out.println("Composed corpse texture created.");
            if (instance != null) {
                instance.corpseImg = composedCorpseTexture;
            }
        }
        return composedCorpseTexture;
    }

    private static void refreshCorpseImage() {
        if (instance != null && selectedHeroes.size() == 4) {
            Texture corpseTexture = composeCorpseImage(selectedHeroes);
            if (corpseTexture != null) {
                instance.corpseImg = corpseTexture;
            }
        }
    }

    public Heroes(String name, PlayerClass setClass) {
        super(name, setClass, ORB_TEXTURES, null, LAYER_SPEED, null, null);
        instance = this;

        // 人物对话气泡的大小，如果游戏中尺寸不对在这里修改（libgdx的坐标轴左下为原点）
        this.dialogX = (this.drawX + 0.0F * Settings.scale);
        this.dialogY = (this.drawY + 150.0F * Settings.scale);


        // 初始化你的人物，如果你的人物只有一张图，那么第一个参数填写你人物图片的路径。
        String characterImage = getCharacterImagePath();
        this.initializeClass(
                characterImage, // 人物图片
                CHARACTER_SHOULDER_2, CHARACTER_SHOULDER_1,
                CORPSE, // 人物死亡图像
                this.getLoadout(),
                0.0F, 0.0F,
                200.0F, 220.0F, // 人物碰撞箱大小，越大的人物模型这个越大
                new EnergyManager(3) // 初始每回合的能量
        );

        if (selectedHeroes != null && selectedHeroes.size() == 4) {
            composeCharacterImage(selectedHeroes);
            refreshCorpseImage();
        }

        // 如果你的人物没有动画，那么这些不需要写
        // this.loadAnimation("ExampleModResources/img/char/character.atlas", "ExampleModResources/img/char/character.json", 1.8F);
        // AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        // e.setTime(e.getEndTime() * MathUtils.random());
        // e.setTimeScale(1.2F);


    }

    private String getCharacterImagePath() {
        if (selectedHeroes != null && !selectedHeroes.isEmpty()) {
            int primaryHero = selectedHeroes.get(0);
            String heroPath = "SlayTheDungeonResources/img/char/" + heroNames[primaryHero - 1] + ".png";
            System.out.println("Using hero image: " + heroPath);
            return heroPath;
        } else {
            String defaultPath = "SlayTheDungeonResources/img/char/Stage_Coach.png";
            System.out.println("Using default image: " + defaultPath);
            return defaultPath;
        }
    }

    // 初始卡组的ID，可直接写或引用变量
    @Override
    public ArrayList<String> getStartingDeck() {
        ArrayList<String> retVal = new ArrayList<>();
        retVal.add("SlayTheDungeon:Shovel");
        retVal.add("SlayTheDungeon:Shovel");
        retVal.add("SlayTheDungeon:Shovel");
        retVal.add("SlayTheDungeon:HolyWater");
        retVal.add("SlayTheDungeon:HolyWater");
        retVal.add("SlayTheDungeon:Food");
        return retVal;
    }

    public static ArrayList<String> buildStartingDeck(List<Integer> heroes) {
        ArrayList<String> retVal = new ArrayList<>();

        for (Integer hero : heroes) {
            switch (hero) {
                case 1:
                    retVal.add("SlayTheDungeon:Transform");
                    retVal.add("SlayTheDungeon:Rake");
                    retVal.add("SlayTheDungeon:Slam");
                    break;
                case 2:
                    retVal.add("SlayTheDungeon:FesteringVapours");
                    retVal.add("SlayTheDungeon:FortifyingVapours");
                    retVal.add("SlayTheDungeon:InvigoratingVapours");
                    break;
                case 3:
                    retVal.add("SlayTheDungeon:SniperShot");
                    retVal.add("SlayTheDungeon:SnipersMark");
                    retVal.add("SlayTheDungeon:SuppressingFire");
                    break;
                case 4:
                    retVal.add("SlayTheDungeon:FinishHim");
                    retVal.add("SlayTheDungeon:MarkForDeath");
                    retVal.add("SlayTheDungeon:Uppercut");
                    break;
                case 5:
                    retVal.add("SlayTheDungeon:BulwarkOfFaith");
                    retVal.add("SlayTheDungeon:InspiringCry");
                    retVal.add("SlayTheDungeon:StunningBlow");
                    break;
                case 6:
                    retVal.add("SlayTheDungeon:RainOfSorrows");
                    retVal.add("SlayTheDungeon:Reclaim");
                    retVal.add("SlayTheDungeon:Redeem");
                    break;
                case 7:
                    retVal.add("SlayTheDungeon:ShadowFade");
                    retVal.add("SlayTheDungeon:PoisonDart");
                    retVal.add("SlayTheDungeon:ToxinTrickery");
                    break;
                case 8:
                    retVal.add("SlayTheDungeon:IronSawn");
                    retVal.add("SlayTheDungeon:BleedOut");
                    retVal.add("SlayTheDungeon:IfItBleeds");
                    break;
                case 9:
                    retVal.add("SlayTheDungeon:DuelistsAdvance");
                    retVal.add("SlayTheDungeon:OpenVein");
                    retVal.add("SlayTheDungeon:PointBlankShoot");
                    break;
                case 10:
                    retVal.add("SlayTheDungeon:CryHavoc");
                    retVal.add("SlayTheDungeon:HoundsHurry");
                    retVal.add("SlayTheDungeon:HoundsRush");
                    break;
                case 11:
                    retVal.add("SlayTheDungeon:Finale");
                    retVal.add("SlayTheDungeon:BattleBallad");
                    retVal.add("SlayTheDungeon:InspiringTune");
                    break;
                case 12:
                    retVal.add("SlayTheDungeon:Intimidate");
                    retVal.add("SlayTheDungeon:Revenge");
                    retVal.add("SlayTheDungeon:Withstand");
                    break;
                case 13:
                    retVal.add("SlayTheDungeon:Bolster");
                    retVal.add("SlayTheDungeon:Defender");
                    retVal.add("SlayTheDungeon:Retribution");
                    break;
                case 14:
                    retVal.add("SlayTheDungeon:AimedShot");
                    retVal.add("SlayTheDungeon:PatchUp");
                    retVal.add("SlayTheDungeon:SkeetShot");
                    break;
                case 15:
                    retVal.add("SlayTheDungeon:HandsFromTheAbyss");
                    retVal.add("SlayTheDungeon:WeakeningCurse");
                    retVal.add("SlayTheDungeon:WyrdReconstruction");
                    break;
                case 16:
                    retVal.add("SlayTheDungeon:PlagueGrenade");
                    retVal.add("SlayTheDungeon:NoxiousBlast");
                    retVal.add("SlayTheDungeon:BattlefieldMedicine");
                    break;
                case 17:
                    retVal.add("SlayTheDungeon:Captivate");
                    retVal.add("SlayTheDungeon:Puncture");
                    retVal.add("SlayTheDungeon:SerpentSway");
                    break;
                case 18:
                    retVal.add("SlayTheDungeon:DazzlingLight");
                    retVal.add("SlayTheDungeon:DivineGrace");
                    retVal.add("SlayTheDungeon:DivineComfort");
                    break;
                default:
                    break;
            }       
        }

        return retVal;
    }

    public static void updateStartingDeckForSelection(List<Integer> heroes) {
        selectedHeroes = new ArrayList<>(heroes);

        if (!(AbstractDungeon.player instanceof Heroes)) {
            return;
        }

        composeCharacterImage(selectedHeroes);
        refreshCorpseImage();

        for (String cardId : buildStartingDeck(selectedHeroes)) {
            AbstractDungeon.player.masterDeck.addToBottom(CardLibrary.getCopy(cardId));
        }
    }

    // 初始遗物的ID，可以先写个原版遗物凑数
    @Override
    public ArrayList<String> getStartingRelics() {
        ArrayList<String> retVal = new ArrayList<>();
        retVal.add(PressureIndicator.ID);
        retVal.add(BrightnessIndicator.ID);

        return retVal;
    }

    @Override
    public CharSelectInfo getLoadout() {
        return new CharSelectInfo(
                characterStrings.NAMES[0], // 人物名字
                characterStrings.TEXT[0], // 人物介绍
                75, // 当前血量
                75, // 最大血量
                0, // 初始充能球栏位
                99, // 初始携带金币
                5, // 每回合抽牌数量
                this, // 别动
                this.getStartingRelics(), // 初始遗物
                this.getStartingDeck(), // 初始卡组
                false // 别动
        );
    }

    // 人物名字（出现在游戏左上角）
    @Override
    public String getTitle(PlayerClass playerClass) {
        return "英雄们";
    }

    // 你的卡牌颜色（这个枚举在最下方创建）
    @Override
    public AbstractCard.CardColor getCardColor() {
        return SlayTheDungeonColorEnum.DEEP_RED;
    }

    // 翻牌事件出现的你的职业牌（一般设为打击）
    @Override
    public AbstractCard getStartCardForEvent() {
        return new Strike();
    }

    // 卡牌轨迹颜色（和CardColor不是同一个类。使用com.badlogic.gdx.graphics.Color）
    @Override
    public Color getCardTrailColor() {
        return SlayTheDungeon.SLAY_THE_DUNGEON_COLOR;
    }

    // 高进阶带来的生命值损失
    @Override
    public int getAscensionMaxHPLoss() {
        return 5;
    }

    // 卡牌的能量字体，没必要修改
    @Override
    public BitmapFont getEnergyNumFont() {
        return FontHelper.energyNumFontBlue;
    }

    // 人物选择界面点击你的人物按钮时触发的方法，这里为屏幕轻微震动
    @Override
    public void doCharSelectScreenSelectEffect() {
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, false);
    }

    // 碎心图片
    @Override
    public ArrayList<CutscenePanel> getCutscenePanels() {
        ArrayList<CutscenePanel> panels = new ArrayList<>();
        // 有两个参数的，第二个参数表示出现图片时播放的音效
        panels.add(new CutscenePanel("SlayTheDungeonResources/img/char/Victory1.png", "ATTACK_MAGIC_FAST_1"));
        panels.add(new CutscenePanel("SlayTheDungeonResources/img/char/Victory2.png"));
        panels.add(new CutscenePanel("SlayTheDungeonResources/img/char/Victory3.png"));
        return panels;
    }

    // 自定义模式选择你的人物时播放的音效
    @Override
    public String getCustomModeCharacterButtonSoundKey() {
        return "ATTACK_HEAVY";
    }

    // 游戏中左上角显示在你的名字之后的人物名称
    @Override
    public String getLocalizedCharacterName() {
        return characterStrings.NAMES[0];
    }

    // 创建人物实例，照抄，记得替换MyCharacter为你的类名
    @Override
    public AbstractPlayer newInstance() {
        return new Heroes(this.name, SlayTheDungeonColorEnum.Heroes);
    }

    // 第三章面对心脏说的话（例如战士是“你握紧了你的长刀……”之类的）
    @Override
    public String getSpireHeartText() {
        return characterStrings.TEXT[1];
    }

    // 打心脏的颜色，不是很明显（和CardColor不是同一个类。使用com.badlogic.gdx.graphics.Color）
    @Override
    public Color getSlashAttackColor() {
        return SlayTheDungeon.SLAY_THE_DUNGEON_COLOR;
    }

    // 吸血鬼事件文本，主要是他（索引为0）和她（索引为1）的区别（机器人另外）
    @Override
    public String getVampireText() {
        return Vampires.DESCRIPTIONS[0];
    }

    // 卡牌选择界面选择该牌的颜色（和CardColor不是同一个类。使用com.badlogic.gdx.graphics.Color）
    @Override
    public Color getCardRenderColor() {
        return SlayTheDungeon.SLAY_THE_DUNGEON_COLOR;
    }

    // 第三章面对心脏造成伤害时的特效
    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[]{AbstractGameAction.AttackEffect.SLASH_HEAVY, AbstractGameAction.AttackEffect.FIRE, AbstractGameAction.AttackEffect.SLASH_DIAGONAL, AbstractGameAction.AttackEffect.SLASH_HEAVY, AbstractGameAction.AttackEffect.FIRE, AbstractGameAction.AttackEffect.SLASH_DIAGONAL};
    }

    // Save and Load methods for selectedHeroes
    public static void saveSelectedHeroes() {
        // 保存时通过 BaseMod.addSaveField 统一处理，无需本地实现。
    }

    public static void loadSelectedHeroes(List<Integer> heroes) {
        if (heroes == null || heroes.isEmpty()) {
            selectedHeroes = new ArrayList<>();
            return;
        }
        selectedHeroes = new ArrayList<>(heroes);
        if (instance != null) {
            composeCharacterImage(selectedHeroes);
            refreshCorpseImage();
        }
    }

    // 以下为原版人物枚举、卡牌颜色枚举扩展的枚举，需要写，接下来要用

    // 注意此处是在 MyCharacter 类内部的静态嵌套类中定义的新枚举值
    // 不可将该定义放在外部的 MyCharacter 类中，具体原因见《高级技巧 / 01 - Patch / SpireEnum》
    public static class SlayTheDungeonColorEnum {
        // 修改为你的颜色名称，确保不会与其他mod冲突
        @SpireEnum
        public static PlayerClass Heroes;

        // ***将CardColor和LibraryType的变量名改为你的角色的颜色名称，确保不会与其他mod冲突***
        // ***并且名称需要一致！***
        @SpireEnum
        public static AbstractCard.CardColor DEEP_RED;
    }

    public static class PlayerLibraryEnum {
        // ***将CardColor和LibraryType的变量名改为你的角色的颜色名称，确保不会与其他mod冲突***
        // ***并且名称需要一致！***

        // 这个变量未被使用（呈现灰色）是正常的
        @SpireEnum
        public static CardLibrary.LibraryType DEEP_RED;
    }
}
