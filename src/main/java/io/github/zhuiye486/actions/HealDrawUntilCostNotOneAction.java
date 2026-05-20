package io.github.zhuiye486.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class HealDrawUntilCostNotOneAction extends AbstractGameAction {
    public int draws = 0;
    public int maxDraws = 10;
    public AbstractPlayer p;
    public int baseHeal;

    public HealDrawUntilCostNotOneAction(AbstractPlayer p, int baseHeal) {
        this.p = p;
        this.baseHeal = baseHeal;
        this.actionType = ActionType.DRAW;
    }

    @Override
    public void update() {
        int previousHandSize = AbstractDungeon.player.hand.size();
        AbstractDungeon.actionManager.addToBottom(new DrawCardAction(1));
        draws++;
        AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.utility.WaitAction(Settings.ACTION_DUR_XFAST));
        AbstractDungeon.actionManager.addToBottom(new CheckDrawAction(this, previousHandSize));
        this.isDone = true;
    }
}