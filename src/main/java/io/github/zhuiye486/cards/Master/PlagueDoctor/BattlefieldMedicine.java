package io.github.zhuiye486.cards.Master.PlagueDoctor;

import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;

import io.github.zhuiye486.cards.AbstractDungeonCard;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.cards.AbstractCard;
import io.github.zhuiye486.patches.HealPatch;

import io.github.zhuiye486.helpers.ModHelper;

public class BattlefieldMedicine extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(BattlefieldMedicine.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/Battlefieldmedicine.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.SELF;

    public BattlefieldMedicine() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.heal = this.baseHeal = 1;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(
            new HealAction(
                p, 
                p, 
                this.heal
            )
        );
        addToBot(
            new RemoveSpecificPowerAction(
                p, 
                p, 
                "SlayTheDungeon:Bleed"
            )
        );
        addToBot(
            new RemoveSpecificPowerAction(
                p, 
                p, 
                "Poison"
            )
        );
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.baseHeal += 2;
            this.heal = this.baseHeal;
            HealPatch.upgradedHeal.set(this, true);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new BattlefieldMedicine();
    }
}
