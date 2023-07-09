package com.mysoft.devtools.views.users;

import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.ContextHelpLabel;
import com.intellij.ui.components.fields.ExtendableTextField;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.controls.ChooseFileUtil;
import com.mysoft.devtools.dtos.DbLinkDTO;
import com.mysoft.devtools.dtos.MysoftSettingsDTO;
import com.mysoft.devtools.services.AppSettingsStateService;
import com.mysoft.devtools.settings.DataSourceConfigurable;
import com.mysoft.devtools.utils.MetadataUtil;
import com.mysoft.devtools.utils.idea.IdeaNotifyUtil;
import com.mysoft.devtools.utils.idea.IdeaTerminalUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Objects;

import static com.mysoft.devtools.bundles.LocalBundle.message;

/**
 * @author hezd 2023/5/31
 */
public class ExecuteSqlDialog extends BaseDialogComponent {
    private final MysoftSettingsDTO settings = AppSettingsStateService.getInstance().getState();
    private final Project project;
    private JPanel contentPanel;
    private ContextHelpLabel contextHelpLabel1;
    private ComboBox<String> cmbDataSource;
    private ExtendableTextField txtSqlPath;
    private ComboBox<String> cmbMode;

    public ExecuteSqlDialog(Project project) {
        setTitle(LocalBundle.message("devtools.menutools.executesql.title"));
        init();
        this.project = project;
        contentPanel.setPreferredSize(new Dimension(600, 100));
    }

    @Override
    protected JComponent createCenterPanel() {
        MysoftSettingsDTO settings = AppSettingsStateService.getInstance().getState();
        txtSqlPath.addExtension(ChooseFileUtil.getChooseSingleFolderExtension(LocalBundle.message("devtools.menutools.executesql.choose.sqlpath.tooltip"), path -> txtSqlPath.setText(path)));
        txtSqlPath.setEditable(true);


        if (settings != null) {
            if (settings.dataSources != null) {
                settings.dataSources.forEach(x -> cmbDataSource.addItem(x.getAlias()));
            }
        }

        cmbMode.addItem("install");
        cmbMode.addItem("update");

        cmbMode.setSelectedIndex(0);

        txtSqlPath.setText(MetadataUtil.getRootPath());
        return contentPanel;
    }

    private DbLinkDTO getSelectDb() {
        String item = cmbDataSource.getItem();
        if (item == null || item.isBlank()) {
            return null;
        }
        return settings.dataSources.stream().filter(x -> Objects.equals(x.getAlias(), item)).findFirst().orElse(null);
    }

    @Override
    protected void doOKAction() {
        DbLinkDTO dbLinkDTO = getSelectDb();
        if (dbLinkDTO == null) {
            IdeaNotifyUtil.dialogError(LocalBundle.message("devtools.settings.metadata.sync.client.validate.unselect"));
            return;
        }


        var args = MessageFormat.format(" -h {0} -p {1} -n {2} -u {3} -P {4} -s {5} -t {6} -m {7}"
                , dbLinkDTO.getServerIp()
                , dbLinkDTO.getServerPort()
                , dbLinkDTO.getDbName()
                , dbLinkDTO.getUserName()
                , dbLinkDTO.getPassword()
                , txtSqlPath.getText()
                , dbLinkDTO.getProvider().toLowerCase()
                , cmbMode.getItem()
        );
        String cmd;
        if (settings.sqlToolPath.toLowerCase().endsWith(".exe")) {
            cmd = MessageFormat.format("{0} {1}", settings.sqlToolPath, args);
        } else {
            cmd = MessageFormat.format("java -jar {0} {1}", settings.sqlToolPath, args);
        }

        try {
            IdeaTerminalUtil.execute(project, new File(settings.metadataSyncClientPath).getParent(), cmd, LocalBundle.message("devtools.menutools.executesql.title"));
            close(0);
        } catch (IOException ex) {
            IdeaNotifyUtil.dialogError(ex.getMessage());
        }
    }

    private void createUIComponents() {
        contextHelpLabel1 = ContextHelpLabel.createWithLink(message("devtools.menutools.syncmetadata.title")
                , message("devtools.menutools.syncmetadata.description")
                , message("devtools.menutools.syncmetadata.createdatasource"), () -> ShowSettingsUtil.getInstance().showSettingsDialog(project, DataSourceConfigurable.class));
    }
}
