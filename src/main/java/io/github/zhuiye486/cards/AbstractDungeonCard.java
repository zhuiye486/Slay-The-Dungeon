package io.github.zhuiye486.cards;

import com.megacrit.cardcrawl.cards.AbstractCard.CardColor;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTarget;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;

import basemod.abstracts.CustomCard;

public abstract class AbstractDungeonCard extends CustomCard{

    public static int secondMagicNumber;
    public static int baseSecondMagicNumber;
    public static boolean upgradedSecondMagicNumber;
    public static boolean isSecondMagicNumberModified;

    public AbstractDungeonCard(String id, String name,  String img, int cost, String rawDescription, CardType type, CardColor color, CardRarity rarity, CardTarget target) {
        super(id, name, img, cost, rawDescription, type, color, rarity, target);
    }
    
    public void upgradeSecondMagicNumber(int amount) {
        baseSecondMagicNumber += amount;
        secondMagicNumber = baseSecondMagicNumber;
        upgradedSecondMagicNumber = true;
    }
}
