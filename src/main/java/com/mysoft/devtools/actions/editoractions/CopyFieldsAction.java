package com.mysoft.devtools.actions.editoractions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.utils.idea.psi.PsiFieldExtension;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;
import java.util.Collection;

/**
 * 复制当前类所有字段，每个字段一行
 *
 * @author hezd 2023/7/17
 */
@ExtensionMethod({PsiFieldExtension.class})
public class CopyFieldsAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        PsiFile psiFile = e.getDataContext().getData(CommonDataKeys.PSI_FILE);
        if (!(psiFile instanceof PsiJavaFile)) {
            return;
        }

        PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
        PsiClass psiRootClass = PsiTreeUtil.findChildOfType(psiJavaFile, PsiClass.class);
        Collection<PsiField> psiFields = PsiTreeUtil.findChildrenOfAnyType(psiRootClass, PsiField.class);

        StringBuilder sb = new StringBuilder();
        for (PsiField psiField : psiFields) {
            sb.append(psiField.getComment()).append("\t").append(psiField.getType().getPresentableText()).append("\t").append(psiField.getName()).append(System.lineSeparator());
        }

        CopyPasteManager.getInstance().setContents(new StringSelection(sb.toString()));
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
        statusBar.setInfo(LocalBundle.message("devtools.editortabs.copyfullnameaction.success.text", ""));
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        PsiFile psiFile = e.getDataContext().getData(CommonDataKeys.PSI_FILE);

        Presentation presentation = e.getPresentation();
        boolean enable = project != null && psiFile instanceof PsiJavaFile;
        presentation.setEnabledAndVisible(enable);
        presentation.setDescription(LocalBundle.message("devtools.editor.copyfields.description"));
    }
}
