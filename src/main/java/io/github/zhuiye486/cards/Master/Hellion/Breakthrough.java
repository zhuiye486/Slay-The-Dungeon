package io.github.zhuiye486.cards.Master.Hellion;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import io.github.zhuiye486.cards.AbstractDungeonCard;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import io.github.zhuiye486.powers.BleedPower;

public class Breakthrough extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(Breakthrough.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/Breakthrough.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.NONE;

    public Breakthrough() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.magicNumber = this.baseMagicNumber = 5;
        this.secondMagicNumber = this.baseSecondMagicNumber = 2;
    }

    
    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        addToBot(new ApplyPowerAction(player, player, new BleedPower(player, this.magicNumber)));
        addToBot(new DrawCardAction(this.secondMagicNumber));
    }

    
    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeSecondMagicNumber(1);
            upgradeBaseCost(0);
        }
    }

    
    @Override
    public AbstractCard makeCopy() {
        return new Breakthrough();
    }
}
