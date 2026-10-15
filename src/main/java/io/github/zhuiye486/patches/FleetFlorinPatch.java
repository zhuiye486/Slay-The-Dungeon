package io.github.zhuiye486.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import io.github.zhuiye486.relics.FleetFlorin;

public class FleetFlorinPatch {
    @SpirePatch(clz = AbstractRoom.class, method = "addGoldToRewards", paramtypez = {int.class})
    public static class AddSeparateGoldReward {
        @SpirePostfixPatch
        public static void postfix(AbstractRoom room, int amount) {
            if (amount <= 0 || room.rewards == null || com.megacrit.cardcrawl.dungeons.AbstractDungeon.player == null
                    || !com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasRelic(FleetFlorin.ID)) {
                return;
            }
            for (RewardItem reward : room.rewards) {
                if (FleetFlorinRewardField.isBonus.get(reward)) {
                    return;
                }
            }
            RewardItem bonus = new RewardItem(FleetFlorin.BONUS_GOLD);
            FleetFlorinRewardField.isBonus.set(bonus, true);
            room.rewards.add(bonus);
        }
    }
}
