package io.github.zhuiye486.cards.Master.PlagueDoctor;

import com.evacipated.cardcrawl.mod.stslib.actions.common.StunMonsterAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import io.github.zhuiye486.cards.AbstractDungeonCard;
import io.github.zhuiye486.actions.ApplyStunArtifactAction;
import io.github.zhuiye486.actions.PlayLastVapoursAction;
import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.patches.CardTagsEnum;

public class BlindingGas extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(BlindingGas.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/BlindingGas.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.NONE;

    public BlindingGas() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.magicNumber = this.baseMagicNumber = 1;
        this.tags.add(CardTagsEnum.Vapours);
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        for (int i = 0; i < this.magicNumber; i++) {
            AbstractMonster targetMonster = AbstractDungeon.getRandomMonster();
            if (targetMonster != null) {
                addToBot(new StunMonsterAction(targetMonster, player));
                addToBot(new ApplyStunArtifactAction(targetMonster, player));
            }
        }
        if (!this.isInAutoplay) {
            addToBot(new PlayLastVapoursAction(this));
        }
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
        return new BlindingGas();
    }
}
