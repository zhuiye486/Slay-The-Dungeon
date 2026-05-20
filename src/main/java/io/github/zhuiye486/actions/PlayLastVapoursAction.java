package io.github.zhuiye486.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import io.github.zhuiye486.patches.CardTagsEnum;

public class PlayLastVapoursAction extends AbstractGameAction {
    public PlayLastVapoursAction() {
        this.actionType = ActionType.USE;
    }

    @Override
    public void update() {
        AbstractCard lastVapoursCard = null;
        int count = 0;
        for (int i = AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 1; i >= 0; i--) {
            AbstractCard c = AbstractDungeon.actionManager.cardsPlayedThisCombat.get(i);
            if (c.hasTag(CardTagsEnum.Vapours)) {
                count += 1;
            }
            if (count == 2) {
                lastVapoursCard = c;
            } else if (count == 3) {
                lastVapoursCard = c;
                break;
            }
        }
        if (lastVapoursCard != null) {
            AbstractCard queuedVapoursCard = lastVapoursCard.makeStatEquivalentCopy();
            queuedVapoursCard.purgeOnUse = true;
            addToBot(
                new NewQueueCardAction(
                    queuedVapoursCard, 
                    true, 
                    true, 
                    true
                )
            );
        }
        this.isDone = true;
    }
}


