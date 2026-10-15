package io.github.zhuiye486.relics;

import com.megacrit.cardcrawl.relics.AbstractRelic;

import io.github.zhuiye486.helpers.ModHelper;

public class OccultistRelic extends HeroRelic {
    public static final String ID = ModHelper.makePath(OccultistRelic.class.getSimpleName());
        private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/ExampleRelic.png";

        public OccultistRelic() {
            super(ID, IMG_PATH);
    }

    @Override
    public AbstractRelic makeCopy() {
        return new OccultistRelic();
    }
}
