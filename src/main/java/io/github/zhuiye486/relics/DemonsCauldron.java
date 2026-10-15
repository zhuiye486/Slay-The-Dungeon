package io.github.zhuiye486.relics;

import com.megacrit.cardcrawl.relics.AbstractRelic;

import io.github.zhuiye486.actions.ChangeBrightnessAction;
import io.github.zhuiye486.helpers.ModHelper;

public class DemonsCauldron extends HeroRelic {
    public static final String ID = ModHelper.makePath(DemonsCauldron.class.getSimpleName());
    private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/DemonsCauldron.png";

    public DemonsCauldron() {
        super(ID, IMG_PATH);
    }

    @Override
    public void atTurnStart() {
        flash();
        addToBot(new ChangeBrightnessAction(-5));
    }

    @Override
    public AbstractRelic makeCopy() {
        return new DemonsCauldron();
    }
}
