package io.github.zhuiye486.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import io.github.zhuiye486.patches.CardTagsEnum;

public class DamageAllEnemyPerMusicPlayedAction extends AbstractGameAction{
    public int[] multiDamage;
    private DamageInfo info;
    private AbstractCard sourceCard;
    
    public DamageAllEnemyPerMusicPlayedAction(DamageInfo info, int[] multiDamage, AttackEffect effect) {
        this.multiDamage = multiDamage;
        this.info = info;
        this.actionType = ActionType.DAMAGE;
        this.attackEffect = effect;
    }

    public DamageAllEnemyPerMusicPlayedAction(DamageInfo info, int[] multiDamage) {
        this(info, multiDamage, AttackEffect.NONE);
    }

    public DamageAllEnemyPerMusicPlayedAction(DamageInfo info, int[] multiDamage, AbstractCard sourceCard) {
        this(info, multiDamage, AttackEffect.NONE);
        this.sourceCard = sourceCard;
    }

    @Override
    public void update() {
        int count = 0;
        for (AbstractCard c : AbstractDungeon.actionManager.cardsPlayedThisCombat) {
            if (c != sourceCard && c.hasTag(CardTagsEnum.Music)) {
                count++;
            }
        }

        for (int i = 0; i < count; i++) {
            AbstractDungeon.actionManager.addToTop(
                new DamageAllEnemiesAction(
                    info.owner, 
                    this.multiDamage, 
                    info.type,
                    this.attackEffect
                )
            );
        }
        this.isDone = true;
    }
}
