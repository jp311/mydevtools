package com.mysoft.devtools.utils.idea.psi;

import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.util.PsiTreeUtil;
import lombok.experimental.ExtensionMethod;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author hezd   2023/5/9
 */
@ExtensionMethod({PsiParameterExtension.class})
public class PsiMethodExtension {
    public static boolean isPublic(PsiMethod psiMethod) {
        PsiModifierList psiModifierList = psiMethod.getModifierList();
        return psiModifierList.hasModifierProperty(PsiModifier.PUBLIC);
    }

    public static boolean isStatic(PsiMethod psiMethod) {
        PsiModifierList psiModifierList = psiMethod.getModifierList();
        return psiModifierList.hasModifierProperty(PsiModifier.STATIC);
    }

    public static boolean isAbstract(PsiMethod psiMethod) {
        PsiModifierList psiModifierList = psiMethod.getModifierList();
        return psiModifierList.hasModifierProperty(PsiModifier.ABSTRACT);
    }

    public static String getComment(PsiMethod method) {
        PsiDocComment docComment = method.getDocComment();
        if (docComment == null) {
            return "";
        }
        return Arrays.stream(docComment.getDescriptionElements()).map(x -> x.getText().replace(" ", "").replace("\n", "")).collect(Collectors.joining(""));
    }

    public static void addAnnotation(PsiMethod method, PsiAnnotation annotation) {
        PsiModifierList modifierList = method.getModifierList();
        modifierList.addAfter(annotation, null);
    }

    public static String getQualifiedName(PsiMethod method) {
        if (method.getContainingClass() == null) {
            return method.getName();
        }
        String className;
        if (method.getContainingClass() instanceof PsiAnonymousClass) {
            PsiClass psiClass = PsiTreeUtil.getParentOfType(method.getContainingClass(), PsiClass.class);
            className = psiClass != null ? psiClass.getQualifiedName() : method.getContainingClass().getQualifiedName();
        } else {
            className = method.getContainingClass().getQualifiedName();
        }
        return className + "." + method.getName();
    }

    public static String getSignature(PsiMethod method) {
        StringBuilder sb = new StringBuilder();
        if (method.getReturnType() == null) {
            sb.append("void ");
        } else {
            sb.append(method.getReturnType().getPresentableText()).append(" ");
        }

        sb.append(method.getName()).append(" (");

        String params = Arrays.stream(method.getParameterList().getParameters()).map(x -> x.getType().getPresentableText() + " " + x.getName()).collect(Collectors.joining(", "));
        sb.append(params);
        sb.append(" )");
        long end = System.currentTimeMillis();
        return sb.toString();
    }

    public static String getSimpleName(PsiMethod method) {
        PsiClass aClass = method.getContainingClass();
        return aClass == null ? "" : aClass.getName() + "." + method.getName();
    }
}
