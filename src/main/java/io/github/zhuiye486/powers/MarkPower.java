package io.github.zhuiye486.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import io.github.zhuiye486.helpers.ModHelper;

public class MarkPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("Mark");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTION = powerStrings.DESCRIPTIONS;

    public MarkPower(AbstractCreature owner, int Amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.DEBUFF;
        this.amount = Amount;

        String path128 = "SlayTheDungeonResources/img/powers/MarkPower128.png";
        String path48 = "SlayTheDungeonResources/img/powers/MarkPower48.png";
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
        if (info.type != DamageType.HP_LOSS) {
            this.flash();

            // Relics like Mercury Hourglass / Stone Calendar deal damage with a null owner
            com.megacrit.cardcrawl.core.AbstractCreature source =
                info.owner != null ? info.owner : com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;
            this.addToTop(
                new DamageAction(
                    owner, 
                    new DamageInfo(
                        source,
                        this.amount, 
                        DamageType.HP_LOSS
                    ),
                    AttackEffect.FIRE
                )
            );
        }

        return damageAmount;

    }

}