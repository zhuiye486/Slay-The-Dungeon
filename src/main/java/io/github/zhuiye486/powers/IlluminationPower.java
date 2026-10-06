package io.github.zhuiye486.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

import io.github.zhuiye486.actions.ChangeBrightnessAction;
import io.github.zhuiye486.helpers.ModHelper;

public class IlluminationPower extends com.megacrit.cardcrawl.powers.AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("Illumination");
    private static final PowerStrings STRINGS = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public IlluminationPower(AbstractCreature owner, int amount) {
        this.name = STRINGS.NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;
        this.loadRegion("accuracy");
        this.description = STRINGS.DESCRIPTIONS[0] + amount + STRINGS.DESCRIPTIONS[1];
    }

    @Override
    public void atStartOfTurn() {
        addToBot(new ChangeBrightnessAction(this.amount));
    }
}