package io.github.zhuiye486.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import io.github.zhuiye486.helpers.ModHelper;

public class RagePower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("Rage");
    private static final PowerStrings STRINGS = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public RagePower(AbstractCreature owner, int amount) {
        this.name = STRINGS.NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;
        this.region128 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                com.megacrit.cardcrawl.helpers.ImageMaster.loadImage("SlayTheDungeonResources/img/powers/RagePower128.png"), 0, 0, 84, 84);
        this.region48 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                com.megacrit.cardcrawl.helpers.ImageMaster.loadImage("SlayTheDungeonResources/img/powers/RagePower48.png"), 0, 0, 32, 32);
        this.description = STRINGS.DESCRIPTIONS[0] + amount + STRINGS.DESCRIPTIONS[1];
    }

    @Override
    public void onCardDraw(AbstractCard card) {
        addToBot(new ApplyPowerAction(this.owner, this.owner, new PlaguePower(this.owner, this.amount)));
        for (AbstractMonster enemy : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!enemy.isDeadOrEscaped()) {
                addToBot(new ApplyPowerAction(enemy, this.owner, new PlaguePower(enemy, this.amount)));
            }
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        }
    }
}