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

    public BrightnessIndicator() {
        super(ID, ImageMaster.loadImage(IMG_PATH), RELIC_TIER, LANDING_SOUND);
        this.counter = 50;
    }

    public int getBrightness() {
        return this.counter;
    }

    public void setBrightness(int brightness) {
        int updatedBrightness = Math.max(MIN_BRIGHTNESS, Math.min(MAX_BRIGHTNESS, brightness));
        if (updatedBrightness != this.counter) {
            this.counter = updatedBrightness;
            MaceBash.onBrightnessChanged();
        }
    }

    public void changeBrightness(int amount) {
        setBrightness(this.counter + amount);
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        float damageMultiplier = (120.0f - getBrightness()) / 100.0f;
        return Math.max(0, Math.round(damageAmount * damageMultiplier));
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new BrightnessIndicator();
    }
}
