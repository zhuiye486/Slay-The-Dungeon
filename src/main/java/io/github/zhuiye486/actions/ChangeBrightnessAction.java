package io.github.zhuiye486.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import io.github.zhuiye486.relics.BrightnessIndicator;

public class ChangeBrightnessAction extends AbstractGameAction {
    public ChangeBrightnessAction(int amount) {
        this.amount = amount;
    }

    @Override
    public void update() {
        if (AbstractDungeon.player != null) {
            BrightnessIndicator relic = (BrightnessIndicator) AbstractDungeon.player.getRelic(BrightnessIndicator.ID);
            if (relic != null) relic.changeBrightness(amount);
        }
        this.isDone = true;
    }
}