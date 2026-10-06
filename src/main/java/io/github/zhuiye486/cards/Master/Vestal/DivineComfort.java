package io.github.zhuiye486.cards.Master.Vestal;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import io.github.zhuiye486.cards.AbstractDungeonCard;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.actions.HealDrawUntilCostNotOneAction;
import io.github.zhuiye486.actions.ChangeBrightnessAction;

public class DivineComfort extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(DivineComfort.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/DivineComfort.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;

    public int amount = 0;

    public DivineComfort() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.amount = 5;
        this.magicNumber = this.baseMagicNumber = 5;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        HealDrawUntilCostNotOneAction action = new HealDrawUntilCostNotOneAction(p, amount);
        action.setOnComplete(() -> addToBot(new ChangeBrightnessAction(action.draws)));
        addToBot(action);
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(0);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new DivineComfort();
    }
}
