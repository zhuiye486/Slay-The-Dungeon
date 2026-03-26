package io.github.zhuiye486.variables;

import com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.abstracts.DynamicVariable;
import io.github.zhuiye486.cards.AbstractDungeonCard;

public class SecondMagicNumber extends DynamicVariable{
    @Override
    public String key() {
        return "SlayTheDungeonM2";
    }

    @Override
    public boolean isModified(AbstractCard card) {
        return AbstractDungeonCard.isSecondMagicNumberModified;
    }

    @Override
    public int value(AbstractCard card) {
        return AbstractDungeonCard.secondMagicNumber;
    }

    @Override
    public int baseValue(AbstractCard card) {
        return AbstractDungeonCard.baseSecondMagicNumber;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        return AbstractDungeonCard.upgradedSecondMagicNumber;
    }
}