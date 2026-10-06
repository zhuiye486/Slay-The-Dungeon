package io.github.zhuiye486.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class PickToTheFaceRemoveBlockAction extends AbstractGameAction {
    public PickToTheFaceRemoveBlockAction(AbstractMonster target) {
        this.target = target;
        this.actionType = ActionType.DAMAGE;
    }

    @Override
    public void update() {
        if (this.target != null && !this.target.isDeadOrEscaped()) {
            this.target.loseBlock(this.target.currentBlock);
        }
        this.isDone = true;
    }
}