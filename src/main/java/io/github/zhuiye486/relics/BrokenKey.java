package io.github.zhuiye486.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;

import io.github.zhuiye486.helpers.ModHelper;

public class BrokenKey extends HeroRelic {
    public static final String ID = ModHelper.makePath(BrokenKey.class.getSimpleName());
    private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/BrokenKey.png";

    private int played = 0;

    public BrokenKey() {
        super(ID, IMG_PATH);
    }

    @Override
    public void atBattleStart() {
        played = 0;
    }

    @Override
    public void atTurnStart() {
        played = 0;
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
        played++;
        refreshHand();
    }

    private void refreshHand() {
        if (AbstractDungeon.player == null) {
            return;
        }
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            applyCost(c);
        }
    }

    private void applyCost(AbstractCard c) {
        if (c.cost < 0) {
            return;
        }
        boolean odd = (played + 1) % 2 == 1;
        int target = odd ? 0 : c.cost + 1;
        if (c.costForTurn != target) {
            c.setCostForTurn(target);
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new BrokenKey();
    }
}
