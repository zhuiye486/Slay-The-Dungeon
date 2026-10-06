package io.github.zhuiye486.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class CheckDrawAction extends AbstractGameAction {
    private final HealDrawUntilCostNotOneAction parent;
    private final int previousHandSize;

    public CheckDrawAction(HealDrawUntilCostNotOneAction parent, int previousHandSize) {
        this.parent = parent;
        this.previousHandSize = previousHandSize;
        this.actionType = ActionType.WAIT;
    }

    @Override
    public void update() {
        int currentHandSize = AbstractDungeon.player.hand.size();
        if (currentHandSize > previousHandSize) {
            // 有新牌，检查新抽的牌
            AbstractCard lastCard = AbstractDungeon.player.hand.getTopCard();
            if (lastCard.cost == 1 && parent.draws < parent.maxDraws && currentHandSize < 10) {
                // 继续抽牌
                int newPreviousSize = currentHandSize;
                AbstractDungeon.actionManager.addToBottom(new DrawCardAction(1));
                parent.draws++;
                AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.utility.WaitAction(Settings.ACTION_DUR_XFAST));
                AbstractDungeon.actionManager.addToBottom(new CheckDrawAction(parent, newPreviousSize));
            } else {
                // 停止，治疗
                AbstractDungeon.actionManager.addToBottom(new HealAction(parent.p, parent.p, parent.baseHeal + parent.draws));
                parent.complete();
            }
        } else {
            // 没有抽到牌，停止，治疗
            AbstractDungeon.actionManager.addToBottom(new HealAction(parent.p, parent.p, parent.baseHeal + parent.draws));
            parent.complete();
        }
        this.isDone = true;
    }
}