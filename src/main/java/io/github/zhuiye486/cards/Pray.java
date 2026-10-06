package io.github.zhuiye486.cards;

import java.util.Random;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import io.github.zhuiye486.actions.ChangePressureAction;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.patches.CardTagsEnum;
import io.github.zhuiye486.powers.LuckyPower;

public class Pray extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(Pray.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/Pray.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.NONE;


    public Pray() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.magicNumber = this.baseMagicNumber = 10;
        this.baseChance = this.chance = 0.75f;
        this.tags.add(CardTagsEnum.Probability);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        Random random = AbstractDungeon.cardRandomRng.random;
        if (random.nextFloat() < LuckyPower.getLuckyChance(chance)) {
            addToBot(
                new ChangePressureAction(-this.magicNumber)
            );
        } else {
            addToBot(
                new ChangePressureAction(5)
            );
        }
    }


    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.upgradeMagicNumber(5);
        }
    }

    @Override
    public AbstractDungeonCard makeCopy() {
        return new Pray();
    }
}
