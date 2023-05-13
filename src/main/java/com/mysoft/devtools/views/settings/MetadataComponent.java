package com.mysoft.devtools.views.settings;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.ui.components.fields.ExtendableTextComponent;
import com.intellij.ui.components.fields.ExtendableTextField;
import com.mysoft.devtools.dtos.MysoftSettingsDTO;
import com.mysoft.devtools.services.AppSettingsStateService;
import com.mysoft.devtools.utils.psi.IdeaContext;

import javax.swing.*;
import java.util.Objects;

/**
 * @author hezd 2023/4/27
 */
public class MetadataComponent extends BaseSettingsComponent {
    private ExtendableTextField txtMedataPath;
    private JPanel contentPanel;

    private MysoftSettingsDTO settings = AppSettingsStateService.getInstance().getState();

    @Override
    public JComponent getContentPanel() {
        contentPanel.setBorder(null);


        ExtendableTextComponent.Extension browseExtension =
                ExtendableTextComponent.Extension.create(AllIcons.General.OpenDisk, AllIcons.General.OpenDiskHover,
                        "选择元数据目录", () -> {
                            FileChooserDescriptor dirChooser = FileChooserDescriptorFactory.createSingleFolderDescriptor();
                            dirChooser.setShowFileSystemRoots(true);
                            dirChooser.setHideIgnored(true);
                            dirChooser.setTitle("选择元数据目录");
                            FileChooser.chooseFiles(dirChooser,IdeaContext.getProject(),null,paths ->{
                                if (paths.size() == 0){
                                    return;
                                }
                                txtMedataPath.setText(paths.get(0).getPath());
                            });
                        });

        txtMedataPath.addExtension(browseExtension);
        txtMedataPath.setEditable(false);


        txtMedataPath.setText(settings.metadataPath);
        return contentPanel;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return txtMedataPath;
    }

    @Override
    public boolean isModified() {
        return !Objects.equals(settings.metadataPath, txtMedataPath.getText());
    }

    @Override
    public void apply() {
        settings.metadataPath = txtMedataPath.getText();
        AppSettingsStateService.getInstance().loadState(settings);
    }

    @Override
    public void reset() {
        txtMedataPath.setText(settings.metadataPath);
    }

    @Override
    public void disposeUIResources() {
        contentPanel.setEnabled(false);
        settings = null;
    }
}
