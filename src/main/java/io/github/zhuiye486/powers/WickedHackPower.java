package io.github.zhuiye486.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import io.github.zhuiye486.helpers.ModHelper;

public class WickedHackPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("WickedHack");
    private static final PowerStrings STRINGS = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private boolean waitingForRiposte;

    public WickedHackPower(AbstractCreature owner) {
        this.name = STRINGS.NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        this.type = PowerType.BUFF;
        this.region128 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                com.megacrit.cardcrawl.helpers.ImageMaster.loadImage("SlayTheDungeonResources/img/powers/WickedHackPower128.png"), 0, 0, 84, 84);
        this.region48 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                com.megacrit.cardcrawl.helpers.ImageMaster.loadImage("SlayTheDungeonResources/img/powers/WickedHackPower48.png"), 0, 0, 32, 32);
        this.description = STRINGS.DESCRIPTIONS[0];
    }

    @Override
    public void atStartOfTurn() {
        this.waitingForRiposte = true;
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (!this.waitingForRiposte || info.owner != this.owner
                || info.type != DamageInfo.DamageType.NORMAL || damageAmount <= 0 || target == null) {
            return;
        }
        this.waitingForRiposte = false;
        addToBot(new ApplyPowerAction(target, this.owner, new BleedPower(target, damageAmount)));
        addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
    }
}