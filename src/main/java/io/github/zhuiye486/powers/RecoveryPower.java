package io.github.zhuiye486.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import io.github.zhuiye486.helpers.ModHelper;

public class RecoveryPower extends AbstractPower{
    public static final String POWER_ID = ModHelper.makePath("Recovery");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTION = powerStrings.DESCRIPTIONS;

    public RecoveryPower(AbstractCreature owner, int Amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;
        this.amount = Amount;

        String path128 = "SlayTheDungeonResources/img/powers/RecoveryPower128.png";
        String path48 = "SlayTheDungeonResources/img/powers/RecoveryPower48.png";
        this.region128 = new AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTION[0] + this.amount + DESCRIPTION[1];
    }

    @Override
    public int onHeal(int healAmount) {
        this.flash();
        return (int)(healAmount * 1.5f);
    }

    @Override
    public void atEndOfRound() {
        this.amount--;
        if (this.amount <= 0) {
            addToBot(
                new RemoveSpecificPowerAction(this.owner, this.owner, this)
            );
        } else {
            this.updateDescription();
        }
    }
}
