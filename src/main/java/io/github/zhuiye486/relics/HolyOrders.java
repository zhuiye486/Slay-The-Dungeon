package io.github.zhuiye486.relics;

import com.megacrit.cardcrawl.relics.AbstractRelic;

import io.github.zhuiye486.helpers.ModHelper;

public class HolyOrders extends HeroRelic {
    public static final String ID = ModHelper.makePath(HolyOrders.class.getSimpleName());
    private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/HolyOrders.png";
    public static final int BONUS_MAX_BRIGHTNESS = 20;

    public HolyOrders() {
        super(ID, IMG_PATH);
    }

    @Override
    public AbstractRelic makeCopy() {
        return new HolyOrders();
    }
}
