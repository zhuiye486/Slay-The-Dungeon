package io.github.zhuiye486.relics;

import com.megacrit.cardcrawl.relics.AbstractRelic;

import io.github.zhuiye486.actions.ChangeBrightnessAction;
import io.github.zhuiye486.helpers.ModHelper;

public class SacredScroll extends HeroRelic {
    public static final String ID = ModHelper.makePath(SacredScroll.class.getSimpleName());
    private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/SacredScroll.png";

    public SacredScroll() {
        super(ID, IMG_PATH);
    }

    @Override
    public void atTurnStart() {
        flash();
        addToBot(new ChangeBrightnessAction(5));
    }

    @Override
    public AbstractRelic makeCopy() {
        return new SacredScroll();
    }
}
