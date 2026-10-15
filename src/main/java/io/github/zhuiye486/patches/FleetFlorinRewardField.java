package io.github.zhuiye486.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.rewards.RewardItem;

@SpirePatch(clz = RewardItem.class, method = SpirePatch.CLASS)
public class FleetFlorinRewardField {
    public static SpireField<Boolean> isBonus = new SpireField<>(() -> false);
}
