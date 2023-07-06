package com.mysoft.devtools.actions.intentions;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author hezd   2023/7/6
 */
public abstract class BaseIntention implements IntentionAction {
    protected PsiClass getPsiClass(@NotNull Project project, Editor editor, PsiFile file) {
        CaretModel caretModel = editor.getCaretModel();
        int offset = caretModel.getOffset();
        PsiElement elementAt = file.findElementAt(offset);
        return PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
    }
}
