package io.github.zhuiye486.powers;

import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import io.github.zhuiye486.helpers.ModHelper;

public class EndurePower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("Endure");
    private static final PowerStrings STRINGS = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private final int heal;

    public EndurePower(AbstractCreature owner, int turns, int heal) {
        this.owner = owner;
        this.name = STRINGS.NAME;
        this.ID = POWER_ID;
        this.type = PowerType.BUFF;
        this.amount = turns;
        this.heal = heal;
        this.region128 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                ImageMaster.loadImage("SlayTheDungeonResources/img/powers/EndurePower128.png"), 0, 0, 84, 84);
        this.region48 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                ImageMaster.loadImage("SlayTheDungeonResources/img/powers/EndurePower48.png"), 0, 0, 32, 32);
        updateDescription();
    }

    @Override
    public void atStartOfTurn() {
        if (amount <= 1) {
            addToBot(new HealAction(owner, owner, heal));
            addToBot(new RemoveSpecificPowerAction(owner, owner, this));
        } else {
            amount--;
            updateDescription();
        }
    }

    @Override
    public void updateDescription() {
        this.description = STRINGS.DESCRIPTIONS[0] + this.amount + STRINGS.DESCRIPTIONS[1] + this.heal;
    }
}