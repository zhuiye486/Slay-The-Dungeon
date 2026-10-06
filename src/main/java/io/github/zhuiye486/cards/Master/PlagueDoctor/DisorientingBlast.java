package io.github.zhuiye486.cards.Master.PlagueDoctor;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import io.github.zhuiye486.powers.BleedPower;
import io.github.zhuiye486.powers.MarkPower;
import io.github.zhuiye486.powers.CorrosionPower;

import io.github.zhuiye486.cards.AbstractDungeonCard;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;

public class DisorientingBlast extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(DisorientingBlast.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/DisorientingBlast.png";
    private static final int COST = 2;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.NONE;

    public DisorientingBlast() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.magicNumber = this.baseMagicNumber = 40;
        this.exhaust = true;
    }

    
    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        for (int i = 0; i < this.magicNumber; i++) {
            AbstractMonster target = AbstractDungeon.getRandomMonster();
            if (target == null) {
                continue;
            }
            switch (AbstractDungeon.cardRandomRng.random(4)) {
                case 0: addToBot(new ApplyPowerAction(target, player, new PoisonPower(target, player, 1))); break;
                case 1: addToBot(new ApplyPowerAction(target, player, new MarkPower(target, 1))); break;
                case 2: addToBot(new ApplyPowerAction(target, player, new BleedPower(target, 1))); break;
                case 3: addToBot(new ApplyPowerAction(target, player, new io.github.zhuiye486.powers.PlaguePower(target, 1))); break;
                default: addToBot(new ApplyPowerAction(target, player, new CorrosionPower(target, 1))); break;
            }
        }
    }

    
    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(10);
        }
    }

    
    @Override
    public AbstractCard makeCopy() {
        return new DisorientingBlast();
    }
}
