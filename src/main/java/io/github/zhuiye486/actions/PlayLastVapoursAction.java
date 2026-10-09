package io.github.zhuiye486.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import io.github.zhuiye486.patches.CardTagsEnum;

public class PlayLastVapoursAction extends AbstractGameAction {
    private final AbstractCard sourceCard;

    public PlayLastVapoursAction() {
        this(null);
    }

    public PlayLastVapoursAction(AbstractCard sourceCard) {
        this.actionType = ActionType.USE;
        this.sourceCard = sourceCard;
    }

    @Override
    public void update() {
        boolean skippedSourceCard = false;
        for (int i = AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 1; i >= 0; i--) {
            AbstractCard c = AbstractDungeon.actionManager.cardsPlayedThisCombat.get(i);
            if (!skippedSourceCard && c == sourceCard) {
                skippedSourceCard = true;
                continue;
            }
            if (!c.purgeOnUse && c.hasTag(CardTagsEnum.Vapours)) {
                AbstractCard queuedVapoursCard = c.makeStatEquivalentCopy();
                queuedVapoursCard.purgeOnUse = true;
                addToBot(
                    new NewQueueCardAction(
                        queuedVapoursCard,
                        true,
                        true,
                        true
                    )
                );
                break;
            }
        }
        this.isDone = true;
    }
}


