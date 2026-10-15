package io.github.zhuiye486.cards.Master.Occultist;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardColor;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTarget;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;

import io.github.zhuiye486.actions.ChangeBrightnessAction;
import io.github.zhuiye486.cards.AbstractDungeonCard;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;

public class WeakeningCurse extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(WeakeningCurse.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/Weakeningcurse.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    public WeakeningCurse() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.magicNumber = this.baseMagicNumber = 1;
        this.secondMagicNumber = this.baseSecondMagicNumber = 10;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(
            new DamageAction(
                m,
                new DamageInfo(
                    p,
                    4
                ),
                AttackEffect.POISON
)
        );
        addToBot(
            new ApplyPowerAction(
                m, 
                p, 
                new StrengthPower(
                    m, 
                    -2
                )
            )
        );
        addToBot(
            new ApplyPowerAction(
                m, 
                p, 
                new LoseStrengthPower(
                    m, 
                    2
                )
            )
        );
        addToBot(
            new ApplyPowerAction(
                m, 
                p, 
                new WeakPower(
                    m, 
                    this.magicNumber, 
                    false
                )
            )
        );
        addToBot(new ChangeBrightnessAction(-this.secondMagicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new WeakeningCurse();
    }
}
