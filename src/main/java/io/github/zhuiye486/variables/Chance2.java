package io.github.zhuiye486.variables;

import com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.abstracts.DynamicVariable;
import io.github.zhuiye486.cards.AbstractDungeonCard;

public class Chance2 extends DynamicVariable {
    @Override
    public String key() {
        return "SlayTheDungeonP2";
    }

    @Override
    public boolean isModified(AbstractCard card) {
        return card instanceof AbstractDungeonCard
            && ((AbstractDungeonCard) card).isChanceModified;
    }

    @Override
    public int value(AbstractCard card) {
        return card instanceof AbstractDungeonCard
            ? Math.round((1.0f - ((AbstractDungeonCard) card).chance) * 100.0f)
            : 0;
    }

    @Override
    public int baseValue(AbstractCard card) {
        return card instanceof AbstractDungeonCard
            ? Math.round((1.0f - ((AbstractDungeonCard) card).baseChance) * 100.0f)
            : 0;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        return false;
    }
}