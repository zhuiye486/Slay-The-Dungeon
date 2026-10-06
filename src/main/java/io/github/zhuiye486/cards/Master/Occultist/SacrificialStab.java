package io.github.zhuiye486.cards.Master.Occultist;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

import io.github.zhuiye486.actions.ChangeBrightnessAction;
import io.github.zhuiye486.cards.AbstractDungeonCard;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;

public class SacrificialStab extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(SacrificialStab.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/SacrificialStab.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.NONE;

    public SacrificialStab() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.damage = this.baseDamage = 5;
        this.magicNumber = this.baseMagicNumber = 2;
        this.secondMagicNumber = this.baseSecondMagicNumber = 10;
    }

    
    public void use(AbstractPlayer player, AbstractMonster monster) {
        addToBot(new LoseHPAction(player, player, this.damage));
        for (AbstractMonster enemy : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!enemy.isDeadOrEscaped()) {
                addToBot(new ApplyPowerAction(enemy, player, new VulnerablePower(enemy, this.magicNumber, false)));
                addToBot(new ApplyPowerAction(enemy, player, new WeakPower(enemy, this.magicNumber, false)));
            }
        }
        addToBot(new ChangeBrightnessAction(-this.secondMagicNumber));
    }

    
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(2);
            upgradeMagicNumber(1);
            upgradeSecondMagicNumber(-2);
        }
    }

    
    public AbstractCard makeCopy() {
        return new SacrificialStab();
    }
}
