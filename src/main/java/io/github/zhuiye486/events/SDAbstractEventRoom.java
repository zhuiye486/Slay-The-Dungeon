package io.github.zhuiye486.events;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.EventRoom;

public class SDAbstractEventRoom extends EventRoom{
    public void onPlayerEntry() {
        AbstractDungeon.overlayMenu.proceedButton.hide();
        this.event.onEnterRoom();
    }
}
