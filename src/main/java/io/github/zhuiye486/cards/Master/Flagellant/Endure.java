package io.github.zhuiye486.cards.Master.Flagellant;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import io.github.zhuiye486.powers.EndurePower;

import io.github.zhuiye486.cards.AbstractDungeonCard;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;

public class Endure extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(Endure.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/Endure.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.NONE;

    public Endure() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.damage = this.baseDamage = 5;
        this.heal = this.baseHeal = 8;
    }

    
    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        addToBot(new DamageAction(player, new DamageInfo(player, this.damage, DamageInfo.DamageType.HP_LOSS)));
        addToBot(new ApplyPowerAction(player, player, new EndurePower(player, 1, this.heal)));
    }

    
    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(3);
            this.baseHeal += 3;
            this.heal = this.baseHeal;
        }
    }

    
    @Override
    public AbstractCard makeCopy() {
        return new Endure();
    }
}
