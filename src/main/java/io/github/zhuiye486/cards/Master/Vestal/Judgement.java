package io.github.zhuiye486.cards.Master.Vestal;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.cards.DamageInfo;

import io.github.zhuiye486.cards.AbstractDungeonCard;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.actions.ChangeBrightnessAction;
import io.github.zhuiye486.relics.BrightnessIndicator;

public class Judgement extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(Judgement.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/Judgement.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    public Judgement() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
    }

    
    public void use(AbstractPlayer player, AbstractMonster monster) {
        BrightnessIndicator brightness = (BrightnessIndicator) player.getRelic(BrightnessIndicator.ID);
        int amount = brightness == null ? 0 : brightness.getBrightness();
        addToBot(new ChangeBrightnessAction(-amount));
        addToBot(new DamageAction(monster, new DamageInfo(player, amount, DamageInfo.DamageType.NORMAL), AttackEffect.LIGHTNING));
    }

    
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(0);
        }
    }

    
    public AbstractCard makeCopy() {
        return new Judgement();
    }
}
