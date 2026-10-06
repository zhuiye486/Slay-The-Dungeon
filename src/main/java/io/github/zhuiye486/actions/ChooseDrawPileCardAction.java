package io.github.zhuiye486.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ChooseDrawPileCardAction extends AbstractGameAction {
    private boolean selectionOpened;

    public ChooseDrawPileCardAction() {
        this.actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {
        if (!this.selectionOpened) {
            if (AbstractDungeon.player.drawPile.isEmpty()) {
                this.isDone = true;
                return;
            }

            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            AbstractDungeon.gridSelectScreen.open(
                AbstractDungeon.player.drawPile,
                1,
                "Choose a card to add to your hand at 0 cost",
                false,
                false,
                false,
                false
            );
            this.selectionOpened = true;
            return;
        }

        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard selectedCard = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.player.drawPile.moveToHand(selectedCard);
            selectedCard.setCostForTurn(0);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            this.isDone = true;
        }
    }
}