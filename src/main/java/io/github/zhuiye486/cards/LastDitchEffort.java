package io.github.zhuiye486.cards;

import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;


public class LastDitchEffort extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(LastDitchEffort.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/Lastditcheffort.png";
    private static final int COST = 3;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;


    public LastDitchEffort() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        updateDamageDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int damageAmount = getDamageAmount(p);
        addToBot(
            new DamageAction(
                m, 
                new DamageInfo(
                    m, 
                    damageAmount
                )
            )
        );
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        updateDamageDescription();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.upgradeBaseCost(1);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new LastDitchEffort();
    }

    private int getDamageAmount(AbstractPlayer player) {
        return Math.max(0, player.maxHealth - player.currentHealth);
    }

    private void updateDamageDescription() {
        AbstractPlayer player = AbstractDungeon.player;
        int damageAmount = player == null ? 0 : getDamageAmount(player);
        this.rawDescription = CARD_STRINGS.DESCRIPTION
                + CARD_STRINGS.EXTENDED_DESCRIPTION[0] + damageAmount
                + CARD_STRINGS.EXTENDED_DESCRIPTION[1];
        this.initializeDescription();
    }
}
