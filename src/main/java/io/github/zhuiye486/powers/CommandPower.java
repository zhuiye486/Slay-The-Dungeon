package io.github.zhuiye486.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;

import io.github.zhuiye486.helpers.ModHelper;

public class CommandPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("Command");
    private static final PowerStrings STRINGS = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public CommandPower(AbstractCreature owner, int amount) {
        this.owner = owner;
        this.name = STRINGS.NAME;
        this.ID = POWER_ID;
        this.amount = amount;
        this.type = PowerType.BUFF;
        this.region128 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                com.megacrit.cardcrawl.helpers.ImageMaster.loadImage("SlayTheDungeonResources/img/powers/CommandPower128.png"), 0, 0, 84, 84);
        this.region48 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                com.megacrit.cardcrawl.helpers.ImageMaster.loadImage("SlayTheDungeonResources/img/powers/CommandPower48.png"), 0, 0, 32, 32);
        this.description = STRINGS.DESCRIPTIONS[0] + amount + STRINGS.DESCRIPTIONS[1];
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) addToBot(new ApplyPowerAction(owner, owner, new DexterityPower(owner, amount)));
        addToBot(new RemoveSpecificPowerAction(owner, owner, this));
    }
}