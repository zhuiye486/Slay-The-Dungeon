package io.github.zhuiye486.cards.Master.GraveRobber;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import io.github.zhuiye486.cards.AbstractDungeonCard;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.powers.PlaguePower;

public class PoisonDart extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(PoisonDart.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/Poisondart.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    public PoisonDart() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.damage = this.baseDamage = 5;
        this.magicNumber = this.baseMagicNumber = 2;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(
            new DamageAction(
                m,
                new DamageInfo(
                    p, 
                    this.damage, 
                    DamageType.NORMAL)
            )
        );
        addToBot(
            new ApplyPowerAction(
                m, 
                p, 
                new PlaguePower(
                    m, 
                    this.magicNumber
                )
            )
        );
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
            this.upgradeDamage(2);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new PoisonDart();
    }
}