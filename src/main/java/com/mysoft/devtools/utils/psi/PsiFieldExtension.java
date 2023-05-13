package com.mysoft.devtools.utils.psi;

import com.intellij.psi.PsiField;
import com.intellij.psi.javadoc.PsiDocComment;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author hezd 2023/5/11
 */
public class PsiFieldExtension {
    public static String getComment(PsiField psiField){
        PsiDocComment docComment = psiField.getDocComment();
        if (docComment == null){
            return "";
        }
        return Arrays.stream(docComment.getDescriptionElements()).map(x -> x.getText().replace(" ", "").replace("\n", "")).collect(Collectors.joining(""));
    }
}
