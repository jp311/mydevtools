package com.mysoft.devtools.actions.toolactions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.dtos.MysoftSettingsDTO;
import com.mysoft.devtools.services.AppSettingsStateService;
import com.mysoft.devtools.settings.MetadataConfigurable;
import com.mysoft.devtools.utils.idea.IdeaNotifyUtil;
import com.mysoft.devtools.views.users.MetadataSyncDialog;
import org.jetbrains.annotations.NotNull;

/**
 * 元数据同步
 *
 * @author hezd   2023/5/6
 */
public class MetadataSyncAction extends ProjectBaseAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        MysoftSettingsDTO settings = AppSettingsStateService.getInstance().getState();
        if (settings == null || settings.metadataSyncClientPath == null || settings.metadataSyncClientPath.isBlank()) {
            int res = IdeaNotifyUtil.dialogQuestion(LocalBundle.message("devtools.menutools.syncmetadata.settings.fail"));
            if (Messages.NO == res) {
                return;
            }
            ShowSettingsUtil.getInstance().showSettingsDialog(project, MetadataConfigurable.class);
            return;
        }

        MetadataSyncDialog dialog = new MetadataSyncDialog(project);
        dialog.show();
    }
}
