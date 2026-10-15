package io.github.zhuiye486.relics;

import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import io.github.zhuiye486.helpers.ModHelper;

public class ResurrectionsCollar extends HeroRelic {
    public static final String ID = ModHelper.makePath(ResurrectionsCollar.class.getSimpleName());
    private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/ResurrectionsCollar.png";

    public ResurrectionsCollar() {
        super(ID, IMG_PATH);
    }

    @Override
    public void atBattleStart() {
        this.counter = 0;
    }

    @Override
    public void atTurnStart() {
        this.counter++;
        if (this.counter == 3) {
            flash();
            addToBot(new HealAction(AbstractDungeon.player, AbstractDungeon.player, 10));
        } else if (this.counter == 5) {
            flash();
            addToBot(new LoseHPAction(AbstractDungeon.player, AbstractDungeon.player, 8, AttackEffect.FIRE));
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new ResurrectionsCollar();
    }
}
