package io.github.zhuiye486.cards.Master.HighWayMan;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import io.github.zhuiye486.cards.AbstractDungeonCard;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.powers.RipostePower;

public class GrapeshotBlast extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(GrapeshotBlast.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/GrapeshotBlast.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.NONE;

    public GrapeshotBlast() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.damage = this.baseDamage = 4;
        this.magicNumber = this.baseMagicNumber = 3;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        int livingEnemies = 0;
        for (AbstractMonster enemy : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!enemy.isDeadOrEscaped()) {
                livingEnemies++;
            }
        }

        for (int i = 0; i < this.magicNumber; i++) {
            AbstractMonster targetMonster = AbstractDungeon.getRandomMonster();
            if (targetMonster != null) {
                addToBot(new DamageAction(
                    targetMonster,
                    new DamageInfo(player, this.damage, DamageInfo.DamageType.NORMAL),
                    AttackEffect.SLASH_HORIZONTAL
                ));
            }
        }

        int missingEnemies = Math.max(0, this.magicNumber - livingEnemies);
        if (missingEnemies > 0) {
            addToBot(new ApplyPowerAction(player, player, new RipostePower(player, missingEnemies)));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(2);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new GrapeshotBlast();
    }
}
