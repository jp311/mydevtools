package com.mysoft.devtools.actions.exploreractions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.PsiElement;
import com.mysoft.devtools.views.users.JUnitMethodsChooseDialog;
import org.jetbrains.annotations.NotNull;

/**
 * 单元测试
 *
 * @author hezd   2023/7/7
 */
public class UnitTextAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiElement psiElement = e.getDataContext().getData(CommonDataKeys.PSI_ELEMENT);
        Module module = ModuleUtil.findModuleForPsiElement(psiElement);

        JUnitMethodsChooseDialog dialog = new JUnitMethodsChooseDialog(module);
        dialog.show();
    }
}
