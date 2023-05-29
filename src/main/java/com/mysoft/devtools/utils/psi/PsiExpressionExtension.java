package com.mysoft.devtools.utils.psi;

import com.intellij.psi.*;
import com.intellij.psi.impl.light.LightMethodBuilder;
import com.intellij.psi.impl.source.PsiClassReferenceType;

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

        //String cgPlanName、SFunction<T, R> keyFunc
        if (expression instanceof PsiReferenceExpression) {
            PsiElement referenceExpression = ((PsiReferenceExpression) expression).resolve();
            if (referenceExpression instanceof PsiParameter) {
                return ((PsiParameter) referenceExpression).getType();
            }
        }

        //Long.valueOf(1)
        if (expression instanceof PsiMethodCallExpression) {
            PsiMethod method = ((PsiMethodCallExpression) expression).resolveMethod();
            if (method != null) {
                PsiType returnType = method.getReturnType();
                if (returnType instanceof PsiClassReferenceType) {
                    PsiClassReferenceType type = (PsiClassReferenceType) returnType;
                    if (!type.isRaw()) {
                        return expression.getType();
                    }
                }

                return returnType;
            }
        }

        return null;
    }
}
