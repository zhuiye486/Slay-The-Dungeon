package io.github.zhuiye486.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.PoisonPower;

import io.github.zhuiye486.helpers.ModHelper;

public class TransformPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("Transform");
    private static final PowerStrings POWER_STRINGS = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = POWER_STRINGS.NAME;
    private static final String[] POWER_DESCRIPTIONS = POWER_STRINGS.DESCRIPTIONS;
    private int multi;

    public TransformPower(AbstractCreature owner, int multi) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.multi = multi;
        this.type = PowerType.BUFF;
        this.amount = -1;

        String path128 = "SlayTheDungeonResources/img/powers/TransformPower128.png";
        String path48 = "SlayTheDungeonResources/img/powers/TransformPower48.png";
        this.region128 = new AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);

        this.description = POWER_DESCRIPTIONS[0];
    }

    @Override
    public void updateDescription() {
        this.description = POWER_DESCRIPTIONS[0];
    }

    @Override
    public boolean canPlayCard(AbstractCard card) {
        if (card == null || AbstractDungeon.player == null || AbstractDungeon.player.hand == null) {
            return true;
        }

        if (!AbstractDungeon.player.hand.group.contains(card)) {
            return true;
        }

        return AbstractDungeon.player.hand.group.get(AbstractDungeon.player.hand.group.size() - 1) == card;
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            return damage * multi;
        }
        return damage;
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type, AbstractCard card) {
        return this.atDamageGive(damage, type);
    }

    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (source != this.owner || power == null) {
            return;
        }

        if (power instanceof BleedPower || power instanceof PoisonPower) {
            power.amount *= 2;
        }
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (this.owner == AbstractDungeon.player) {
            this.flash();
        }
        
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            addToBot(
                new RemoveSpecificPowerAction(this.owner, this.owner, this)
            );
        }
    }
}