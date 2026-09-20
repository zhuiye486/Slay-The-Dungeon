package io.github.zhuiye486.variables;

import com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.abstracts.DynamicVariable;
import io.github.zhuiye486.cards.AbstractDungeonCard;

public class Chance extends DynamicVariable {
    @Override
    public String key() {
        return "SlayTheDungeonP";
    }

    @Override
    public boolean isModified(AbstractCard card) {
        return card instanceof AbstractDungeonCard
            && ((AbstractDungeonCard) card).isChanceModified;
    }

    @Override
    public int value(AbstractCard card) {
        return card instanceof AbstractDungeonCard
            ? Math.round(((AbstractDungeonCard) card).chance * 100.0f)
            : 0;
    }

    @Override
    public int baseValue(AbstractCard card) {
        return card instanceof AbstractDungeonCard
            ? Math.round(((AbstractDungeonCard) card).baseChance * 100.0f)
            : 0;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        return false;
    }
}
