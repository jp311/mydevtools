package com.mysoft.devtools.views.users;

import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.utils.InspectionWhiteUtil;
import com.mysoft.devtools.utils.psi.IdeaNotifyUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * @author hezd   2023/5/17
 */
public class InspectionWhiteDialog extends BaseDialogComponent {
    private JPanel contentPanel;
    private JTextField txtName;
    private JRadioButton rbPackage;
    private JRadioButton rbName;
    private JTextField txtPackage;

    private final String scope;

    public InspectionWhiteDialog(String scope, String qualifiedName, String packageName) {
        setTitle(LocalBundle.message("devtools.userview.white.dialogtitle"));
        init();

        txtName.setText(qualifiedName);
        txtPackage.setText(packageName);
        contentPanel.setPreferredSize(new Dimension(600, 200));

        this.scope = scope;
    }

    @Override
    protected JComponent createCenterPanel() {
        return contentPanel;
    }

    @Override
    protected void doOKAction() {
        if (rbPackage.isSelected()) {
            try {
                InspectionWhiteUtil.appendPackage(scope, txtPackage.getText());
                close(0, true);
                return;
            } catch (IOException e) {
                IdeaNotifyUtil.dialogError(e.getMessage());
            }
        }
        if (rbName.isSelected()) {
            try {
                InspectionWhiteUtil.appendName(scope, txtName.getText());
                close(0, true);
            } catch (IOException e) {
                IdeaNotifyUtil.dialogError(e.getMessage());
            }
        }
    }
}
