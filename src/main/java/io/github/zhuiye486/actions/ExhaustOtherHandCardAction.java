package io.github.zhuiye486.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ExhaustOtherHandCardAction extends AbstractGameAction {
    private final AbstractPlayer player;
    private final AbstractCard excludedCard;
    private boolean selectionOpened;

    public ExhaustOtherHandCardAction(AbstractPlayer player, AbstractCard excludedCard) {
        this.player = player;
        this.excludedCard = excludedCard;
        this.actionType = ActionType.EXHAUST;
    }

    @Override
    public void update() {
        if (!this.selectionOpened) {
            CardGroup selectableCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard card : this.player.hand.group) {
                if (card != this.excludedCard) {
                    selectableCards.addToTop(card);
                }
            }

            if (selectableCards.isEmpty()) {
                this.isDone = true;
                return;
            }

            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            AbstractDungeon.gridSelectScreen.open(selectableCards, 1, "Select a card to Exhaust", false, false, false, false);
            this.selectionOpened = true;
            return;
        }

        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard selectedCard = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            if (this.player.hand.contains(selectedCard) && selectedCard != this.excludedCard) {
                this.player.hand.moveToExhaustPile(selectedCard);
                this.player.hand.applyPowers();
                this.player.hand.refreshHandLayout();
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            this.isDone = true;
        }
    }
}