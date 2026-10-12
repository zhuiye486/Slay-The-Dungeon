package io.github.zhuiye486.cards.Master.Hellion;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import io.github.zhuiye486.cards.AbstractDungeonCard;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.powers.AdrenalineRushPower;

public class AdrenalineRush extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(AdrenalineRush.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/AdrenalineRush.png";
    private static final int COST = 2;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.POWER;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.NONE;

    public AdrenalineRush() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
    }

    
    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        addToBot(new ApplyPowerAction(player, player, new AdrenalineRushPower(player, 1)));
    }

    
    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(1);
        }
    }

    
    @Override
    public AbstractCard makeCopy() {
        return new AdrenalineRush();
    }
}
