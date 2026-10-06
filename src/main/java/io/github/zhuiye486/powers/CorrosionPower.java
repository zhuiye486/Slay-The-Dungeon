package io.github.zhuiye486.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.PoisonPower;

import io.github.zhuiye486.helpers.ModHelper;

public class CorrosionPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("Corrosion");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTION = powerStrings.DESCRIPTIONS;

    public CorrosionPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.DEBUFF;
        this.amount = amount;

        String path128 = "SlayTheDungeonResources/img/powers/PlaguePower128.png";
        String path48 = "SlayTheDungeonResources/img/powers/PlaguePower48.png";
        this.region128 = new AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTION[0] + this.amount + DESCRIPTION[1];
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (damageAmount <= 0 || info.type == DamageInfo.DamageType.HP_LOSS) {
            return damageAmount;
        }

        AbstractMonster randomMonster = AbstractDungeon.getRandomMonster();
        if (randomMonster != null) {
            addToBot(new ApplyPowerAction(
                randomMonster,
                this.owner,
                new PoisonPower(randomMonster, this.owner, this.amount)
            ));
        }
        return damageAmount;
    }
}
