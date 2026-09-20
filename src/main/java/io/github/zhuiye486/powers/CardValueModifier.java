package io.github.zhuiye486.powers;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface CardValueModifier {
    int modifyStatusAmount(AbstractCard card, int amount);
}
