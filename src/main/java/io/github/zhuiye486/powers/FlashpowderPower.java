package io.github.zhuiye486.powers;

import com.megacrit.cardcrawl.actions.common.GainGoldAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import io.github.zhuiye486.helpers.ModHelper;

public class FlashpowderPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("Flashpowder");
    private static final PowerStrings STRINGS = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public FlashpowderPower(AbstractCreature owner, int chance) {
        this.owner = owner;
        this.name = STRINGS.NAME;
        this.ID = POWER_ID;
        this.type = PowerType.BUFF;
        this.amount = chance;
        this.region128 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                ImageMaster.loadImage("SlayTheDungeonResources/img/powers/LuckyPower128.png"), 0, 0, 84, 84);
        this.region48 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                ImageMaster.loadImage("SlayTheDungeonResources/img/powers/LuckyPower48.png"), 0, 0, 32, 32);
        updateDescription();
    }

    private void roll() {
        if (com.megacrit.cardcrawl.dungeons.AbstractDungeon.cardRandomRng.randomBoolean(amount / 100.0f)) {
            addToBot(new GainGoldAction(5));
        }
    }

    @Override public void onCardDraw(AbstractCard card) { roll(); }
    @Override public void onUseCard(AbstractCard card, com.megacrit.cardcrawl.actions.utility.UseCardAction action) { roll(); }
    @Override public void onExhaust(AbstractCard card) { roll(); }
    @Override public void updateDescription() { this.description = STRINGS.DESCRIPTIONS[0] + amount + STRINGS.DESCRIPTIONS[1]; }
}