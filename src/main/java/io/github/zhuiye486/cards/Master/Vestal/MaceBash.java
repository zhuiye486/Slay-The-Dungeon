package io.github.zhuiye486.cards.Master.Vestal;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;

import io.github.zhuiye486.cards.AbstractDungeonCard;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;

public class MaceBash extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(MaceBash.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/MaceBash.png";
    private static final int COST = 0;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private boolean waitingForBrightnessChange;

    public MaceBash() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.damage = this.baseDamage = 5;
    }

    
    public void use(AbstractPlayer player, AbstractMonster monster) {
        addToBot(new DamageAction(monster,
            new DamageInfo(player, this.damage, DamageInfo.DamageType.NORMAL), AttackEffect.BLUNT_LIGHT));
        this.waitingForBrightnessChange = true;
    }

    public static void onBrightnessChanged() {
        if (AbstractDungeon.player == null) {
            return;
        }
        checkPile(AbstractDungeon.player.hand, true);
        checkPile(AbstractDungeon.player.discardPile, false);
        checkPile(AbstractDungeon.player.drawPile, false);
        checkPile(AbstractDungeon.player.exhaustPile, false);
    }

    private static void checkPile(CardGroup pile, boolean inHand) {
        for (AbstractCard card : pile.group) {
            if (!(card instanceof MaceBash)) {
                continue;
            }
            MaceBash maceBash = (MaceBash) card;
            if (!maceBash.waitingForBrightnessChange) {
                continue;
            }
            if (inHand) {
                maceBash.waitingForBrightnessChange = false;
                continue;
            }
            if (pile != AbstractDungeon.player.discardPile) {
                continue;
            }
            maceBash.waitingForBrightnessChange = false;
            AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                @Override
                public void update() {
                    if (AbstractDungeon.player.discardPile.contains(maceBash)) {
                        AbstractDungeon.player.discardPile.removeCard(maceBash);
                        AbstractDungeon.player.hand.addToTop(maceBash);
                        AbstractDungeon.player.hand.refreshHandLayout();
                        maceBash.unhover();
                    }
                    this.isDone = true;
                }
            });
        }
    }

    
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(3);
        }
    }

    
    public AbstractCard makeCopy() {
        return new MaceBash();
    }
}
