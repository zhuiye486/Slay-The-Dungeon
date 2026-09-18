package io.github.zhuiye486.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.powers.BleedPower;

public class Hound extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(Hound.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/Hound.png";
    private static final int COST = 0;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardColor COLOR = CardColor.COLORLESS;
    private static final CardRarity RARITY = CardRarity.SPECIAL;
    private static CardTarget TARGET = CardTarget.ENEMY;

    public Hound() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.damage = this.baseDamage = 3;
        this.magicNumber = this.baseMagicNumber = 3;
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (AbstractDungeon.player.hasPower("SlayTheDungeon:HoundsHurry")){    
            this.TARGET = CardTarget.ALL_ENEMY;
            
            addToBot(
                new DamageAllEnemiesAction(
                    p, 
                    DamageInfo.createDamageMatrix(this.damage, true),
                    DamageType.NORMAL,
                    AttackEffect.SLASH_VERTICAL
                )
            );

            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                addToBot(
                    new ApplyPowerAction(
                        mo, 
                        p, 
                            new BleedPower(
                            mo, 
                            this.magicNumber
                        )
                    )
                );
            }
        } else {
            this.TARGET = CardTarget.ENEMY;
            
            addToBot(
                new DamageAction(
                    m, 
                    new DamageInfo(
                        p, 
                        this.damage,
                        DamageType.NORMAL
                    )
                )
            );

            addToBot(
                new ApplyPowerAction(
                    m, 
                    p, 
                        new BleedPower(
                        m, 
                        this.magicNumber
                    )
                )
            );
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(2);
            this.upgradeMagicNumber(2);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Hound();
    }
}
