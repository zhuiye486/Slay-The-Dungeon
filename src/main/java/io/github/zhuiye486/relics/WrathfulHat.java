package io.github.zhuiye486.relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageRandomEnemyAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import io.github.zhuiye486.helpers.ModHelper;

public class WrathfulHat extends HeroRelic {
    public static final String ID = ModHelper.makePath(WrathfulHat.class.getSimpleName());
    private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/WrathfulHat.png";

    public WrathfulHat() {
        super(ID, IMG_PATH);
    }

    @Override
    public void onPlayerEndTurn() {
        flash();
        for (int i = 0; i < 3; i++) {
            addToBot(new DamageRandomEnemyAction(
                new DamageInfo(AbstractDungeon.player, 2, DamageType.THORNS), AttackEffect.BLUNT_LIGHT));
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new WrathfulHat();
    }
}
