package io.github.zhuiye486.relics;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.powers.LuckyPower;

public class HuntersTalons extends HeroRelic {
    public static final String ID = ModHelper.makePath(HuntersTalons.class.getSimpleName());
    private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/HuntersTalons.png";
    private static final float CHANCE = 0.10f;
    private int displayedChance = -1;

    public HuntersTalons() {
        super(ID, IMG_PATH);
        updateDescription();
    }

    private float getCurrentChance() {
        return LuckyPower.getLuckyChance(CHANCE);
    }

    private void updateDescription() {
        displayedChance = Math.round(getCurrentChance() * 100.0f);
        this.description = DESCRIPTIONS[0] + displayedChance + DESCRIPTIONS[1];
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.purgeOnUse) {
            return;
        }
        if (!AbstractDungeon.cardRandomRng.randomBoolean(getCurrentChance())) {
            return;
        }
        flash();
        AbstractMonster target = action.target instanceof AbstractMonster ? (AbstractMonster) action.target : null;
        AbstractCard copy = card.makeSameInstanceOf();
        AbstractDungeon.player.limbo.addToBottom(copy);
        copy.current_x = card.current_x;
        copy.current_y = card.current_y;
        copy.target_x = Settings.WIDTH / 2.0f - 300.0f * Settings.scale;
        copy.target_y = Settings.HEIGHT / 2.0f;
        copy.freeToPlayOnce = true;
        copy.purgeOnUse = true;
        if (target != null) {
            copy.calculateCardDamage(target);
        }
        AbstractDungeon.actionManager.addCardQueueItem(
            new com.megacrit.cardcrawl.cards.CardQueueItem(copy, target, card.energyOnUse, true, true), true);
    }

    @Override
    public String getUpdatedDescription() {
        updateDescription();
        return this.description;
    }

    @Override
    public void update() {
        super.update();
        if (Math.round(getCurrentChance() * 100.0f) != displayedChance) {
            updateDescription();
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new HuntersTalons();
    }
}
