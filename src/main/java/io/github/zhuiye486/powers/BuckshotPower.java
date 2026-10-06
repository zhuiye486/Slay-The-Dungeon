package io.github.zhuiye486.powers;

import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import io.github.zhuiye486.helpers.ModHelper;

public class BuckshotPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("Buckshot");
    private static final PowerStrings STRINGS = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private int attacksPlayed;

    public BuckshotPower(AbstractCreature owner, int attackNumber) {
        this.owner = owner;
        this.name = STRINGS.NAME;
        this.ID = POWER_ID;
        this.type = PowerType.BUFF;
        this.amount = attackNumber;
        this.region128 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                ImageMaster.loadImage("SlayTheDungeonResources/img/powers/LuckyPower128.png"), 0, 0, 84, 84);
        this.region48 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                ImageMaster.loadImage("SlayTheDungeonResources/img/powers/LuckyPower48.png"), 0, 0, 32, 32);
        this.description = STRINGS.DESCRIPTIONS[0] + this.amount + STRINGS.DESCRIPTIONS[1];
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type != AbstractCard.CardType.ATTACK || card.purgeOnUse) return;
        attacksPlayed++;
        if (attacksPlayed != amount) return;

        action.exhaustCard = false;
        action.returnToHand = false;
        for (int copyIndex = 0; copyIndex < 2; copyIndex++) {
            AbstractCard copy = card.makeStatEquivalentCopy();
            copy.damage = Math.max(0, card.damage / 2);
            copy.baseDamage = copy.damage;
            copy.setCostForTurn(0);
            copy.purgeOnUse = true;
            addToTop(new NewQueueCardAction(copy, true, true, true));
        }
        addToBot(new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction(owner, owner, this));
    }

    @Override
    public void atStartOfTurn() {
        attacksPlayed = 0;
    }

    @Override
    public void updateDescription() {
        this.description = STRINGS.DESCRIPTIONS[0] + amount + STRINGS.DESCRIPTIONS[1];
    }
}