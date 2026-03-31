package io.github.zhuiye486.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import io.github.zhuiye486.helpers.ModHelper;

public class BleedOutPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("BleedOut");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTION = powerStrings.DESCRIPTIONS;
    private boolean waitingForAttackDamage;

    public BleedOutPower(AbstractCreature owner, int Amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.DEBUFF;
        this.amount = Amount;

        String path128 = "SlayTheDungeonResources/img/powers/BleedOutPower128.png";
        String path48 = "SlayTheDungeonResources/img/powers/BleedOutPower48.png";
        this.region128 = new AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTION[0] + this.amount + DESCRIPTION[1];
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (!card.purgeOnUse && card.type == AbstractCard.CardType.ATTACK && this.amount > 0) {
            this.waitingForAttackDamage = true;
            this.amount -= 1;
            this.updateDescription();
            if (this.amount == 0) {
                addToBot(
                    new RemoveSpecificPowerAction(
                        this.owner, 
                        this.owner, 
                        this)
                );
            }
        }
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (!this.waitingForAttackDamage) {
            return;
        }
        if (info.owner != this.owner || info.type != DamageInfo.DamageType.NORMAL) {
            return;
        }
        this.waitingForAttackDamage = false;
        if (target != null && !target.isDeadOrEscaped() && damageAmount > 0) {
            addToTop(new ApplyPowerAction(
                target,
                this.owner,
                new BleedPower(target, damageAmount)
            ));
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            addToBot(
                new RemoveSpecificPowerAction(
                    this.owner, 
                    this.owner, 
                    this)
            );
        }
    }
}
