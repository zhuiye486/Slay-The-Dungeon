package io.github.zhuiye486.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.relics.BrightnessIndicator;

public class BrightnessGainPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("BrightnessGain");
    private static final PowerStrings STRINGS = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public BrightnessGainPower(AbstractCreature owner, int amount) {
        this.owner = owner;
        this.name = STRINGS.NAME;
        this.ID = POWER_ID;
        this.amount = amount;
        this.type = PowerType.BUFF;
        this.description = STRINGS.DESCRIPTIONS[0] + amount + STRINGS.DESCRIPTIONS[1];
    }

    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (target != owner) return;
        BrightnessIndicator relic = AbstractDungeon.player == null
            ? null
            : (BrightnessIndicator) AbstractDungeon.player.getRelic(BrightnessIndicator.ID);
        if (relic != null) relic.changeBrightness(amount);
    }
}