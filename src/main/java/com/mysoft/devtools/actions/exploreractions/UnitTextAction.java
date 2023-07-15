package com.mysoft.devtools.actions.exploreractions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.dtos.MysoftSettingsDTO;
import com.mysoft.devtools.services.AppSettingsStateService;
import com.mysoft.devtools.settings.AIConfigurable;
import com.mysoft.devtools.utils.StringExtension;
import com.mysoft.devtools.utils.idea.IdeaNotifyUtil;
import com.mysoft.devtools.views.users.JUnitMethodsChooseDialog;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

/**
 * 单元测试
 *
 * @author hezd   2023/7/7
 */
@ExtensionMethod({StringExtension.class})
public class UnitTextAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        MysoftSettingsDTO settings = AppSettingsStateService.getInstance().getState();
        if (settings == null || settings.aiConfigurable == null
                || settings.aiConfigurable.getHost().isNullOrEmpty()
                || settings.aiConfigurable.getUserName().isNullOrEmpty()
                || settings.aiConfigurable.getPassword().isNullOrEmpty()) {
            int res = IdeaNotifyUtil.dialogQuestion(LocalBundle.message("devtools.explorer.ai.settings.fail"));
            if (Messages.NO == res) {
                return;
            }
            ShowSettingsUtil.getInstance().showSettingsDialog(project, AIConfigurable.class);
            return;
        }
        PsiElement psiElement = e.getDataContext().getData(CommonDataKeys.PSI_ELEMENT);
        JUnitMethodsChooseDialog dialog = new JUnitMethodsChooseDialog(project, psiElement);
        dialog.show();
    }
}
