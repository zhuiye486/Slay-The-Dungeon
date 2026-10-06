package io.github.zhuiye486.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import io.github.zhuiye486.helpers.ModHelper;

public class FlashingDaggersPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("FlashingDaggers");
    private static final PowerStrings POWER_STRINGS = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public FlashingDaggersPower(AbstractCreature owner, int chance) {
        this.owner = owner;
        this.name = POWER_STRINGS.NAME;
        this.ID = POWER_ID;
        this.type = PowerType.BUFF;
        this.amount = chance;
        this.region128 = new AtlasRegion(
            ImageMaster.loadImage("SlayTheDungeonResources/img/powers/LuckyPower128.png"), 0, 0, 84, 84
        );
        this.region48 = new AtlasRegion(
            ImageMaster.loadImage("SlayTheDungeonResources/img/powers/LuckyPower48.png"), 0, 0, 32, 32
        );
        updateDescription();
    }

    @Override
    public void stackPower(int stackAmount) {
        this.amount = Math.max(this.amount, stackAmount);
        updateDescription();
    }

    public void onManualDiscard(AbstractCard discardedCard) {
        if (discardedCard == null || discardedCard.purgeOnUse || this.amount <= 0) {
            return;
        }
        if (!AbstractDungeon.cardRandomRng.randomBoolean(
            LuckyPower.getLuckyChance(this.amount / 100.0f)
        )) {
            return;
        }

        AbstractCard replay = discardedCard.makeStatEquivalentCopy();
        replay.freeToPlayOnce = true;
        replay.purgeOnUse = true;
        addToBot(new NewQueueCardAction(replay, true, true, true));
    }

    @Override
    public void updateDescription() {
        this.description = POWER_STRINGS.DESCRIPTIONS[0] + this.amount + POWER_STRINGS.DESCRIPTIONS[1];
    }

    @SpirePatch(clz = AbstractCard.class, method = "triggerOnManualDiscard")
    public static class ManualDiscardPatch {
        @SpirePostfixPatch
        public static void postfix(AbstractCard __instance) {
            if (AbstractDungeon.player == null) {
                return;
            }
            for (AbstractPower power : AbstractDungeon.player.powers) {
                if (power instanceof FlashingDaggersPower) {
                    ((FlashingDaggersPower) power).onManualDiscard(__instance);
                }
            }
        }
    }
}