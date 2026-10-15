package io.github.zhuiye486.relics;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import io.github.zhuiye486.helpers.ModHelper;

public class RampartShield extends HeroRelic {
    public static final String ID = ModHelper.makePath(RampartShield.class.getSimpleName());
    private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/RampartShield.png";

    public RampartShield() {
        super(ID, IMG_PATH);
    }

    @Override
    public void atBattleStart() {
        this.counter = 0;
    }

    @Override
    public void onPlayerEndTurn() {
        this.counter++;
        if (this.counter == 3) {
            flash();
            addToBot(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, 10));
            this.counter = 0;
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new RampartShield();
    }
}
