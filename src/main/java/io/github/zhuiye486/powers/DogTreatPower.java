package io.github.zhuiye486.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import io.github.zhuiye486.cards.Hound;
import io.github.zhuiye486.helpers.ModHelper;

public class DogTreatPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("Dogfood");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTION = powerStrings.DESCRIPTIONS;

    private static int postfix = 0;
    public boolean isUpgraded = false;

    public DogTreatPower(AbstractCreature owner, int Amount, boolean isUpgraded) {
        this.name = NAME;
        this.ID = POWER_ID + postfix++;
        this.owner = owner;
        this.type = PowerType.BUFF;
        this.amount = Amount;
        this.isUpgraded = isUpgraded;

        String path128 = "SlayTheDungeonResources/img/powers/DogfoodPower128.png";
        String path48 = "SlayTheDungeonResources/img/powers/DogfoodPower48.png";
        this.region128 = new AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        if (this.isUpgraded) {
            this.description = DESCRIPTION[0] + "+" + DESCRIPTION[1];
        } else {
            this.description = DESCRIPTION[0] + DESCRIPTION[1];
        }
    }

    @Override
    public void atStartOfTurn() {
        for (int i = 0; i < this.amount * 2; i++) {
            AbstractCard hound = new Hound();
            if (this.isUpgraded) {
                hound.upgrade();
            }
            addToBot(new MakeTempCardInHandAction(hound));
        }
    }

    
}
