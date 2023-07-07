package com.mysoft.devtools.utils.idea.psi;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaCodeReferenceElement;

/**
 * @author hezd   2023/5/21
 */
public class PsiClassObjectAccessExpressionExtension {
    public static PsiClass getPsiClass(PsiClassObjectAccessExpression psiClassObjectAccessExpression) {
        PsiJavaCodeReferenceElement innermostComponentReferenceElement = psiClassObjectAccessExpression.getOperand().getInnermostComponentReferenceElement();
        if (innermostComponentReferenceElement == null) {
            return null;
        }
        PsiElement resolve = innermostComponentReferenceElement.resolve();
        if (resolve instanceof PsiClass) {
            return (PsiClass) resolve;
        }
        return null;
    }
}
