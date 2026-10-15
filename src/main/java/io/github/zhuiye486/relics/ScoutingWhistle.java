package io.github.zhuiye486.relics;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import io.github.zhuiye486.cards.Hound;
import io.github.zhuiye486.helpers.ModHelper;

public class ScoutingWhistle extends HeroRelic {
    public static final String ID = ModHelper.makePath(ScoutingWhistle.class.getSimpleName());
    private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/ScoutingWhistle.png";

    public ScoutingWhistle() {
        super(ID, IMG_PATH);
    }

    @Override
    public void atTurnStart() {
        flash();
        addToBot(new MakeTempCardInHandAction(new Hound(), 1));
    }

    @Override
    public AbstractRelic makeCopy() {
        return new ScoutingWhistle();
    }
}
