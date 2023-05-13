package com.mysoft.devtools.utils.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author hezd   2023/5/9
 */
public class PsiParameterExtension {

    public static String getComment(PsiParameter psiParameter) {
        PsiElement declarationScope = psiParameter.getDeclarationScope();
        if (!(declarationScope instanceof PsiMethod)) {
            return "";
        }
        PsiMethod method = (PsiMethod) declarationScope;
        PsiDocComment docComment = method.getDocComment();
        if (docComment == null) {
            return "";
        }

        PsiDocTag[] params = docComment.findTagsByName("param");
        PsiDocTag psiDocTag = Arrays.stream(params).filter(x -> x.getDataElements().length >= 2 && Objects.equals(x.getDataElements()[0].getText(), psiParameter.getName())).findFirst().orElse(null);
        if (psiDocTag == null) {
            return "";
        }
        return psiDocTag.getDataElements()[1].getText().replace(" ", "").replace("\n", "");
    }
}
