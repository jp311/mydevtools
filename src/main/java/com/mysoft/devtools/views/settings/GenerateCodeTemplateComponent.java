package com.mysoft.devtools.views.settings;

import com.intellij.openapi.project.Project;
import com.intellij.ui.ContextHelpLabel;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.components.JBTextField;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.controls.FreeMarkerEditor;
import com.mysoft.devtools.dtos.MysoftSettingsDTO;
import com.mysoft.devtools.services.AppSettingsStateService;
import com.mysoft.devtools.utils.psi.IdeaContext;
import com.mysoft.devtools.utils.psi.IdeaNotifyUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * @author hezd 2023/4/30
 */
public class GenerateCodeTemplateComponent extends BaseSettingsComponent {
    private JPanel contentPanel;
    private JBTabbedPane tabPanel;
    private JBTextField txtAuthor;
    private ContextHelpLabel help;

    private FreeMarkerEditor etEntity;

    private FreeMarkerEditor etJsProxy;

    private FreeMarkerEditor etEntityDto;

    private MysoftSettingsDTO settings = AppSettingsStateService.getInstance().getState();

    @Override
    public JComponent getContentPanel() {
        Project project = IdeaContext.getActiveProject();

        JPanel entityPanel = new JPanel();
        entityPanel.setLayout(new BorderLayout());
        etEntity = new FreeMarkerEditor(project, settings.entityTemplate);
        entityPanel.add(etEntity);
        tabPanel.addTab("Entity模板", null, entityPanel, "该模板控制Entity生成格式");

        JPanel entityDtoPanel = new JPanel();
        entityDtoPanel.setLayout(new BorderLayout());
        etEntityDto = new FreeMarkerEditor(project, settings.entityDtoTemplate);
        entityDtoPanel.add(etEntityDto);
        tabPanel.addTab("EntityDto模板", null, entityDtoPanel, "该模板控制EntityDto生成格式");

        JPanel jsProxyPanel = new JPanel();
        jsProxyPanel.setLayout(new BorderLayout());
        etJsProxy = new FreeMarkerEditor(project, settings.jsProxyTemplate);
        jsProxyPanel.add(etJsProxy);
        tabPanel.addTab("JSProxy模板", null, jsProxyPanel, "该模板控制JS代理类生成格式");


        contentPanel.setBorder(null);

        txtAuthor.setText(settings.author);
        return contentPanel;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return etEntity;
    }

    @Override
    public boolean isModified() {
        return !Objects.equals(settings.author, txtAuthor.getText())
                || !Objects.equals(settings.entityTemplate, etEntity.getText())
                || !Objects.equals(settings.entityDtoTemplate, etEntityDto.getText())
                || !Objects.equals(settings.jsProxyTemplate, etJsProxy.getText());
    }

    @Override
    public void apply() {
        settings.author = txtAuthor.getText();
        settings.entityTemplate = etEntity.getText();
        settings.entityDtoTemplate = etEntityDto.getText();
        settings.jsProxyTemplate = etJsProxy.getText();

        AppSettingsStateService.getInstance().loadState(settings);
    }

    @Override
    public void reset() {
        txtAuthor.setText(settings.author);
        etEntity.setText(settings.entityTemplate);
        etJsProxy.setText(settings.jsProxyTemplate);
    }

    @Override
    public void disposeUIResources() {
        settings = null;
        contentPanel.setEnabled(false);
    }

    private void createUIComponents() {
        help = ContextHelpLabel.createWithLink(LocalBundle.message("devtools.settings.generatecodetemplate.title")
                , LocalBundle.message("devtools.settings.generatecodetemplate.description")
                , LocalBundle.message("devtools.settings.generatecodetemplate.help"), () -> {
                    try {
                        if (!Desktop.isDesktopSupported()) {
                            IdeaNotifyUtil.dialogError("Desktop is not supported");
                            return;
                        }
                        Desktop.getDesktop().browse(new URI("http://freemarker.foofun.cn/pgui_config_sharedvariables.html"));
                    } catch (IOException | URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                });

    }
}
