package com.mysoft.devtools.views.users;

import com.intellij.icons.AllIcons;
import com.intellij.ide.CommonActionsManager;
import com.intellij.ide.DefaultTreeExpander;
import com.intellij.ide.TreeExpander;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassImpl;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.*;
import com.intellij.util.ui.UIUtil;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.jobs.UnitTestBackgroundJob;
import com.mysoft.devtools.utils.idea.BackgroundJobUtil;
import com.mysoft.devtools.utils.idea.IdeaNotifyUtil;
import com.mysoft.devtools.utils.idea.psi.PsiClassExtension;
import com.mysoft.devtools.utils.idea.psi.PsiMethodExtension;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hezd   2023/7/9
 */
@ExtensionMethod({PsiClassExtension.class, PsiMethodExtension.class})
public class JUnitMethodsChooseDialog extends BaseDialogComponent {
    private JPanel contentPanel;
    private final Module module;
    private final Project project;

    private CheckboxTree myTree;
    private SearchTextField txtFilter;
    private JPanel toolbarPanel;

    private final List<CheckedTreeNode> datas = new ArrayList<>();

    private final MyCheckedTreeNode rootNode = new MyCheckedTreeNode("root");

    public JUnitMethodsChooseDialog(Module module) {
        this.module = module;
        this.project = module.getProject();

        setTitle(LocalBundle.message("devtools.ai.backgroundjob.unittest.title"));
        init();
        contentPanel.setPreferredSize(new Dimension(800, 600));
    }

    @Override
    protected void doOKAction() {
        List<PsiMethod> selectMethods = datas.stream().filter(CheckedTreeNode::isChecked)
                .map(x -> (PsiMethod) x.getUserObject())
                .collect(Collectors.toList());

        if (selectMethods.size() == 0) {
            IdeaNotifyUtil.dialogError(LocalBundle.message("devtools.ai.backgroundjob.unittest.validate.nodata"));
            return;
        }
        BackgroundJobUtil.run(new UnitTestBackgroundJob(project, selectMethods.toArray(new PsiMethod[0])));
        close(0);
    }

    @Override
    protected JComponent createCenterPanel() {
        MyCheckedTreeNode root = loadCurrentModuleClasses();
        myTree.setModel(new DefaultTreeModel(root));

        DefaultActionGroup group = new DefaultActionGroup();
        CommonActionsManager actionManager = CommonActionsManager.getInstance();
        TreeExpander treeExpander = new DefaultTreeExpander(myTree);
        group.add(actionManager.createExpandAllAction(treeExpander, myTree));
        group.add(actionManager.createCollapseAllAction(treeExpander, myTree));

        ActionToolbar treeToolbar = ActionManager.getInstance().createActionToolbar("JUnit5TestTree", group, true);
        treeToolbar.setTargetComponent(myTree);
        toolbarPanel.add(treeToolbar.getComponent(), BorderLayout.CENTER);

        txtFilter.addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                doFilter(rootNode, txtFilter.getText());
            }
        });

        txtFilter.addKeyboardListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                }
            }
        });

        return contentPanel;
    }

    /**
     * 获取当前module下所有类及公开非抽象的方法
     */
    private MyCheckedTreeNode loadCurrentModuleClasses() {
        //获取当前Module下所有java文件
        Collection<VirtualFile> virtualFiles = FileTypeIndex.getFiles(JavaFileType.INSTANCE, GlobalSearchScope.moduleScope(module));
        for (VirtualFile virtualFile : virtualFiles) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
            //保险起见做个判断
            if (!(psiFile instanceof PsiJavaFile)) {
                continue;
            }

            //获取文件的Class
            PsiClass[] classes = ((PsiJavaFile) psiFile).getClasses();
            for (PsiClass psiClass : classes) {
                //排除内部类
                if (!(psiClass.getParent() instanceof PsiJavaFile)) {
                    continue;
                }

                MyCheckedTreeNode classNode = new MyCheckedTreeNode(psiClass);
                classNode.setName(psiClass.getName());
                classNode.setIcon(((PsiClassImpl) psiClass).getElementIcon(Iconable.ICON_FLAG_VISIBILITY));
                classNode.setHint(psiClass.getPackageName());
                classNode.setChecked(false);

                //获取类的所有方法
                PsiMethod[] methods = psiClass.getMethods();
                for (PsiMethod method : methods) {
                    //排除非public 或 abstract
                    if (!method.isPublic() || method.isAbstract()) {
                        continue;
                    }
                    MyCheckedTreeNode methodNode = new MyCheckedTreeNode(method);
                    methodNode.setName(method.getName());
                    if (method instanceof PsiMethodImpl) {
                        methodNode.setIcon(((PsiMethodImpl) method).getIcon(Iconable.ICON_FLAG_VISIBILITY));
                    } else {
                        methodNode.setIcon(AllIcons.Nodes.Method);
                    }

                    methodNode.setHint(method.getSignature());
                    methodNode.setChecked(false);
                    classNode.add(methodNode);
                    datas.add(methodNode);
                }
                //排除没有任何方法的类
                if (classNode.getChildCount() > 0) {
                    rootNode.add(classNode);
                }
            }
        }
        return rootNode;
    }

    private void createUIComponents() {
        myTree = new MyCheckboxTree();

        myTree.setCellRenderer(new CheckboxTree.CheckboxTreeCellRenderer(true) {
            @Override
            public void customizeRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                if (!(value instanceof MyCheckedTreeNode)) {
                    return;
                }

                MyCheckedTreeNode node = (MyCheckedTreeNode) value;

                Color background = UIUtil.getTreeBackground(selected, true);
                UIUtil.changeBackGround(this, background);

                String text = node.getName();
                String hint = node.getHint();

                ColoredTreeCellRenderer textRenderer = getTextRenderer();
                textRenderer.setIcon(node.getIcon());

                if (text != null) {
                    getTextRenderer().append(text, SimpleTextAttributes.REGULAR_ATTRIBUTES);
                }

                if (hint != null) {
                    getTextRenderer().append(" " + text, SimpleTextAttributes.GRAYED_ATTRIBUTES);
                }
            }
        });
    }

    private void doFilter(MyCheckedTreeNode node, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return;
        }
//        int childCount = node.getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            MyCheckedTreeNode childNode = (MyCheckedTreeNode) node.getChildAt(i);
//
//            // 判断节点是否匹配过滤条件
//            childNode.setVisible(childNode.getName().contains(keyword));
//            // 根据匹配结果隐藏或显示节点
//
////            TreePath childPath = new TreePath(childNode.getPath());
////            myTree.getModel().valueForPathChanged(childPath, childNode.getUserObject());
//
//            // 递归过滤子节点
//            doFilter(childNode, keyword);
//        }
    }

    private static final class MyCheckboxTree extends CheckboxTree {

        @Override
        protected void onDoubleClick(CheckedTreeNode treeNode) {
            TreePath treePath = new TreePath(treeNode.getPath());
            Object node = treePath.getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode mutableTreeNode = (DefaultMutableTreeNode) node;
                if (!mutableTreeNode.isLeaf()) {
                    if (this.isExpanded(treePath)) {
                        // 收起节点
                        this.collapsePath(treePath);
                    } else {
                        // 展开节点
                        this.expandPath(treePath);
                    }
                }
            }
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    private static final class MyCheckedTreeNode extends CheckedTreeNode {
        public MyCheckedTreeNode(Object userObject) {
            super(userObject);
        }

        private Icon icon;

        private String hint;

        private String name;

        private boolean visible;
    }
}
