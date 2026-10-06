package io.github.zhuiye486.powers;

import io.github.zhuiye486.actions.ChangeBrightnessAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.evacipated.cardcrawl.mod.stslib.actions.common.StunMonsterAction;
import io.github.zhuiye486.helpers.ModHelper;

public class RallyingFlarePower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("RallyingFlare");
    private static final PowerStrings STRINGS = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private final int chance;
    private final int brightness;

    public RallyingFlarePower(AbstractCreature owner, int chance, int brightness) {
        this.owner = owner;
        this.name = STRINGS.NAME;
        this.ID = POWER_ID;
        this.type = PowerType.BUFF;
        this.amount = chance;
        this.chance = chance;
        this.brightness = brightness;
        this.region128 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                ImageMaster.loadImage("SlayTheDungeonResources/img/powers/LuckyPower128.png"), 0, 0, 84, 84);
        this.region48 = new com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion(
                ImageMaster.loadImage("SlayTheDungeonResources/img/powers/LuckyPower48.png"), 0, 0, 32, 32);
        this.description = STRINGS.DESCRIPTIONS[0] + chance + STRINGS.DESCRIPTIONS[1]
            + brightness + STRINGS.DESCRIPTIONS[2];
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (target instanceof AbstractMonster
            && com.megacrit.cardcrawl.dungeons.AbstractDungeon.cardRandomRng.randomBoolean(
                LuckyPower.getLuckyChance(chance / 100.0f))) {
            addToBot(new StunMonsterAction((AbstractMonster) target, owner));
        }
    }

    @Override
    public void onUseCard(AbstractCard card, com.megacrit.cardcrawl.actions.utility.UseCardAction action) {
        if (card.type == AbstractCard.CardType.ATTACK) {
            addToBot(new ChangeBrightnessAction(brightness));
        }
    }

    @Override
    public void updateDescription() {
        this.description = STRINGS.DESCRIPTIONS[0] + chance + STRINGS.DESCRIPTIONS[1] + brightness + STRINGS.DESCRIPTIONS[2];
    }
}