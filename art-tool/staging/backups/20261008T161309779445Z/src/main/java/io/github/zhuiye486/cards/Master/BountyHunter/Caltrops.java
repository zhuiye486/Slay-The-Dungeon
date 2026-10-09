package io.github.zhuiye486.cards.Master.BountyHunter;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import io.github.zhuiye486.cards.AbstractDungeonCard;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.patches.CardTagsEnum;
import io.github.zhuiye486.powers.CaltropsPower;

public class Caltrops extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(Caltrops.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/Caltrops.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.POWER;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.NONE;

    public Caltrops() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.magicNumber = this.baseMagicNumber = 1;
        this.secondMagicNumber = this.baseSecondMagicNumber = 2;
        this.baseChance = this.chance = 0.25f;
        this.tags.add(CardTagsEnum.Probability);
    }

    
    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        addToBot(new com.megacrit.cardcrawl.actions.common.ApplyPowerAction(
            player, player, new CaltropsPower(player, this.magicNumber, this.secondMagicNumber, this.chance)));
    }

    
    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
            upgradeSecondMagicNumber(1);
            this.baseChance = this.chance = 0.75f;
        }
    }

    
    @Override
    public AbstractCard makeCopy() {
        return new Caltrops();
    }
}
