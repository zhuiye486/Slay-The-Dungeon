package io.github.zhuiye486.powers;

import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import io.github.zhuiye486.helpers.ModHelper;

public class Mark extends AbstractPower {
    public static final String POWER_ID = ModHelper.makePath("ExamplePower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTION = powerStrings.DESCRIPTIONS;
    private static final String path128 = "SlayTheDungeonResources/img/powers/Mark128.png";
    private static final String path48 = "SlayTheDungeonResources/img/powers/Mark48.png";

    public Mark(AbstractCreature owner, int Amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.DEBUFF;
        this.amount = Amount;

        this.updateDescription();
    }

    public void updateDescription() {
        this.description = DESCRIPTION[0] + this.amount + DESCRIPTION[1];
    }


    public void onAttack() {
        this.flash();
        
        this.addToTop(
            new DamageAction(
                owner, 
                new DamageInfo(
                    null, 
                    amount, 
                    DamageType.HP_LOSS
                )
            )
        );

    }

}
