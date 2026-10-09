package io.github.zhuiye486.cards.Master.Arbalest;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import io.github.zhuiye486.cards.AbstractDungeonCard;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.powers.RallyingFlarePower;

public class RallyingFlare extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(RallyingFlare.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/RallyingFlare.png";
    private static final int COST = 2;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.POWER;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.NONE;

    public RallyingFlare() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.baseChance = this.chance = 0.5f;
        this.magicNumber = this.baseMagicNumber = 1;
    }

    
    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        addToBot(new com.megacrit.cardcrawl.actions.common.ApplyPowerAction(player, player,
                new RallyingFlarePower(player, 50, this.magicNumber)));
    }

    
    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
        }
    }

    
    @Override
    public AbstractCard makeCopy() {
        return new RallyingFlare();
    }
}
