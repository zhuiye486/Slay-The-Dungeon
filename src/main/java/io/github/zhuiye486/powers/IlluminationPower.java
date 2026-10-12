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
        this.region128 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                com.megacrit.cardcrawl.helpers.ImageMaster.loadImage("SlayTheDungeonResources/img/powers/IlluminationPower128.png"), 0, 0, 84, 84);
        this.region48 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                com.megacrit.cardcrawl.helpers.ImageMaster.loadImage("SlayTheDungeonResources/img/powers/IlluminationPower48.png"), 0, 0, 32, 32);
        this.description = STRINGS.DESCRIPTIONS[0] + amount + STRINGS.DESCRIPTIONS[1];
    }

    @Override
    public void atStartOfTurn() {
        addToBot(new ChangeBrightnessAction(this.amount));
    }
}