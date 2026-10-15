package io.github.zhuiye486.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.powers.BleedPower;

public class BloodInTheMud extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(BloodInTheMud.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/Bloodinthemud.png";
    private static final int COST = 2;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ALL_ENEMY;

    public BloodInTheMud() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.damage = this.baseDamage = 9;
        this.magicNumber = this.baseMagicNumber = 10;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped() && !mo.isDying) {
                addToBot(
                    new ApplyPowerAction(
                        mo,
                        p,
                        new BleedPower(
                            mo, 
                            this.baseMagicNumber
                        )
                    )
                );
                addToBot(
                    new DamageAction(
                        mo, 
                        new DamageInfo(
                            p, 
                            this.damage, 
                            DamageType.NORMAL
                        ),
                        AttackEffect.SLASH_HORIZONTAL
                    )
                );
            }
        }
    }

    @Override
    protected void applyDynamicValues() {
        setDynamicMagicNumber(modifyStatusAmount(this.baseMagicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(3);
            upgradeMagicNumber(5);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new BloodInTheMud();
    }
    
}
