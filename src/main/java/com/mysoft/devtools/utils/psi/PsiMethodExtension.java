package com.mysoft.devtools.utils.psi;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.javadoc.PsiDocComment;
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

    public static String getComment(PsiMethod method){
        PsiDocComment docComment = method.getDocComment();
        if (docComment == null){
            return "";
        }
        return Arrays.stream(docComment.getDescriptionElements()).map(x -> x.getText().replace(" ", "").replace("\n", "")).collect(Collectors.joining(""));
    }
}
