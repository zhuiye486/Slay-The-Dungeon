package io.github.zhuiye486.cards;

import java.util.Random;

import com.megacrit.cardcrawl.actions.common.GainGoldAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Injury;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.patches.CardTagsEnum;
import io.github.zhuiye486.powers.LuckyPower;

public class Kleptomaniac extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(Kleptomaniac.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/ItsukiS.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.NONE;

    public static float chance = 0.25f;

    public Kleptomaniac() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.magicNumber = this.baseMagicNumber = 20;
        this.tags.add(CardTagsEnum.Lucky);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        Random random = AbstractDungeon.cardRandomRng.random;
        if (random.nextFloat() < LuckyPower.getLuckyChance(chance)) {
            addToBot(
                new GainGoldAction(this.magicNumber)
            );
        } else {
            addToBot(
                new MakeTempCardInDiscardAction(
                    new Injury(), 
                    1
                )
            );
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(5);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Kleptomaniac();
    }
    
}
