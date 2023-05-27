package com.mysoft.devtools.utils.psi;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTypesUtil;

/**
 * @author hezd   2023/5/27
 */
public class PsiTypeExtension {
    public static boolean compareTypes(PsiType leftType, PsiType rightType) {
        if (leftType == null || rightType == null) {
            return false;
        }
        PsiClass srcClass = PsiTypesUtil.getPsiClass(leftType);
        PsiClass tarClass = PsiTypesUtil.getPsiClass(rightType);
        return PsiTypesUtil.compareTypes(leftType, rightType, true)
                || leftType.isAssignableFrom(rightType)
                || (srcClass != null && srcClass.isEquivalentTo(tarClass))
                || (srcClass != null && tarClass != null && srcClass.isInheritor(tarClass, true));
    }
}
