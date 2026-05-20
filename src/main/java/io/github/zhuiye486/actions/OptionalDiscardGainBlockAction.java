package io.github.zhuiye486.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class OptionalDiscardGainBlockAction extends AbstractGameAction {
    private final AbstractPlayer player;
    private final boolean endTurn;
    private final int blockPerDiscard;

    public OptionalDiscardGainBlockAction(AbstractPlayer player, int amount) {
        this(player, amount, false, 0);
    }

    public OptionalDiscardGainBlockAction(AbstractPlayer player, int amount, boolean endTurn) {
        this(player, amount, endTurn, 0);
    }

    public OptionalDiscardGainBlockAction(AbstractPlayer player, int amount, int blockPerDiscard) {
        this(player, amount, false, blockPerDiscard);
    }

    public OptionalDiscardGainBlockAction(AbstractPlayer player, int amount, boolean endTurn, int blockPerDiscard) {
        this.player = player;
        this.endTurn = endTurn;
        this.blockPerDiscard = blockPerDiscard;
        this.setValues(player, player, amount);
        this.actionType = ActionType.DISCARD;
        this.duration = Settings.ACTION_DUR_XFAST;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_XFAST) {
            if (this.player == null || this.player.hand == null || this.player.hand.isEmpty() || this.amount <= 0) {
                this.isDone = true;
                return;
            }

            AbstractDungeon.handCardSelectScreen.open(
                DiscardAction.TEXT[0],
                Math.min(this.amount, this.player.hand.size()),
                true,
                true
            );
            this.player.hand.applyPowers();
            this.tickDuration();
            return;
        }

        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            int discardedCards = AbstractDungeon.handCardSelectScreen.selectedCards.group.size();
            for (AbstractCard card : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                this.player.hand.moveToDiscardPile(card);
                if (!this.endTurn) {
                    card.triggerOnManualDiscard();
                }
                GameActionManager.incrementDiscard(this.endTurn);
            }

            if (discardedCards > 0 && this.blockPerDiscard > 0) {
                for (int i = 0; i < discardedCards; i++) {    
                    addToTop(new GainBlockAction(this.player, this.player, this.blockPerDiscard));
                }
            }

            AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            this.player.hand.applyPowers();
        }

        this.tickDuration();
    }
}