package io.github.zhuiye486.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.relics.BrightnessIndicator;

public class LuckyPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("Lucky");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTION = powerStrings.DESCRIPTIONS;

    public LuckyPower(AbstractCreature owner, int Amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;
        this.amount = Amount;

        String path128 = "SlayTheDungeonResources/img/powers/LuckyPower128.png";
        String path48 = "SlayTheDungeonResources/img/powers/LuckyPower48.png";
        this.region128 = new AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);

        this.description = DESCRIPTION[0] + this.amount + DESCRIPTION[1];
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTION[0] + this.amount + DESCRIPTION[1];
    }

    public static float getLuckyChance(float baseChance) {
        float brightnessBonus = 0.0f;
        float relicPenalty = 0.0f;
        if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(io.github.zhuiye486.relics.BerserkMask.ID)) {
            relicPenalty = -io.github.zhuiye486.relics.BerserkMask.LUCKY_PENALTY;
        }
        if (AbstractDungeon.player != null) {
            BrightnessIndicator brightness = (BrightnessIndicator) AbstractDungeon.player.getRelic(BrightnessIndicator.ID);
            if (brightness != null) {
                brightnessBonus = BrightnessIndicator.getPositiveChanceModifier(brightness.getBrightness());
            }
        }
        if (AbstractDungeon.player != null && AbstractDungeon.player.hasPower(POWER_ID)) {
            float bonus = AbstractDungeon.player.getPower(POWER_ID).amount / 100.0f;
            bonus += ComeHitherPower.getBonus(AbstractDungeon.player) / 100.0f;
            return Math.min(1.0f, Math.max(0.0f, baseChance + bonus + brightnessBonus + relicPenalty));
        }
        return Math.min(1.0f, Math.max(0.0f, baseChance + relicPenalty
                + ComeHitherPower.getBonus(AbstractDungeon.player) / 100.0f + brightnessBonus));
    }
}
