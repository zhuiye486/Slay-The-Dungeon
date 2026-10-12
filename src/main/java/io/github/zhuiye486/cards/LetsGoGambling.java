package io.github.zhuiye486.cards;

import java.util.ArrayList;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;

public class LetsGoGambling extends AbstractDungeonCard{
    public static final String ID = ModHelper.makePath(LetsGoGambling.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/Letsgogambling.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.NONE;

    public LetsGoGambling() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        ArrayList<AbstractCard> drawCards = new ArrayList<>(p.drawPile.group);
        ArrayList<AbstractCard> discardCards = new ArrayList<>(p.discardPile.group);
        ArrayList<AbstractCard> exhaustCards = new ArrayList<>(p.exhaustPile.group);

        p.drawPile.group.clear();
        p.discardPile.group.clear();
        p.exhaustPile.group.clear();

        p.discardPile.group.addAll(drawCards);
        p.exhaustPile.group.addAll(discardCards);
        p.drawPile.group.addAll(exhaustCards);

        for (AbstractCard card : discardCards) {
            for (AbstractRelic relic : p.relics) {
                relic.onExhaust(card);
            }
            for (AbstractPower power : p.powers) {
                power.onExhaust(card);
            }
            card.triggerOnExhaust();
        }

        for (AbstractCard card : exhaustCards) {
            card.unfadeOut();
            card.untip();
            card.unhover();
        }

        p.drawPile.shuffle();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(0);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new LetsGoGambling();
    }
}
