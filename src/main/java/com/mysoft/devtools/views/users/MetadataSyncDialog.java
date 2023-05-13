package com.mysoft.devtools.views.users;

import com.intellij.ui.ContextHelpLabel;

import javax.swing.*;
import java.awt.*;

import static com.mysoft.devtools.bundles.LocalBundle.message;

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
        contextHelpLabel1 = ContextHelpLabel.createWithLink(message("devtools.menutools.syncmetadata.title")
                , message("devtools.menutools.syncmetadata.description")
                , message("devtools.menutools.syncmetadata.createdatasource"), System.out::println);
    }
}
