package io.github.zhuiye486.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.PoisonPower;

import io.github.zhuiye486.helpers.ModHelper;

public class PlaguePower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("Plague");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTION = powerStrings.DESCRIPTIONS;

    public PlaguePower(AbstractCreature owner, int Amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.DEBUFF;
        this.amount = Amount;

        String path128 = "SlayTheDungeonResources/img/powers/PlaguePower128.png";
        String path48 = "SlayTheDungeonResources/img/powers/PlaguePower48.png";
        this.region128 = new AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTION[0] + this.amount + DESCRIPTION[1];
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        AbstractMonster randomMonster = AbstractDungeon.getRandomMonster();
        if (randomMonster == null) {
            return;
        }

        int plagueAmount = this.amount;
        if (randomMonster == this.owner) {
            addToBot(new ApplyPowerAction(
                this.owner,
                this.owner,
                new PoisonPower(this.owner, this.owner, plagueAmount)
            ));
            addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
            return;
        }

        addToBot(new DamageAction(
            this.owner,
            new DamageInfo(this.owner, plagueAmount, DamageType.HP_LOSS)
        ));
        addToBot(new ReducePowerAction(this.owner, this.owner, POWER_ID, plagueAmount / 2));

        addToBot(new ApplyPowerAction(
            randomMonster,
            this.owner,
            new PlaguePower(randomMonster, plagueAmount)
        ));
    }
}
