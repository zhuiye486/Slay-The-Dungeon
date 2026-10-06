package io.github.zhuiye486.cards.Master.ShieldBreaker;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.PoisonPower;

import io.github.zhuiye486.cards.AbstractDungeonCard;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.powers.PlaguePower;

public class Expose extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(Expose.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/Expose.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ALL_ENEMY;

    public Expose() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.exhaust = true;
    }

    
    public void use(AbstractPlayer player, AbstractMonster monster) {
        for (AbstractMonster enemy : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (enemy.isDeadOrEscaped()) continue;
            int totalStacks = 0;
            for (AbstractPower power : enemy.powers) {
                if (power.type == AbstractPower.PowerType.DEBUFF) {
                    totalStacks += Math.max(0, power.amount);
                    addToBot(new RemoveSpecificPowerAction(enemy, player, power.ID));
                }
            }
            if (totalStacks > 0) {
                addToBot(new ApplyPowerAction(enemy, player, new PoisonPower(enemy, player, totalStacks)));
                addToBot(new ApplyPowerAction(enemy, player, new PlaguePower(enemy, totalStacks)));
            }
        }
    }

    
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(0);
        }
    }

    
    public AbstractCard makeCopy() {
        return new Expose();
    }
}
