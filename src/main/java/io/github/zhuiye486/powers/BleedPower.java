package io.github.zhuiye486.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.AbstractPower.PowerType;

import io.github.zhuiye486.helpers.ModHelper;

public class BleedPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("Bleed");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTION = powerStrings.DESCRIPTIONS;
    
    public static final int BASE_TARGET_AMOUNT = 30;
    public static final double DAMAGE_PERCENT = 0.5;
    public int targetAmount;

    public BleedPower(AbstractCreature owner, int Amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.DEBUFF;
        this.amount = Amount;
        this.targetAmount = BASE_TARGET_AMOUNT;

        String path128 = "SlayTheDungeonResources/img/powers/BleedPower128.png";
        String path48 = "SlayTheDungeonResources/img/powers/BleedPower48.png";
        this.region128 = new AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTION[0] + this.targetAmount + DESCRIPTION[1] + DAMAGE_PERCENT * 100 + "%" + DESCRIPTION[2];
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        while (this.amount >= this.targetAmount) {
            addToTop(
                new DamageAction(
                    owner, 
                    new DamageInfo(
                        owner, 
                        (int)(owner.currentHealth * DAMAGE_PERCENT),
                        DamageType.HP_LOSS
                    )
                )
            );

            this.amount -= this.targetAmount;
            this.targetAmount += 10;
        }
        updateDescription();
    }

}
