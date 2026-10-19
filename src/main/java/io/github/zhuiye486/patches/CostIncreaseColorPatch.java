package io.github.zhuiye486.patches;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

// 费用被增加时（costForTurn > cost）原版仍显示绿色，这里改为红色
@SpirePatch(clz = AbstractCard.class, method = "renderEnergy")
public class CostIncreaseColorPatch {
    private static final Color RED = new Color(1.0F, 0.3F, 0.3F, 1.0F);

    @SpireInstrumentPatch
    public static ExprEditor Instrument() {
        return new ExprEditor() {
            @Override
            public void edit(FieldAccess f) throws javassist.CannotCompileException {
                if (f.isReader() && f.getFieldName().equals("ENERGY_COST_MODIFIED_COLOR")) {
                    f.replace("{ $_ = " + CostIncreaseColorPatch.class.getName()
                            + ".color(this, $proceed($$)); }");
                }
            }
        };
    }

    public static Color color(AbstractCard card, Color original) {
        if (card.cost >= 0 && card.costForTurn > card.cost) {
            return RED.cpy();
        }
        return original;
    }
}
