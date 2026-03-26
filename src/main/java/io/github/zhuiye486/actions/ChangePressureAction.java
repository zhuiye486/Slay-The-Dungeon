package io.github.zhuiye486.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import io.github.zhuiye486.helpers.ModHelper;

public class ChangePressureAction extends AbstractGameAction {
    private int pressureAmount;
    
    public ChangePressureAction(int amount) {
        this.pressureAmount = amount;
    }

    @Override
    public void update() {
        AbstractDungeon.player.getRelic(ModHelper.makePath("PressureIndicator")).counter += pressureAmount;
        this.isDone = true; 
    }
}
