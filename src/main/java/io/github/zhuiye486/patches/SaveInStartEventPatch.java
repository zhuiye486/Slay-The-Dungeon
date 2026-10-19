package io.github.zhuiye486.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

// 选英雄事件的房间位于起始节点(y = -1)，在其中存档后读档会因为地图中没有该节点而崩溃。
// 此时回退为涅奥房间，由原版逻辑重新进入开局流程。
@SpirePatch(clz = AbstractDungeon.class, method = "populatePathTaken")
public class SaveInStartEventPatch {
    @SpirePrefixPatch
    public static void Prefix(AbstractDungeon dungeon, SaveFile saveFile) {
        if (saveFile != null && saveFile.room_y < 0) {
            saveFile.current_room = NeowRoom.class.getName();
        }
    }
}
