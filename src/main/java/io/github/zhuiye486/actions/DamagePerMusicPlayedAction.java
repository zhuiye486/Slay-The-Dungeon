package io.github.zhuiye486.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import io.github.zhuiye486.patches.CardTagsEnum;

public class DamagePerMusicPlayedAction extends AbstractGameAction {
    private DamageInfo info;

    public DamagePerMusicPlayedAction(AbstractCreature target, DamageInfo info, AttackEffect effect) {
        this.info = info;
        setValues(target, info);
        this.actionType = ActionType.DAMAGE;
        this.attackEffect = effect;
    }

    public DamagePerMusicPlayedAction(AbstractCreature target, DamageInfo info) {
        this(target, info, AttackEffect.NONE);
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
                new DamageAction(
                    this.target, 
                    this.info, 
                    this.attackEffect
                )
            );
        }
        this.isDone = true;
    }
}
