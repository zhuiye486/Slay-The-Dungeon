package io.github.zhuiye486.relics;

import io.github.zhuiye486.cards.Master.Vestal.MaceBash;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import basemod.abstracts.CustomRelic;
import io.github.zhuiye486.helpers.ModHelper;

public class BrightnessIndicator extends CustomRelic {
    public static final String ID = ModHelper.makePath(BrightnessIndicator.class.getSimpleName());
    private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/BrightnessIndicator.png";
    private static final RelicTier RELIC_TIER = RelicTier.STARTER;
    private static final LandingSound LANDING_SOUND = LandingSound.FLAT;
    public static final int MIN_BRIGHTNESS = 0;
    public static final int MAX_BRIGHTNESS = 100;

    public static int getMaxBrightness() {
        int max = MAX_BRIGHTNESS;
        if (com.megacrit.cardcrawl.dungeons.AbstractDungeon.player != null
                && com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasRelic(HolyOrders.ID)) {
            max += HolyOrders.BONUS_MAX_BRIGHTNESS;
        }
        return max;
    }

    public BrightnessIndicator() {
        super(ID, ImageMaster.loadImage(IMG_PATH), RELIC_TIER, LANDING_SOUND);
        this.counter = 50;
    }

    public int getBrightness() {
        return this.counter;
    }

    public void setBrightness(int brightness) {
        int updatedBrightness = Math.max(MIN_BRIGHTNESS, Math.min(getMaxBrightness(), brightness));
        if (updatedBrightness != this.counter) {
            this.counter = updatedBrightness;
            updateDescription();
            flash();
            MaceBash.onBrightnessChanged();
        }
    }

    public void changeBrightness(int amount) {
        setBrightness(this.counter + amount);
    }

    public static float getDamageTakenMultiplier(int brightness) {
        return 1.2f - brightness * 0.004f;
    }

    public static float getPressureMultiplier(int brightness) {
        return getDamageTakenMultiplier(brightness);
    }

    public static float getGoldMultiplier(int brightness) {
        return (150.0f - brightness) / 100.0f;
    }

    public static float getPositiveChanceModifier(int brightness) {
        return Math.max(-0.25f, Math.min(0.25f, (50.0f - brightness) / 100.0f));
    }

    private String buildDescription() {
        int brightness = getBrightness();
        int damagePercent = Math.round(getDamageTakenMultiplier(brightness) * 100.0f);
        int pressurePercent = Math.round(getPressureMultiplier(brightness) * 100.0f);
        int chancePercent = Math.round(getPositiveChanceModifier(brightness) * 100.0f);
        int goldPercent = Math.round(getGoldMultiplier(brightness) * 100.0f);
        return String.format(this.DESCRIPTIONS[0], damagePercent, pressurePercent, chancePercent, goldPercent);
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        float damageMultiplier = getDamageTakenMultiplier(getBrightness());
        return Math.max(0, Math.round(damageAmount * damageMultiplier));
    }

    @Override
    public String getUpdatedDescription() {
        return buildDescription();
    }

    private void updateDescription() {
        String updatedDescription = buildDescription();
        if (!updatedDescription.equals(this.description)) {
            this.description = updatedDescription;
            if (this.tips != null && !this.tips.isEmpty()) {
                this.tips.get(0).body = updatedDescription;
            }
        }
    }

    @Override
    public void update() {
        super.update();
        updateDescription();
    }

    @Override
    public AbstractRelic makeCopy() {
        return new BrightnessIndicator();
    }
}
