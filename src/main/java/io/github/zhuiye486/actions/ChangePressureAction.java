package io.github.zhuiye486.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.relics.PressureIndicator;

public class ChangePressureAction extends AbstractGameAction {
    private int pressureAmount;
    
    public ChangePressureAction(int amount) {
        this.pressureAmount = amount;
    }

    @Override
    public void update() {
        PressureIndicator relic = (PressureIndicator) AbstractDungeon.player.getRelic(
            ModHelper.makePath("PressureIndicator")
        );
        relic.changePressure(pressureAmount);

        this.isDone = true; 
    }
}
