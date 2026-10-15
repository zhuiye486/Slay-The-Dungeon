package io.github.zhuiye486.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import io.github.zhuiye486.helpers.ModHelper;

public class LuckyTalisman extends HeroRelic {
    public static final String ID = ModHelper.makePath(LuckyTalisman.class.getSimpleName());
    private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/LuckyTalisman.png";

    public LuckyTalisman() {
        super(ID, IMG_PATH);
    }

    @Override
    public void atBattleStart() {
        flash();
        addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
            new DexterityPower(AbstractDungeon.player, 2), 2));
    }

    @Override
    public AbstractRelic makeCopy() {
        return new LuckyTalisman();
    }
}
