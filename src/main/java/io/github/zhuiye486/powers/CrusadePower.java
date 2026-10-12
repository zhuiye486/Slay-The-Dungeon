package io.github.zhuiye486.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import io.github.zhuiye486.helpers.ModHelper;

public class CrusadePower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("Crusade");
    private static final PowerStrings POWER_STRINGS = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public CrusadePower(AbstractCreature owner) {
        this.name = POWER_STRINGS.NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;
        this.amount = 0;
        this.region128 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                ImageMaster.loadImage("SlayTheDungeonResources/img/powers/CrusadePower128.png"), 0, 0, 84, 84);
        this.region48 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                ImageMaster.loadImage("SlayTheDungeonResources/img/powers/CrusadePower48.png"), 0, 0, 32, 32);
        this.description = POWER_STRINGS.DESCRIPTIONS[0] + 4 + POWER_STRINGS.DESCRIPTIONS[1];
    }

    public void recordLance() {
        amount++;
        if (amount >= 4) {
            amount = 0;
            addToBot(new ApplyPowerAction(owner, owner, new HolyLanceStartPower(owner, 1)));
        }
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = POWER_STRINGS.DESCRIPTIONS[0] + amount + POWER_STRINGS.DESCRIPTIONS[1];
    }
}