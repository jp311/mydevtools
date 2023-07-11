package com.mysoft.devtools.views.users;

import com.intellij.codeInsight.intention.impl.config.IntentionActionMetaData;
import com.intellij.ide.ui.search.SearchUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.ui.CheckboxTree;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.FilterComponent;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.JBViewport;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.UIUtil;
import com.mysoft.devtools.bundles.LocalBundle;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

/**
 * @author hezd   2023/7/9
 */
public class JUnitMethodsChooseDialog extends BaseDialogComponent {
    private JPanel contentPanel;
    private Tree tree1;
    private JBViewport report;
    private Module module;
    private Project project;

    private FilterComponent myFilter;

    public JUnitMethodsChooseDialog(Module module) {
        this.module = module;
        this.project = module.getProject();

        setTitle(LocalBundle.message("devtools.ai.backgroundjob.unittest.title"));
        init();
        contentPanel.setPreferredSize(new Dimension(960, 600));
    }

    private static String getNodeText(CheckedTreeNode node) {
        final Object userObject = node.getUserObject();
        String text;
        if (userObject instanceof String) {
            text = (String) userObject;
        } else if (userObject instanceof IntentionActionMetaData) {
            text = ((IntentionActionMetaData) userObject).getFamily();
        } else {
            text = "???";
        }
        return text;
    }

    @Override
    protected JComponent createCenterPanel() {
        tree1.setCellRenderer(new CheckboxTree.CheckboxTreeCellRenderer(true, false) {
            @Override
            public void customizeRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                if (!(value instanceof CheckedTreeNode)) {
                    return;
                }

                CheckedTreeNode node = (CheckedTreeNode) value;
                SimpleTextAttributes attributes = node.getUserObject() instanceof IntentionActionMetaData ? SimpleTextAttributes.REGULAR_ATTRIBUTES : SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES;
                final String text = getNodeText(node);
                Color background = UIUtil.getTreeBackground(selected, true);
                UIUtil.changeBackGround(this, background);
                SearchUtil.appendFragments(myFilter != null ? myFilter.getFilter() : null,
                        text,
                        attributes.getStyle(),
                        attributes.getFgColor(),
                        background,
                        getTextRenderer());
            }
        });

        CheckedTreeNode root = new CheckedTreeNode("Root");
        root.add(new CheckedTreeNode("测试1"));

        DefaultTreeModel model = new DefaultTreeModel(root);
        tree1.setModel(model);
        tree1.setShowsRootHandles(true);
        //IntentionSettingsTree.java
        return contentPanel;
    }


}
