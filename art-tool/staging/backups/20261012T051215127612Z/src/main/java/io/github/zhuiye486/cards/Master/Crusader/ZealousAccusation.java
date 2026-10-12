package io.github.zhuiye486.cards.Master.Crusader;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import io.github.zhuiye486.powers.MarkPower;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import io.github.zhuiye486.actions.ChangeBrightnessAction;

import io.github.zhuiye486.cards.AbstractDungeonCard;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;

public class ZealousAccusation extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(ZealousAccusation.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/ZealousAccusation.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.NONE;

    public ZealousAccusation() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.magicNumber = this.baseMagicNumber = 2;
        this.secondMagicNumber = this.baseSecondMagicNumber = 5;
        this.secondMagicNumber = this.baseSecondMagicNumber = 5;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        for (int i = 0; i < 2; i++) {
            AbstractMonster targetMonster = AbstractDungeon.getRandomMonster();
            if (targetMonster != null) {
                addToBot(new ApplyPowerAction(targetMonster, player, new MarkPower(targetMonster, this.magicNumber)));
            }
        }
        addToBot(new ChangeBrightnessAction(this.secondMagicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
        }
    }

    
    @Override
    public AbstractCard makeCopy() {
        return new ZealousAccusation();
    }
}
