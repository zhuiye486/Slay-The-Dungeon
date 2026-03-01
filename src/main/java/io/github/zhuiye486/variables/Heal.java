package io.github.zhuiye486.variables;

import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.evacipated.cardcrawl.mod.stslib.StSLib;

import basemod.abstracts.DynamicVariable;

import com.megacrit.cardcrawl.cards.AbstractCard;

public class Heal extends DynamicVariable{
    @Override
    public String key() {
        return "SlayTheDungeon:H";
    }

    @Override
    public boolean isModified(AbstractCard card) {
        return card.is;
    }

    @Override
    public int value(AbstractCard card) {
        return card.heal;
    }

    @Override
    public int baseValue(AbstractCard card) {
        return card.baseHeal;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        return card.upgraded;
    }
}
