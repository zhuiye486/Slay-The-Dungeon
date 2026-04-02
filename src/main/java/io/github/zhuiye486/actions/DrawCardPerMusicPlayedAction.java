package io.github.zhuiye486.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import io.github.zhuiye486.patches.CardTagsEnum;

public class DrawCardPerMusicPlayedAction extends AbstractGameAction {
    public DrawCardPerMusicPlayedAction(int amount) {
        this.actionType = ActionType.DRAW;
        this.amount = amount;
    }

    @Override
    public void update() {
        int count = 0;
        for (AbstractCard c : AbstractDungeon.actionManager.cardsPlayedThisCombat) {
            if (c.hasTag(CardTagsEnum.Music)) {
                count++;
            }
        }
        count--;

        for (int i = 0; i < count; i++) {
            AbstractDungeon.actionManager.addToTop(
                new DrawCardAction(
                    AbstractDungeon.player, 
                    amount
                )
            );
        }
        this.isDone = true;
    }
}
