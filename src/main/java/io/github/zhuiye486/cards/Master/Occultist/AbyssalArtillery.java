package io.github.zhuiye486.cards.Master.Occultist;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;

import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import io.github.zhuiye486.cards.AbstractDungeonCard;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.relics.BrightnessIndicator;

public class AbyssalArtillery extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(AbyssalArtillery.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/AbyssalArtillery.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    public AbyssalArtillery() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        updateDamageDescription();
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        updateDamageDescription();
    }

    public void use(AbstractPlayer player, AbstractMonster monster) {
        addToBot(new DamageAction(monster,
                new DamageInfo(player, getFormulaDamage(player), DamageInfo.DamageType.NORMAL), AttackEffect.POISON));
    }

    
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(0);
        }
    }

    
    public AbstractCard makeCopy() {
        return new AbyssalArtillery();
    }

    private int getFormulaDamage(AbstractPlayer player) {
        BrightnessIndicator brightness = (BrightnessIndicator) player.getRelic(BrightnessIndicator.ID);
        int value = brightness == null ? 50 : brightness.getBrightness();
        return Math.abs(value - 50) / 2;
    }

    private void updateDamageDescription() {
        AbstractPlayer player = com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;
        int formulaDamage = player == null ? 0 : getFormulaDamage(player);
        this.rawDescription = CARD_STRINGS.DESCRIPTION
                + CARD_STRINGS.EXTENDED_DESCRIPTION[0] + formulaDamage
                + CARD_STRINGS.EXTENDED_DESCRIPTION[1];
        this.initializeDescription();
    }
}
