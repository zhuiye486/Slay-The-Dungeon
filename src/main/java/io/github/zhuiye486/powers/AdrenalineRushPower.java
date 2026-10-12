package io.github.zhuiye486.powers;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import io.github.zhuiye486.helpers.ModHelper;

public class AdrenalineRushPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("AdrenalineRush");
    private static final PowerStrings STRINGS = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public AdrenalineRushPower(AbstractCreature owner, int bleedMultiplier) {
        this.name = STRINGS.NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = bleedMultiplier;
        this.type = PowerType.BUFF;
        this.region128 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                com.megacrit.cardcrawl.helpers.ImageMaster.loadImage("SlayTheDungeonResources/img/powers/AdrenalineRushPower128.png"), 0, 0, 84, 84);
        this.region48 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                com.megacrit.cardcrawl.helpers.ImageMaster.loadImage("SlayTheDungeonResources/img/powers/AdrenalineRushPower48.png"), 0, 0, 32, 32);
        this.description = STRINGS.DESCRIPTIONS[0];
    }

    @Override
    public void onUseCard(AbstractCard usedCard, UseCardAction action) {
        if (usedCard.purgeOnUse || AbstractDungeon.player == null) return;
        ArrayList<AbstractCard> handCards = new ArrayList<>(AbstractDungeon.player.hand.group);
        handCards.remove(usedCard);
        if (handCards.isEmpty()) return;

        AbstractCard chosen = handCards.get(AbstractDungeon.cardRandomRng.random(handCards.size() - 1));

        int cost = Math.max(0, chosen.costForTurn);
        if (cost > 0 && this.amount > 0) {
            addToBot(new ApplyPowerAction(this.owner, this.owner,
                    new BleedPower(this.owner, cost * this.amount)));
        }
        chosen.purgeOnUse = true;
        addToBot(new NewQueueCardAction(chosen, true, true, true));
    }
}