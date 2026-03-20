package io.github.zhuiye486.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireRawPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;

@SpirePatch(clz = AbstractCard.class, method = SpirePatch.CLASS)
public class HealPatch {
    public static SpireField<Boolean> isHealModified = new SpireField<>(() -> false);
    public static SpireField<Boolean> upgradedHeal = new SpireField<>(() -> false);
    

    @SpireRawPatch
    public static void upgradeHealPatch(CtClass ctClass) throws CannotCompileException {
        String methodCode = "public void upgradeHeal(int amount) { " +
                            "this.baseHeal += amount; " +
                            "this.upgradedHeal = true; " +
                            "}";
        CtMethod newMethod = CtMethod.make(methodCode, ctClass);
        ctClass.addMethod(newMethod);
    }
}
