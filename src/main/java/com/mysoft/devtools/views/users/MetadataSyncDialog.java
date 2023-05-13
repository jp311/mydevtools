package com.mysoft.devtools.views.users;

import com.intellij.ui.ContextHelpLabel;

import javax.swing.*;
import java.awt.*;

/**
 * @author hezd   2023/5/7
 */
public class MetadataSyncDialog extends BaseDialogComponent {
    private JPanel contentPanel;
    private ContextHelpLabel contextHelpLabel1;

    public MetadataSyncDialog() {
        setTitle("元数据同步");
        init();
        contentPanel.setPreferredSize(new Dimension(600, 400));
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

        contextHelpLabel1 = ContextHelpLabel.createWithLink("元数据同步", "向目标服务器同步元数据，如需创建新数据源点击下方超链接", "创建新数据源", () -> {
            System.out.printf("", "");
        });
    }
}
