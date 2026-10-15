package io.github.zhuiye486.relics;

import java.util.List;
import java.util.function.Supplier;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import basemod.abstracts.CustomRelic;

/** 每个职业对应的专属遗物基类，职业编号与 Heroes 中的顺序一致（1-18）。 */
public abstract class HeroRelic extends CustomRelic {
    @SuppressWarnings("unchecked")
    private static final Supplier<HeroRelic>[] FACTORIES = new Supplier[] {
        (Supplier<HeroRelic>) BrokenKey::new,
        (Supplier<HeroRelic>) FleetFlorin::new,
        (Supplier<HeroRelic>) BullsEyeBandana::new,
        (Supplier<HeroRelic>) HuntersTalons::new,
        (Supplier<HeroRelic>) HolyOrders::new,
        (Supplier<HeroRelic>) ResurrectionsCollar::new,
        (Supplier<HeroRelic>) LuckyTalisman::new,
        (Supplier<HeroRelic>) HellsHairpin::new,
        (Supplier<HeroRelic>) ScoutingWhistle::new,
        (Supplier<HeroRelic>) GunslingersBuckle::new,
        (Supplier<HeroRelic>) DirgeForTheDevoured::new,
        (Supplier<HeroRelic>) BerserkMask::new,
        (Supplier<HeroRelic>) RampartShield::new,
        (Supplier<HeroRelic>) WrathfulHat::new,
        (Supplier<HeroRelic>) DemonsCauldron::new,
        (Supplier<HeroRelic>) BlasphemousVial::new,
        (Supplier<HeroRelic>) VenomousVial::new,
        (Supplier<HeroRelic>) SacredScroll::new,
    };

    protected HeroRelic(String id, String imgPath) {
        super(id, ImageMaster.loadImage(imgPath), RelicTier.SPECIAL, LandingSound.FLAT);
    }

    public static int count() {
        return FACTORIES.length;
    }

    public static HeroRelic create(int hero) {
        return FACTORIES[hero - 1].get();
    }

    public static String idOf(int hero) {
        return create(hero).relicId;
    }

    /** 让玩家持有的职业遗物与当前队伍一致：移除不在队伍中的，补发缺少的。 */
    public static void sync(List<Integer> heroes) {
        AbstractPlayer player = AbstractDungeon.player;
        if (player == null || heroes == null) {
            return;
        }
        for (int hero = 1; hero <= count(); hero++) {
            String id = idOf(hero);
            boolean inParty = heroes.contains(hero);
            boolean owned = player.hasRelic(id);
            if (!inParty && owned) {
                player.loseRelic(id);
            } else if (inParty && !owned) {
                create(hero).instantObtain(player, player.relics.size(), false);
            }
        }
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public abstract AbstractRelic makeCopy();
}
