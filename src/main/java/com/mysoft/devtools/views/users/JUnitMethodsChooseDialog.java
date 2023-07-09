package com.mysoft.devtools.views.users;

import com.intellij.ide.scopeView.nodes.BasePsiNode;
import com.intellij.psi.PsiElement;
import com.intellij.ui.components.JBTreeTable;
import com.intellij.ui.treeStructure.treetable.ListTreeTableModelOnColumns;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.xml.ui.BooleanColumnInfo;
import com.intellij.util.xml.ui.StringColumnInfo;

import javax.swing.*;

/**
 * @author hezd   2023/7/9
 */
public class JUnitMethodsChooseDialog {
    private JPanel contentPanel;
    private JBTreeTable treeClass;

    private void createUIComponents() {

        BasePsiNode<PsiElement> node = new BasePsiNode<>(null);
        ColumnInfo[] cols = new ColumnInfo[3];
        cols[0] = new BooleanColumnInfo("选择");
        cols[1] = new StringColumnInfo("类型");
        cols[2] = new StringColumnInfo("名称");

        ListTreeTableModelOnColumns model = new ListTreeTableModelOnColumns(node, cols);
        model.setSortable(true);

        treeClass = new JBTreeTable(model);

    }
}
