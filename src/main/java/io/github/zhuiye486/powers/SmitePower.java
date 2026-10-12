package io.github.zhuiye486.powers;

import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.relics.BrightnessIndicator;

public class SmitePower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("Smite");
    private static final PowerStrings STRINGS = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public SmitePower(AbstractCreature owner, int damagePercent) {
        this.name = STRINGS.NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = damagePercent;
        this.type = PowerType.BUFF;
        this.region128 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                com.megacrit.cardcrawl.helpers.ImageMaster.loadImage("SlayTheDungeonResources/img/powers/SmitePower128.png"), 0, 0, 84, 84);
        this.region48 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                com.megacrit.cardcrawl.helpers.ImageMaster.loadImage("SlayTheDungeonResources/img/powers/SmitePower48.png"), 0, 0, 32, 32);
        this.description = STRINGS.DESCRIPTIONS[0] + this.amount + STRINGS.DESCRIPTIONS[1];
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (!isPlayer || AbstractDungeon.getCurrRoom() == null) return;
        AbstractMonster target = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(
                null, true, AbstractDungeon.cardRandomRng);
        if (target == null) return;

        BrightnessIndicator brightness = (BrightnessIndicator) AbstractDungeon.player.getRelic(BrightnessIndicator.ID);
        int currentBrightness = brightness == null ? 50 : brightness.getBrightness();
        int damage = Math.round(currentBrightness * this.amount / 100.0f);
        if (damage > 0) {
            addToBot(new DamageAction(target,
                    new DamageInfo(this.owner, damage, DamageInfo.DamageType.THORNS)));
        }
    }
}