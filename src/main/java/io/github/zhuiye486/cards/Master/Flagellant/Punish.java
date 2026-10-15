package io.github.zhuiye486.cards.Master.Flagellant;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import io.github.zhuiye486.cards.AbstractDungeonCard;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import io.github.zhuiye486.powers.BleedPower;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class Punish extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(Punish.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/Punish.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.NONE;

    public Punish() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        updateBleedDescription();
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        updateBleedDescription();
    }

    @Override
    protected void applyDynamicValues() {
        int lastDamageTaken = AbstractDungeon.player == null ? 0 : AbstractDungeon.player.lastDamageTaken;
        setDynamicMagicNumber(Math.max(0, lastDamageTaken));
    }

    private void updateBleedDescription() {
        String baseDescription = this.upgraded && CARD_STRINGS.UPGRADE_DESCRIPTION != null
                ? CARD_STRINGS.UPGRADE_DESCRIPTION
                : CARD_STRINGS.DESCRIPTION;
        int bleedAmount = AbstractDungeon.player == null ? 0 : Math.max(0, AbstractDungeon.player.lastDamageTaken);
        this.rawDescription = baseDescription
                + CARD_STRINGS.EXTENDED_DESCRIPTION[0] + bleedAmount
                + CARD_STRINGS.EXTENDED_DESCRIPTION[1];
        this.initializeDescription();
    }


    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        int amount = Math.max(0, player.lastDamageTaken);
        for (AbstractMonster target : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (amount > 0 && !target.isDeadOrEscaped()) {
                addToBot(new ApplyPowerAction(target, player, new BleedPower(target, amount)));
            }
            if (this.upgraded) {
                if (!target.isDeadOrEscaped()) {
                    addToBot(new ApplyPowerAction(target, player, new BleedPower(target, 5)));
                }
            }
        }
    }

    
    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            updateBleedDescription();
        }
    }

    
    @Override
    public AbstractCard makeCopy() {
        return new Punish();
    }
}
