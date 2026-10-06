package io.github.zhuiye486.variables;

import com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.abstracts.DynamicVariable;
import io.github.zhuiye486.patches.HealPatch;

public class Heal extends DynamicVariable{
    @Override
    public String key() {
        return "SlayTheDungeonH";
    }

    @Override
    public boolean isModified(AbstractCard card) {
        return HealPatch.isHealModified.get(card) || card.heal != card.baseHeal;
    }

    @Override
    public void setIsModified(AbstractCard card, boolean v) {
        HealPatch.isHealModified.set(card, v);
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
        return HealPatch.upgradedHeal.get(card) || (card.upgraded && card.heal != card.baseHeal);
    }
}
