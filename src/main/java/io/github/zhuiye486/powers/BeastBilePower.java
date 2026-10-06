package io.github.zhuiye486.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import io.github.zhuiye486.helpers.ModHelper;

public class BeastBilePower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("BeastBile");
    private static final PowerStrings STRINGS = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public BeastBilePower(AbstractCreature owner) {
        this.name = STRINGS.NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;
        this.amount = -1;
        this.loadRegion("poison");
        this.description = STRINGS.DESCRIPTIONS[0];
    }

    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (target != this.owner || source == null || source == AbstractDungeon.player
                || !PlaguePower.POWER_ID.equals(power.ID)) {
            return;
        }
        int corrosion = power.amount / 2;
        if (corrosion > 0) {
            addToBot(new RemoveSpecificPowerAction(source, source, PlaguePower.POWER_ID));
            addToBot(new ApplyPowerAction(source, AbstractDungeon.player,
                    new CorrosionPower(source, corrosion)));
        }
    }
}