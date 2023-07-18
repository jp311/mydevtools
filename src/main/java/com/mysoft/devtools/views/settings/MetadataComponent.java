package com.mysoft.devtools.views.settings;

import com.intellij.ui.ContextHelpLabel;
import com.intellij.ui.components.fields.ExtendableTextField;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.controls.ChooseFileUtil;
import com.mysoft.devtools.dtos.MysoftSettingsDTO;
import com.mysoft.devtools.services.AppSettingsStateService;
import com.mysoft.devtools.utils.FileUtil;
import com.mysoft.devtools.utils.MetadataUtil;
import com.mysoft.devtools.utils.idea.IdeaNotifyUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import static com.mysoft.devtools.bundles.LocalBundle.message;

/**
 * @author hezd 2023/4/27
 */
public class MetadataComponent extends BaseSettingsComponent {
    private ExtendableTextField txtMedataPath;
    private JPanel contentPanel;
    private ContextHelpLabel contextHelpLabel1;
    private ExtendableTextField txtMetadataSyncClient;
    private ContextHelpLabel contextHelpLabel2;
    private ExtendableTextField txtSqlToolPath;

    private MysoftSettingsDTO settings = AppSettingsStateService.getInstance().getState();

    @Override
    public JComponent getContentPanel() {
        contentPanel.setBorder(null);

        txtMedataPath.addExtension(ChooseFileUtil.getChooseSingleFolderExtension(LocalBundle.message("devtools.settings.metadata.tooltip"), path -> txtMedataPath.setText(path)));
        txtMedataPath.setText(settings.metadataPath);

        txtMetadataSyncClient.addExtension(ChooseFileUtil.getChooseSingeFileExtension(LocalBundle.message("devtools.settings.metadata.sync.client.tooltip"), fileName -> txtMetadataSyncClient.setText(fileName)));

        txtSqlToolPath.addExtension(ChooseFileUtil.getChooseSingeFileExtension(LocalBundle.message("devtools.settings.metadata.sqltool.tooltip"), fileName -> txtSqlToolPath.setText(fileName)));
        return contentPanel;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return txtMedataPath;
    }

    @Override
    public boolean isModified() {
        return !Objects.equals(settings.metadataPath, txtMedataPath.getText())
                || !Objects.equals(settings.metadataSyncClientPath, txtMetadataSyncClient.getText())
                || !Objects.equals(settings.sqlToolPath, txtSqlToolPath.getText())
                ;
    }

    @Override
    public void apply() {
        String metadataRootPath = txtMedataPath.getText();
        if (!FileUtil.isExist(metadataRootPath)) {
            if (!FileUtil.isExist(FileUtil.combine(MetadataUtil.getRootPath(), metadataRootPath))) {
                IdeaNotifyUtil.dialogError(LocalBundle.message("devtools.settings.metadata.validate.metadata.fail"));
                return;
            }
        }

        if (!FileUtil.isExist(txtMetadataSyncClient.getText())) {
            IdeaNotifyUtil.dialogError(LocalBundle.message("devtools.settings.metadata.validate.metadatas.synctools.fail"));
            return;
        }

        if (!FileUtil.isExist(txtSqlToolPath.getText())) {
            IdeaNotifyUtil.dialogError(LocalBundle.message("devtools.settings.metadata.validate.sqltools.fail"));
            return;
        }

        settings.metadataPath = txtMedataPath.getText();
        settings.metadataSyncClientPath = txtMetadataSyncClient.getText();
        settings.sqlToolPath = txtSqlToolPath.getText();
        AppSettingsStateService.getInstance().loadState(settings);
    }

    @Override
    public void reset() {
        txtMedataPath.setText(settings.metadataPath);
        txtMetadataSyncClient.setText(settings.metadataSyncClientPath);
        txtSqlToolPath.setText(settings.sqlToolPath);
    }

    @Override
    public void disposeUIResources() {
        contentPanel.setEnabled(false);
        settings = null;
    }

    private void createUIComponents() {
        contextHelpLabel1 = ContextHelpLabel.createWithLink(message("devtools.settings.metadata.sync.client.title")
                , message("devtools.settings.metadata.sync.client.description")
                , message("devtools.settings.metadata.sync.client.download"), () -> {
                    if (!Desktop.isDesktopSupported()) {
                        IdeaNotifyUtil.dialogError("Desktop is not supported");
                        return;
                    }
                    try {
                        URI uri = new URI("https://docs.mingyuanyun.com/pages/viewpage.action?pageId=148959495");
                        Desktop.getDesktop().browse(uri);
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                });

        contextHelpLabel2 = ContextHelpLabel.createWithLink(message("devtools.settings.metadata.sync.client.title")
                , message("devtools.settings.metadata.sqltool.description")
                , message("devtools.settings.metadata.sqltool.download"), () -> {
                    if (!Desktop.isDesktopSupported()) {
                        IdeaNotifyUtil.dialogError("Desktop is not supported");
                        return;
                    }
                    try {
                        URI uri = new URI("https://docs.mingyuanyun.com/pages/viewpage.action?pageId=151229359");
                        Desktop.getDesktop().browse(uri);
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                });
    }
}
