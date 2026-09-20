package io.github.zhuiye486.cards;

import com.megacrit.cardcrawl.cards.AbstractCard.CardColor;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTarget;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

import basemod.abstracts.CustomCard;
import io.github.zhuiye486.powers.CardValueModifier;

public abstract class AbstractDungeonCard extends CustomCard{

    public int secondMagicNumber;
    public int baseSecondMagicNumber;
    public boolean upgradedSecondMagicNumber;
    public boolean isSecondMagicNumberModified;
    public float chance;
    public float baseChance;
    public boolean isChanceModified;

    public AbstractDungeonCard(String id, String name,  String img, int cost, String rawDescription, CardType type, CardColor color, CardRarity rarity, CardTarget target) {
        super(id, name, img, cost, rawDescription, type, color, rarity, target);
    }
    
    public void upgradeSecondMagicNumber(int amount) {
        baseSecondMagicNumber += amount;
        secondMagicNumber = baseSecondMagicNumber;
        upgradedSecondMagicNumber = true;
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        this.magicNumber = this.baseMagicNumber;
        this.secondMagicNumber = this.baseSecondMagicNumber;
        this.isMagicNumberModified = false;
        this.isSecondMagicNumberModified = false;
        this.chance = this.baseChance;
        this.isChanceModified = false;
        applyDynamicValues();
        this.chance = io.github.zhuiye486.powers.LuckyPower.getLuckyChance(this.baseChance);
        this.isChanceModified = this.chance != this.baseChance;
        this.isMagicNumberModified = this.magicNumber != this.baseMagicNumber;
        this.isSecondMagicNumberModified = this.secondMagicNumber != this.baseSecondMagicNumber;
        this.initializeDescription();
    }

    protected void applyDynamicValues() {
    }

    protected final void setDynamicMagicNumber(int value) {
        this.magicNumber = value;
        this.isMagicNumberModified = value != this.baseMagicNumber;
    }

    protected final void setDynamicSecondMagicNumber(int value) {
        this.secondMagicNumber = value;
        this.isSecondMagicNumberModified = value != this.baseSecondMagicNumber;
    }

    protected final int modifyStatusAmount(int value) {
        if (AbstractDungeon.player == null) {
            return value;
        }
        int modifiedValue = value;
        for (AbstractPower power : AbstractDungeon.player.powers) {
            if (power instanceof CardValueModifier) {
                modifiedValue = ((CardValueModifier) power).modifyStatusAmount(this, modifiedValue);
            }
        }
        return modifiedValue;
    }
}
