package io.github.zhuiye486.cards.Master.Jester;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import io.github.zhuiye486.cards.AbstractDungeonCard;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.powers.BleedPower;

public class SliceOff extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(SliceOff.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/SliceOff.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.NONE;

    public SliceOff() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.damage = this.baseDamage = 4;
        this.magicNumber = this.baseMagicNumber = 2;
        this.secondMagicNumber = this.baseSecondMagicNumber = 2;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        for (int i = 0; i < this.secondMagicNumber; i++) {
            AbstractMonster targetMonster = AbstractDungeon.getRandomMonster();
            if (targetMonster == null) {
                continue;
            }
            addToBot(new DamageAction(targetMonster,
                    new DamageInfo(player, this.damage, DamageInfo.DamageType.NORMAL), AttackEffect.SLASH_VERTICAL));
            addToBot(new ApplyPowerAction(targetMonster, player,
                    new BleedPower(targetMonster, this.magicNumber)));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
            upgradeMagicNumber(1);
            upgradeSecondMagicNumber(1);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new SliceOff();
    }
}
