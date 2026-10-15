package io.github.zhuiye486.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.powers.BleedPower;

public class HellsHairpin extends HeroRelic {
    public static final String ID = ModHelper.makePath(HellsHairpin.class.getSimpleName());
    private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/HellsHairpin.png";

    public HellsHairpin() {
        super(ID, IMG_PATH);
    }

    @Override
    public void atBattleStart() {
        flash();
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDeadOrEscaped()) {
                addToBot(new ApplyPowerAction(m, AbstractDungeon.player, new BleedPower(m, 6)));
            }
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new HellsHairpin();
    }
}
