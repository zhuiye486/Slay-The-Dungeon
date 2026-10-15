package io.github.zhuiye486.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import io.github.zhuiye486.helpers.ModHelper;

public class BerserkMask extends HeroRelic {
    public static final String ID = ModHelper.makePath(BerserkMask.class.getSimpleName());
    private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/BerserkMask.png";
    public static final float LUCKY_PENALTY = 0.10f;

    public BerserkMask() {
        super(ID, IMG_PATH);
    }

    @Override
    public void atBattleStart() {
        flash();
        addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
            new StrengthPower(AbstractDungeon.player, 3), 3));
    }

    @Override
    public AbstractRelic makeCopy() {
        return new BerserkMask();
    }
}
