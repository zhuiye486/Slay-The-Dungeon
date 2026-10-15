package io.github.zhuiye486.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import io.github.zhuiye486.helpers.ModHelper;

public class CaltropsPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("Caltrops");
    private static final PowerStrings STRINGS = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private final int repeats;
    private final float chance;
    private int displayedChance = -1;

    public CaltropsPower(AbstractCreature owner, int markAmount, int repeats, float chance) {
        this.name = STRINGS.NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = markAmount;
        this.repeats = repeats;
        this.chance = chance;
        this.type = PowerType.BUFF;
        this.region128 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                com.megacrit.cardcrawl.helpers.ImageMaster.loadImage("SlayTheDungeonResources/img/powers/CaltropsPower128.png"), 0, 0, 84, 84);
        this.region48 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                com.megacrit.cardcrawl.helpers.ImageMaster.loadImage("SlayTheDungeonResources/img/powers/CaltropsPower48.png"), 0, 0, 32, 32);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        displayedChance = Math.round(getCurrentChance() * 100.0f);
        this.description = STRINGS.DESCRIPTIONS[0] + this.amount + STRINGS.DESCRIPTIONS[1]
                + displayedChance + STRINGS.DESCRIPTIONS[2] + this.repeats
                + STRINGS.DESCRIPTIONS[3];
    }

    private float getCurrentChance() {
        return LuckyPower.getLuckyChance(this.chance);
    }

    @Override
    public void update(int slot) {
        super.update(slot);
        if (Math.round(getCurrentChance() * 100.0f) != displayedChance) {
            updateDescription();
        }
    }

    @Override
    public void atStartOfTurn() {
        if (AbstractDungeon.getCurrRoom() == null) return;
        for (int repetition = 0; repetition <= this.repeats; repetition++) {
            if (repetition > 0
                    && !AbstractDungeon.cardRandomRng.randomBoolean(getCurrentChance())) continue;
            AbstractMonster target = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(
                    null, true, AbstractDungeon.cardRandomRng);
            if (target != null) {
                addToBot(new ApplyPowerAction(target, this.owner, new MarkPower(target, this.amount)));
            }
        }
    }
}