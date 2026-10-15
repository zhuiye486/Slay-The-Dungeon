package io.github.zhuiye486.relics;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import io.github.zhuiye486.cards.Master.Jester.Finale;
import io.github.zhuiye486.helpers.ModHelper;

public class DirgeForTheDevoured extends HeroRelic {
    public static final String ID = ModHelper.makePath(DirgeForTheDevoured.class.getSimpleName());
    private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/DirgeForTheDevoured.png";

    public DirgeForTheDevoured() {
        super(ID, IMG_PATH);
    }

    @Override
    public void atBattleStart() {
        flash();
        AbstractCard finale = new Finale();
        finale.cost = 0;
        finale.costForTurn = 0;
        finale.isCostModified = true;
        finale.selfRetain = true;
        addToBot(new MakeTempCardInHandAction(finale, 1));
    }

    @Override
    public AbstractRelic makeCopy() {
        return new DirgeForTheDevoured();
    }
}
