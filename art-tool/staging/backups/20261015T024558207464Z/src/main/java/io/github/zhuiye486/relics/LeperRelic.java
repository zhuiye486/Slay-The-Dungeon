package io.github.zhuiye486.relics;

import com.megacrit.cardcrawl.relics.AbstractRelic;

import io.github.zhuiye486.helpers.ModHelper;

public class LeperRelic extends HeroRelic {
    public static final String ID = ModHelper.makePath(LeperRelic.class.getSimpleName());
        private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/ExampleRelic.png";

        public LeperRelic() {
            super(ID, IMG_PATH);
    }

    @Override
    public AbstractRelic makeCopy() {
        return new LeperRelic();
    }
}
