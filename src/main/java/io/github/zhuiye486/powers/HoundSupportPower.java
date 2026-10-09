package io.github.zhuiye486.powers;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import io.github.zhuiye486.cards.Hound;
import io.github.zhuiye486.helpers.ModHelper;

public class HoundSupportPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("HoundSupport");
    public static final int EXTRA_HOUND = 1;
    public static final int GUARD = 2;
    public static final int HEAL = 3;
    private final int mode;
    private final PowerStrings strings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public HoundSupportPower(AbstractCreature owner, int mode) {
        this(owner, mode, mode == GUARD ? 8 : mode == HEAL ? 5 : 1);
    }

    public HoundSupportPower(AbstractCreature owner, int mode, int amount) {
        this.owner = owner;
        this.mode = mode;
        this.name = strings.NAME;
        this.ID = POWER_ID + mode;
        this.type = PowerType.BUFF;
        this.amount = amount;
        this.region128 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                ImageMaster.loadImage("SlayTheDungeonResources/img/powers/DogTreatPower128.png"), 0, 0, 84, 84);
        this.region48 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                ImageMaster.loadImage("SlayTheDungeonResources/img/powers/DogTreatPower48.png"), 0, 0, 32, 32);
        updateDescription();
    }

    @Override
    public void onCardDraw(AbstractCard card) {
        if (mode == EXTRA_HOUND && card instanceof Hound) {
            addToBot(new MakeTempCardInHandAction(new Hound(), 1));
        }
    }

    @Override
    public void onPlayCard(AbstractCard card, com.megacrit.cardcrawl.monsters.AbstractMonster monster) {
        if (!(card instanceof Hound)) {
            return;
        }
        if (mode == GUARD) {
            addToBot(new GainBlockAction(owner, amount));
        } else if (mode == HEAL) {
            addToBot(new HealAction(owner, owner, amount));
        }
        if (mode != EXTRA_HOUND) {
            addToBot(new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction(owner, owner, this.ID));
        }
    }

    @Override
    public void updateDescription() {
        this.description = strings.DESCRIPTIONS[Math.min(mode - 1, strings.DESCRIPTIONS.length - 1)];
    }
}