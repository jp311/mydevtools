package com.mysoft.devtools.utils.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;

/**
 * @author hezd   2023/5/17
 */
public class PsiElementExtension {
    public static void addImportIfNotExist(PsiElement psiElement, String importStatement) {
        PsiFile containingFile = psiElement.getContainingFile();
        if (containingFile == null) {
            return;
        }
        if (!(containingFile instanceof PsiJavaFile)) {
            return;
        }
        VirtualFileExtension.addImportIfNotExist((PsiJavaFile) containingFile, importStatement);
    }
}
