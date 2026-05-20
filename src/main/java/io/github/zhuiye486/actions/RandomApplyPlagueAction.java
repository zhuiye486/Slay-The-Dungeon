package io.github.zhuiye486.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import io.github.zhuiye486.powers.PlaguePower;

public class RandomApplyPlagueAction extends AbstractGameAction {
    public RandomApplyPlagueAction(int amount) {
        this.amount = amount;
        this.actionType = ActionType.DEBUFF;
    }

    @Override
    public void update() {
        addToTop(
            new ApplyPowerAction(
                AbstractDungeon.getRandomMonster(), 
                AbstractDungeon.player, 
                new PlaguePower( 
                    AbstractDungeon.getRandomMonster(), 
                    this.amount
                )
            )
        );
        this.isDone = true;
    }
}
