package io.github.zhuiye486.powers;

import java.util.function.Supplier;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import io.github.zhuiye486.helpers.ModHelper;

public class RoarPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("Roar");
    private static final PowerStrings POWER_STRINGS = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private final Supplier<AbstractGameAction> endOfTurnEffect;

    public RoarPower(AbstractCreature owner, int turns, Supplier<AbstractGameAction> endOfTurnEffect) {
        this.owner = owner;
        this.name = POWER_STRINGS.NAME;
        this.ID = POWER_ID;
        this.type = PowerType.BUFF;
        this.amount = Math.max(1, turns);
        this.endOfTurnEffect = endOfTurnEffect;
        this.region128 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                ImageMaster.loadImage("SlayTheDungeonResources/img/powers/LuckyPower128.png"), 0, 0, 84, 84);
        this.region48 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                ImageMaster.loadImage("SlayTheDungeonResources/img/powers/LuckyPower48.png"), 0, 0, 32, 32);
        updateDescription();
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer && endOfTurnEffect != null) {
            addToBot(endOfTurnEffect.get());
        }
        amount--;
        if (amount <= 0) {
            addToBot(new RemoveSpecificPowerAction(owner, owner, this));
        } else {
            updateDescription();
        }
    }

    @Override
    public void updateDescription() {
        this.description = POWER_STRINGS.DESCRIPTIONS[0] + amount + POWER_STRINGS.DESCRIPTIONS[1];
    }
}