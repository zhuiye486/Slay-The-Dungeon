package io.github.zhuiye486.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import io.github.zhuiye486.cards.AbstractDungeonCard;

public class ExperiencedAction extends AbstractGameAction {
    private AbstractDungeonCard card;

    public ExperiencedAction(AbstractDungeonCard card, int amount) {
        this.card = card;
        this.amount = amount;
    }

    @Override
    public void update() {
        this.card.baseDamage += this.amount;
        this.card.applyPowers();
        for (AbstractCard c : AbstractDungeon.player.discardPile.group) {
            if (c instanceof  io.github.zhuiye486.cards.OldRoad) {
                c.baseDamage += this.amount;
                c.applyPowers();
            }
        }
        for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
            if (c instanceof  io.github.zhuiye486.cards.OldRoad) {
                c.baseDamage += this.amount;
                c.applyPowers();
            }
        }
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (c instanceof  io.github.zhuiye486.cards.OldRoad) {
                c.baseDamage += this.amount;
                c.applyPowers();
            }
        }
        this.isDone = true;
    }
}
