package com.mysoft.devtools.utils.psi;

import com.intellij.psi.*;
import com.intellij.psi.impl.light.LightMethodBuilder;
import com.intellij.psi.impl.source.PsiParameterImpl;
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl;

/**
 * @author hezd   2023/5/27
 */
public class PsiExpressionExtension {
    public static PsiType tryGetPsiType(PsiExpression expression) {

        //CgPlan::getName
        if (expression instanceof PsiMethodReferenceExpression) {
            PsiElement methodRefExpression = ((PsiMethodReferenceExpression) expression).resolve();
            if (methodRefExpression instanceof LightMethodBuilder) {
                return ((LightMethodBuilder) methodRefExpression).getReturnType();
            }
        }

        //cgPlanName
        if (expression instanceof PsiReferenceExpressionImpl) {
            PsiElement referenceExpression = ((PsiReferenceExpressionImpl) expression).resolve();
            if (referenceExpression instanceof PsiParameterImpl) {
                return ((PsiParameterImpl) referenceExpression).getType();
            }
        }

        //Long.valueOf(1)
        if (expression instanceof PsiMethodCallExpression) {
            PsiMethod method = ((PsiMethodCallExpression) expression).resolveMethod();
            if (method != null) {
                return method.getReturnType();
            }
        }

        return null;
    }
}
