package io.github.zhuiye486.cards.Master.ShieldBreaker;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import io.github.zhuiye486.cards.AbstractDungeonCard;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.powers.PlaguePower;

public class AddersKiss extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(AddersKiss.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/AddersKiss.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.NONE;

    public AddersKiss() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.magicNumber = this.baseMagicNumber = 2;
    }

    
    public void use(AbstractPlayer player, AbstractMonster monster) {
        for (AbstractMonster enemy : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!enemy.isDeadOrEscaped()) {
                addToBot(new ApplyPowerAction(enemy, player, new PlaguePower(enemy, this.magicNumber)));
            }
        }
    }

    
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }

    
    public AbstractCard makeCopy() {
        return new AddersKiss();
    }
}
