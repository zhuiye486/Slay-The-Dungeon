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
        return card instanceof AbstractDungeonCard
            && ((AbstractDungeonCard) card).isSecondMagicNumberModified;
    }

    @Override
    public int value(AbstractCard card) {
        return card instanceof AbstractDungeonCard
            ? ((AbstractDungeonCard) card).secondMagicNumber
            : 0;
    }

    @Override
    public int baseValue(AbstractCard card) {
        return card instanceof AbstractDungeonCard
            ? ((AbstractDungeonCard) card).baseSecondMagicNumber
            : 0;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        return card instanceof AbstractDungeonCard
            && ((AbstractDungeonCard) card).upgradedSecondMagicNumber;
    }
}