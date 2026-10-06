package io.github.zhuiye486.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.relics.BrightnessIndicator;

public class MaceBashPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("MaceBash");
    private static final PowerStrings STRINGS = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private final AbstractCard card;
    private int observedBrightness;

    public MaceBashPower(AbstractCreature owner, AbstractCard card) {
        this.name = STRINGS.NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.card = card;
        this.amount = -1;
        this.type = PowerType.BUFF;
        this.loadRegion("accuracy");
        this.description = STRINGS.DESCRIPTIONS[0];
        this.observedBrightness = getBrightness();
    }

    @Override
    public void update(int slot) {
        super.update(slot);
        int brightness = getBrightness();
        if (brightness == this.observedBrightness) return;
        this.observedBrightness = brightness;
        if (AbstractDungeon.player.hand.contains(this.card)) {
            addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        } else if (AbstractDungeon.player.discardPile.contains(this.card)) {
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    AbstractDungeon.player.discardPile.removeCard(card);
                    AbstractDungeon.player.hand.addToTop(card);
                    AbstractDungeon.player.hand.refreshHandLayout();
                    card.unhover();
                    this.isDone = true;
                }
            });
            addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        }
    }

    private int getBrightness() {
        BrightnessIndicator indicator = AbstractDungeon.player == null
                ? null
                : (BrightnessIndicator) AbstractDungeon.player.getRelic(BrightnessIndicator.ID);
        return indicator == null ? 50 : indicator.getBrightness();
    }
}