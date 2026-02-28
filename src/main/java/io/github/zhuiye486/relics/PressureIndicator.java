package io.github.zhuiye486.relics;

import java.util.Random;

import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import basemod.abstracts.CustomRelic;
import io.github.zhuiye486.helpers.ModHelper;

public class PressureIndicator extends CustomRelic{
    public static final String ID = ModHelper.makePath(PressureIndicator.class.getSimpleName());
    private static final String IMG_PATH = "SlayTheDungeonResources/img/relic/PressureIndicator.png";
    private static final RelicTier RELIC_TIER = RelicTier.STARTER;
    private static final LandingSound LANDING_SOUND = LandingSound.FLAT;

    public static final int TEST_AMOUNT = 100;
    public static final int DIE_AMOUNT = 200;

    public PressureIndicator() {
        super(ID, ImageMaster.loadImage(IMG_PATH), RELIC_TIER, LANDING_SOUND);
        this.counter = 0;
    }

    @Override
    public void wasHPLost(int damageAmount) {
        int pressureAdded = generateRandomNumber(damageAmount);
        counter += pressureAdded;
        if (counter >= TEST_AMOUNT) {
            //爆美德或折磨
        } else if (counter >= DIE_AMOUNT) {
            addToBot(
                new LoseHPAction(AbstractDungeon.player, null, 99999)
            );
        }
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return null;
    }

    public int generateRandomNumber(int baseNum) {
        Random random = new Random(AbstractDungeon.miscRng.randomLong());
        float avoidChance = 0.3f;
        int output = random.nextInt(
            (int)((-baseNum) * avoidChance),
            (int)(baseNum * (1 - avoidChance))
        );

        return output;
    }
}
