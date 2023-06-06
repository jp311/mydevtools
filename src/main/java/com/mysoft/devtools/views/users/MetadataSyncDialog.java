package com.mysoft.devtools.views.users;

import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ContextHelpLabel;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.settings.DataSourceConfigurable;

import javax.swing.*;
import java.awt.*;

import static com.mysoft.devtools.bundles.LocalBundle.message;

/**
 * @author hezd   2023/5/7
 */
public class MetadataSyncDialog extends BaseDialogComponent {
    private JPanel contentPanel;
    private ContextHelpLabel contextHelpLabel1;
    private Project project;

    public MetadataSyncDialog(Project project) {
        setTitle(LocalBundle.message("devtools.menutools.syncmetadata.title"));
        init();
        this.project = project;
        contentPanel.setPreferredSize(new Dimension(600, 100));
    }

    @Override
    protected JComponent createCenterPanel() {
        return contentPanel;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
    }

    private void createUIComponents() {
        contextHelpLabel1 = ContextHelpLabel.createWithLink(message("devtools.menutools.syncmetadata.title")
                , message("devtools.menutools.syncmetadata.description")
                , message("devtools.menutools.syncmetadata.createdatasource"), () -> {
                    ShowSettingsUtil.getInstance().showSettingsDialog(project, DataSourceConfigurable.class);
                });
    }
}
