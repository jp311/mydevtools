package com.mysoft.devtools.views.users;

import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.ContextHelpLabel;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.dtos.DbLinkDTO;
import com.mysoft.devtools.dtos.MysoftSettingsDTO;
import com.mysoft.devtools.services.AppSettingsStateService;
import com.mysoft.devtools.settings.DataSourceConfigurable;
import com.mysoft.devtools.utils.JdbcUtil;
import com.mysoft.devtools.utils.MetadataUtil;
import com.mysoft.devtools.utils.psi.IdeaNotifyUtil;
import com.mysoft.devtools.utils.psi.IdeaTerminalUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Objects;

import static com.mysoft.devtools.bundles.LocalBundle.message;

/**
 * @author hezd   2023/5/7
 */
public class MetadataSyncDialog extends BaseDialogComponent {
    private JPanel contentPanel;
    private ContextHelpLabel contextHelpLabel1;
    private ComboBox<String> cmDataSource;
    private final Project project;

    private final MysoftSettingsDTO settings = AppSettingsStateService.getInstance().getState();

    public MetadataSyncDialog(Project project) {
        setTitle(LocalBundle.message("devtools.menutools.syncmetadata.title"));
        init();
        this.project = project;
        contentPanel.setPreferredSize(new Dimension(600, 100));
    }

    @Override
    protected JComponent createCenterPanel() {
        MysoftSettingsDTO settings = AppSettingsStateService.getInstance().getState();
        if (settings != null && settings.dataSources != null) {
            settings.dataSources.forEach(x -> cmDataSource.addItem(x.getAlias()));
        }
        return contentPanel;
    }

    @Override
    protected void doOKAction() {
        String item = cmDataSource.getItem();
        if (item == null || item.isBlank()) {
            IdeaNotifyUtil.dialogError(LocalBundle.message("devtools.settings.metadata.sync.client.validate.unselect"));
            return;
        }

        DbLinkDTO dbLinkDTO = settings.dataSources.stream().filter(x -> Objects.equals(x.getAlias(), item)).findFirst().orElse(null);
        if (dbLinkDTO == null) {
            return;
        }

        var args = MessageFormat.format(" --url={0} --dbUserName={1} --dbPassword={2} --siteAddr={3} --basicAddr={4} --moqlServiceUrl={5} --dbType={6}"
                , JdbcUtil.getUrl(dbLinkDTO.getProvider(), dbLinkDTO.getServerIp(), dbLinkDTO.getServerPort(), dbLinkDTO.getDbName())
                , dbLinkDTO.getUserName()
                , dbLinkDTO.getPassword()
                , ""
                , MetadataUtil.getProductMetadataRootPath()
                , ""
                , dbLinkDTO.getProvider()
        );
        String cmd = MessageFormat.format("java -jar {0} {1}", settings.metadataSyncClientPath, args);
        try {
            IdeaTerminalUtil.execute(project,new File(settings.metadataSyncClientPath).getParent(), cmd);
            close(0);
        } catch (IOException ex) {
            IdeaNotifyUtil.dialogError(ex.getMessage());
        }
    }

    private void createUIComponents() {
        contextHelpLabel1 = ContextHelpLabel.createWithLink(message("devtools.menutools.syncmetadata.title")
                , message("devtools.menutools.syncmetadata.description")
                , message("devtools.menutools.syncmetadata.createdatasource"), () -> {
                    ShowSettingsUtil.getInstance().showSettingsDialog(project, DataSourceConfigurable.class);
                });
    }
}
