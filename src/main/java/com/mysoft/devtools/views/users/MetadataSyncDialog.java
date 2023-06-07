package com.mysoft.devtools.views.users;

import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.terminal.JBTerminalWidget;
import com.intellij.ui.ContextHelpLabel;
import com.intellij.ui.content.ContentManager;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.dtos.DbLinkDTO;
import com.mysoft.devtools.dtos.MysoftSettingsDTO;
import com.mysoft.devtools.services.AppSettingsStateService;
import com.mysoft.devtools.settings.DataSourceConfigurable;
import com.mysoft.devtools.utils.psi.IdeaNotifyUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
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

        ToolWindowManager instance = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = instance.getToolWindow("Terminal");
        ContentManager contentManager = toolWindow.getContentManager();
        JBTerminalWidget terminal = (JBTerminalWidget) contentManager.getSelectedContent().getPreferredFocusableComponent();
        Process process = terminal.getProcessTtyConnector().getProcess();
        OutputStream outputStream = process.getOutputStream();
        try {
            outputStream.write("dir".getBytes());
            terminal.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        toolWindow.show();

//        var metadataClientDir = settings.metadataSyncClientPath;
//        var workspaceDir = MetadataUtil.getProductMetadataRootPath();
//
//        var args = MessageFormat.format(" --url={0} --dbUserName={1} --dbPassword={2} --siteAddr={3} --basicAddr={4} --moqlServiceUrl{5}"
//                , JdbcUtil.getUrl(dbLinkDTO.getProvider(), dbLinkDTO.getServerIp(), dbLinkDTO.getServerPort(), dbLinkDTO.getDbName())
//                , dbLinkDTO.getUserName()
//                , dbLinkDTO.getPassword()
//                , ""
//                , MetadataUtil.getProductMetadataRootPath()
//                , ""
//        );
//        String[] cmd = {"java", "-jar", metadataClientDir, args};
//        ConsoleView consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
//
//        GeneralCommandLine commandLine = new GeneralCommandLine(cmd);
//        OSProcessHandler processHandler = null;
//        try {
//            processHandler = new OSProcessHandler(commandLine);
//            commandLine.setCharset(StandardCharsets.UTF_8);
//            commandLine.setWorkDirectory(workspaceDir);
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        }
//        consoleView.attachToProcess(processHandler);
//        processHandler.startNotify();


    }

    private void createUIComponents() {
        contextHelpLabel1 = ContextHelpLabel.createWithLink(message("devtools.menutools.syncmetadata.title")
                , message("devtools.menutools.syncmetadata.description")
                , message("devtools.menutools.syncmetadata.createdatasource"), () -> {
                    ShowSettingsUtil.getInstance().showSettingsDialog(project, DataSourceConfigurable.class);
                });
    }
}
