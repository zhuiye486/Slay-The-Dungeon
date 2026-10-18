package io.github.zhuiye486.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;

import io.github.zhuiye486.helpers.ModHelper;

public class BrokenKey extends HeroRelic {
    public static final String ID = ModHelper.makePath(BrokenKey.class.getSimpleName());
    private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/BrokenKey.png";

    public BrokenKey() {
        super(ID, IMG_PATH);
        this.counter = 1;
    }

    @Override
    public void atBattleStart() {
        this.counter = 1;
    }

    @Override
    public void atTurnStart() {
        this.counter = 1;
    }

    @Override
    public void atTurnStartPostDraw() {
        refreshHand();
    }

    @Override
    public void onCardDraw(AbstractCard drawnCard) {
        applyCost(drawnCard);
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.purgeOnUse) {
            return;
        }
        if (this.counter == 1) {
            this.counter = 2;
        } else {
            this.counter = 1;
        }
        refreshHand();
    }

    private void refreshHand() {
        if (AbstractDungeon.player == null) {
            return;
        }
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (!isQueued(c)) {
                applyCost(c);
            }
        }
    }

    private boolean isQueued(AbstractCard card) {
        if (AbstractDungeon.actionManager == null) {
            return false;
        }
        for (CardQueueItem queueItem : AbstractDungeon.actionManager.cardQueue) {
            if (queueItem.card != null && queueItem.card == card) {
                return true;
            }
        }
        return false;
    }

    private void applyCost(AbstractCard c) {
        if (c.cost < 0) {
            return;
        }
        int target;
        if (this.counter == 1) {
            target = 0;
        } else {
            target = c.cost + 1;
        }
        if (c.costForTurn != target) {
            c.setCostForTurn(target);
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new BrokenKey();
    }
}
