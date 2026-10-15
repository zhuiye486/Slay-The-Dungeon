package io.github.zhuiye486.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.powers.MarkPower;

public class BullsEyeBandana extends HeroRelic {
    public static final String ID = ModHelper.makePath(BullsEyeBandana.class.getSimpleName());
    private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/BullsEyeBandana.png";

    public BullsEyeBandana() {
        super(ID, IMG_PATH);
    }

    @Override
    public void atBattleStart() {
        flash();
        addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDeadOrEscaped()) {
                addToBot(new ApplyPowerAction(m, AbstractDungeon.player, new MarkPower(m, 1)));
            }
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new BullsEyeBandana();
    }
}
