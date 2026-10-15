package io.github.zhuiye486.relics;

import com.megacrit.cardcrawl.relics.AbstractRelic;

import io.github.zhuiye486.helpers.ModHelper;

public class FleetFlorin extends HeroRelic {
    public static final String ID = ModHelper.makePath(FleetFlorin.class.getSimpleName());
    private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/FleetFlorin.png";
    public static final int BONUS_GOLD = 10;

    public FleetFlorin() {
        super(ID, IMG_PATH);
    }

    @Override
    public AbstractRelic makeCopy() {
        return new FleetFlorin();
    }
}
