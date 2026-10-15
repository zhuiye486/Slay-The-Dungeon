package io.github.zhuiye486.actions;

import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;

public class ApplyStunArtifactAction extends AbstractGameAction {
    private final AbstractMonster target;
    private final AbstractCreature source;

    public ApplyStunArtifactAction(AbstractMonster target, AbstractCreature source) {
        this.target = target;
        this.source = source;
    }

    @Override
    public void update() {
        if (target != null) {
            StunMonsterPower stunPower = (StunMonsterPower) target.getPower(StunMonsterPower.POWER_ID);
            if (stunPower != null && stunPower.amount > 0) {
                AbstractDungeon.actionManager.addToTop(
                        new ApplyPowerAction(target, source, new ArtifactPower(target, stunPower.amount)));
            }
        }
        this.isDone = true;
    }
}
