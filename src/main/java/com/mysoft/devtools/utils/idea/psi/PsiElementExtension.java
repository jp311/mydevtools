package com.mysoft.devtools.utils.idea.psi;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
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

    public static long getLineNumber(PsiElement psiElement) {
        PsiFile psiFile = psiElement.getContainingFile();
        Document document = FileDocumentManager.getInstance().getDocument(psiFile.getVirtualFile());
        if (document == null) {
            return -1;
        }
        int textOffset = psiElement.getTextOffset();
        return document.getLineNumber(textOffset) + 1;
    }
}
