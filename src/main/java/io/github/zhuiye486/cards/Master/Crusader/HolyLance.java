package io.github.zhuiye486.cards.Master.Crusader;

import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import io.github.zhuiye486.cards.AbstractDungeonCard;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.powers.CrusadePower;

public class HolyLance extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(HolyLance.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/HolyLance.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    public HolyLance() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.damage = this.baseDamage = 8;
        this.magicNumber = this.baseMagicNumber = 1;
        this.exhaust = true;
    }

    
    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        addToBot(new DamageAction(monster, new DamageInfo(player, this.damage, DamageInfo.DamageType.NORMAL)));
        addToBot(new DrawCardAction(this.magicNumber));
        if (!player.hasPower(CrusadePower.POWER_ID)) {
            addToBot(new ApplyPowerAction(player, player, new CrusadePower(player)));
        }
        addToBot(new com.megacrit.cardcrawl.actions.AbstractGameAction() {
            @Override
            public void update() {
                ((CrusadePower) player.getPower(CrusadePower.POWER_ID)).recordLance();
                this.isDone = true;
            }
        });
        addToBot(new com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction(
                new HolyLance(), 1, false, false, true));
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
        return new HolyLance();
    }
}
