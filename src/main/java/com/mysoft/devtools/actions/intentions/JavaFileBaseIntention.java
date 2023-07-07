package com.mysoft.devtools.actions.intentions;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.mysoft.devtools.utils.idea.psi.PsiEditorExtension;
import org.jetbrains.annotations.NotNull;

/**
 * @author hezd   2023/7/6
 */
public abstract class JavaFileBaseIntention implements IntentionAction {
    protected PsiClass getPsiClass(@NotNull Project project, Editor editor, PsiFile file) {
        return PsiEditorExtension.getActivePsiClass(editor);
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        if (!(file instanceof PsiJavaFile)) {
            return false;
        }

        if (file.findElementAt(editor.getCaretModel().getOffset()) == null) {
            return false;
        }

        return true;
    }
}
