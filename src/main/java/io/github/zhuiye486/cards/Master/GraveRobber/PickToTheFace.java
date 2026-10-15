package io.github.zhuiye486.cards.Master.GraveRobber;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import io.github.zhuiye486.actions.PickToTheFaceRemoveBlockAction;
import io.github.zhuiye486.cards.AbstractDungeonCard;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.powers.PlaguePower;

public class PickToTheFace extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(PickToTheFace.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/PickToTheFace.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    public PickToTheFace() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.magicNumber = this.baseMagicNumber = 1;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void use(AbstractPlayer player, AbstractMonster monster) {
        if (monster == null) {
            return;
        }

        int amount = player.cardsPlayedThisTurn * this.magicNumber;
        addToBot(new PickToTheFaceRemoveBlockAction(monster));
        addToBot(new DamageAction(monster, new DamageInfo(player, amount, DamageInfo.DamageType.NORMAL), AttackEffect.BLUNT_HEAVY));
        if (amount > 0) {
            addToBot(new ApplyPowerAction(monster, player, new PlaguePower(monster, amount)));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new PickToTheFace();
    }
}
