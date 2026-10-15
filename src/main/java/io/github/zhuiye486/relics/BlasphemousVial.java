package io.github.zhuiye486.relics;

import com.megacrit.cardcrawl.relics.AbstractRelic;

import io.github.zhuiye486.helpers.ModHelper;

/** 效果实现在 PlaguePower 中。 */
public class BlasphemousVial extends HeroRelic {
    public static final String ID = ModHelper.makePath(BlasphemousVial.class.getSimpleName());
    private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/BlasphemousVial.png";

    public BlasphemousVial() {
        super(ID, IMG_PATH);
    }

    @Override
    public AbstractRelic makeCopy() {
        return new BlasphemousVial();
    }
}
