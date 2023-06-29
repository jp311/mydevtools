package com.mysoft.devtools.utils.psi;

import com.intellij.psi.*;
import com.intellij.psi.impl.light.LightMethodBuilder;
import com.intellij.psi.util.PsiTypesUtil;

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

        //Long.valueOf(1)、Arrays.asList(BidStatusEnum.Returned.valueOf())、Map<K,V>.getKey()
        if (expression instanceof PsiMethodCallExpression) {
            PsiMethod method = ((PsiMethodCallExpression) expression).resolveMethod();
            if (method != null) {
                PsiType returnType = method.getReturnType();

                PsiClass psiClass = PsiTypesUtil.getPsiClass(returnType);
                if (psiClass != null && psiClass.getQualifiedName() != null) {
                    //adjustInquiryEndTime_CzWeb
                    return expression.getType();
                }
                //BaseDao.findIn
                return returnType;
            }
        }

        return null;
    }
}
