package io.github.zhuiye486.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.powers.RipostePower;

public class GunslingersBuckle extends HeroRelic {
    public static final String ID = ModHelper.makePath(GunslingersBuckle.class.getSimpleName());
    private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/GunslingersBuckle.png";

    public GunslingersBuckle() {
        super(ID, IMG_PATH);
    }

    @Override
    public void wasHPLost(int damageAmount) {
        if (damageAmount > 0 && AbstractDungeon.getCurrRoom().phase == com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase.COMBAT) {
            flash();
            addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                new RipostePower(AbstractDungeon.player, 1)));
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new GunslingersBuckle();
    }
}
