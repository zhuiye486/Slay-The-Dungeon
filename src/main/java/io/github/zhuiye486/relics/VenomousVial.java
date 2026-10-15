package io.github.zhuiye486.relics;

import java.util.ArrayList;
import java.util.List;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.unique.PoisonLoseHpAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.powers.PlaguePower;

public class VenomousVial extends HeroRelic {
    public static final String ID = ModHelper.makePath(VenomousVial.class.getSimpleName());
    private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/VenomousVial.png";

    public VenomousVial() {
        super(ID, IMG_PATH);
    }

    @Override
    public void onPlayerEndTurn() {
        List<AbstractPower> candidates = new ArrayList<>();
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (m.isDeadOrEscaped()) {
                continue;
            }
            AbstractPower poison = m.getPower(PoisonPower.POWER_ID);
            if (poison != null) {
                candidates.add(poison);
            }
            AbstractPower plague = m.getPower(PlaguePower.POWER_ID);
            if (plague != null) {
                candidates.add(plague);
            }
        }
        if (candidates.isEmpty()) {
            return;
        }
        flash();
        AbstractPower chosen = candidates.get(AbstractDungeon.cardRandomRng.random(candidates.size() - 1));
        if (chosen instanceof PoisonPower) {
            addToBot(new PoisonLoseHpAction(chosen.owner, AbstractDungeon.player, chosen.amount, AttackEffect.POISON));
        } else {
            chosen.atEndOfTurn(false);
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new VenomousVial();
    }
}
