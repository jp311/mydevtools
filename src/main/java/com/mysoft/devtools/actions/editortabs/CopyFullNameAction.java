package com.mysoft.devtools.actions.editortabs;

import com.intellij.codeInsight.TargetElementUtilBase;
import com.intellij.ide.actions.CopyReferenceAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.mysoft.devtools.bundles.LocalBundle;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;
import java.util.List;

/**
 * @author hezd   2023/7/16
 */
public class CopyFullNameAction extends CopyReferenceAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        String ref = getRef(psiFile);

        CopyPasteManager.getInstance().setContents(new StringSelection(ref));
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
        statusBar.setInfo(LocalBundle.message("devtools.editortabs.copyfullnameaction.success.text", ref));
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiFile psiFile = e.getDataContext().getData(CommonDataKeys.PSI_FILE);
        Presentation presentation = e.getPresentation();
        presentation.setEnabledAndVisible(psiFile instanceof PsiJavaFile);

        String ref = getRef(psiFile);
        presentation.putClientProperty(Key.create("QUALIFIED_NAME"), ref);
    }


    private String getRef(PsiFile psiFile) {
        PsiPackageStatement psiPackage = PsiTreeUtil.findChildOfType(psiFile, PsiPackageStatement.class);
        PsiClass psiClass = PsiTreeUtil.findChildOfType(psiFile, PsiClass.class);
        String[] strs = new String[2];
        strs[0] = psiPackage == null ? "" : psiPackage.getPackageName();
        strs[1] = psiClass == null ? "" : psiClass.getName();

        return String.join(".", strs);
    }

    @Override
    protected String getQualifiedName(Editor editor, List<? extends PsiElement> elements) {
        PsiElement element;
        if (editor != null) {
            PsiReference reference = TargetElementUtilBase.findReference(editor, editor.getCaretModel().getOffset());
            if (reference == null) {
                return "";
            }
            element = reference.getElement();
        } else {
            element = elements.stream().findFirst().orElse(null);
        }
        if (element == null) {
            return "";
        }
        PsiFile psiFile = element.getContainingFile();
        if (psiFile == null) {
            return "";
        }
        return getRef(psiFile);
    }
}

