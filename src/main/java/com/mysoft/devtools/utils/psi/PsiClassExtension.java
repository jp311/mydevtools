package com.mysoft.devtools.utils.psi;

import com.intellij.psi.PsiClass;
import com.intellij.psi.javadoc.PsiDocComment;
import lombok.experimental.ExtensionMethod;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author hezd   2023/5/9
 */
@ExtensionMethod({VirtualFileExtension.class})
public class PsiClassExtension {
    public static String getComment(PsiClass psiClass){
        PsiDocComment docComment = psiClass.getDocComment();
        if (docComment == null){
            return "";
        }
        return Arrays.stream(docComment.getDescriptionElements()).map(x -> x.getText().replace(" ", "").replace("\n", "")).collect(Collectors.joining(""));
    }
     public static String getPackageName(PsiClass psiClass) {
         return psiClass.getContainingFile().getVirtualFile().getPackageName();
     }
}
