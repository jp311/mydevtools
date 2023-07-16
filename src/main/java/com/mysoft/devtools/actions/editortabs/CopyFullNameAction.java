package com.mysoft.devtools.actions.editortabs;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiPackageStatement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * @author hezd   2023/7/16
 */
public class CopyFullNameAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        PsiPackageStatement psiPackage = PsiTreeUtil.findChildOfType(psiFile, PsiPackageStatement.class);
        PsiClass psiClass = PsiTreeUtil.findChildOfType(psiFile, PsiClass.class);
        String[] strs = new String[2];
        strs[0] = psiPackage == null ? "" : psiPackage.getPackageName();
        strs[1] = psiClass == null ? "" : psiClass.getName();

        String name = String.join(".", strs);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(name), null);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiFile psiFile = e.getDataContext().getData(CommonDataKeys.PSI_FILE);
        e.getPresentation().setVisible(psiFile instanceof PsiJavaFile);
    }
}

