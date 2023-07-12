package com.mysoft.devtools.views.users;

import com.intellij.icons.AllIcons;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.ui.search.SearchUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.*;
import com.intellij.util.ui.UIUtil;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.jobs.UnitTestBackgroundJob;
import com.mysoft.devtools.utils.idea.BackgroundJobUtil;
import com.mysoft.devtools.utils.idea.IdeaNotifyUtil;
import com.mysoft.devtools.utils.idea.psi.PsiMethodExtension;
import lombok.experimental.ExtensionMethod;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author hezd   2023/7/9
 */
@ExtensionMethod({PsiMethodExtension.class})
public class JUnitMethodsChooseDialog extends BaseDialogComponent {
    private JPanel contentPanel;
    private final Module module;
    private final Project project;

    private CheckboxTree myTree;

    public JUnitMethodsChooseDialog(Module module) {
        this.module = module;
        this.project = module.getProject();

        setTitle(LocalBundle.message("devtools.ai.backgroundjob.unittest.title"));
        init();
        contentPanel.setPreferredSize(new Dimension(800, 600));
    }

    @Override
    protected void doOKAction() {
        TreePath[] selectionPaths = myTree.getSelectionModel().getSelectionPaths();
        List<PsiMethod> selectMethods = new ArrayList<>();
        for (TreePath treePath : selectionPaths) {
            Object node = treePath.getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode == false) {
                continue;
            }
            Object userObject = ((DefaultMutableTreeNode) node).getUserObject();
            if (userObject instanceof PsiMethod) {
                PsiMethod method = (PsiMethod) userObject;
                selectMethods.add(method);
            }
        }

        if (selectionPaths.length == 0) {
            IdeaNotifyUtil.dialogError(LocalBundle.message("devtools.ai.backgroundjob.unittest.validate.nodata"));
            return;
        }
        BackgroundJobUtil.run(new UnitTestBackgroundJob(project, selectMethods.toArray(new PsiMethod[0])));
    }

    @Override
    protected JComponent createCenterPanel() {
        CheckedTreeNode root = loadCurrentModuleClasses();
        myTree = new CheckboxTree(new CheckboxTree.CheckboxTreeCellRenderer(true) {
            @Override
            public void customizeRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                SimpleTextAttributes attributes = SimpleTextAttributes.REGULAR_ATTRIBUTES;
                Color background = UIUtil.getTreeBackground(selected, true);
                UIUtil.changeBackGround(this, background);

                if (!(value instanceof CheckedTreeNode)) {
                    return;
                }

                Object userObject = ((CheckedTreeNode) value).getUserObject();
                String text = null;

                ColoredTreeCellRenderer textRenderer = getTextRenderer();

                if (userObject instanceof PsiClass) {
                    text = ((PsiClass) userObject).getName();
                    textRenderer.setIcon(AllIcons.Nodes.Class);
                }

                if (userObject instanceof PsiMethod) {
                    text = ((PsiMethod) userObject).getName();
                    textRenderer.setIcon(AllIcons.Nodes.Method);

                }


                SearchUtil.appendFragments(null,
                        text,
                        attributes.getStyle(),
                        attributes.getFgColor(),
                        background,
                        textRenderer);
            }
        }, root);

        myTree.getSelectionModel().addTreeSelectionListener(e -> {

        });

        //双击展开/折叠子节点
        myTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TreePath treePath = myTree.getPathForLocation(e.getX(), e.getY());
                    if (treePath != null) {
                        Object node = treePath.getLastPathComponent();
                        if (node instanceof DefaultMutableTreeNode) {
                            DefaultMutableTreeNode mutableTreeNode = (DefaultMutableTreeNode) node;
                            if (!mutableTreeNode.isLeaf()) {
                                if (myTree.isExpanded(treePath)) {
                                    // 收起节点
                                    myTree.collapsePath(treePath);
                                } else {
                                    // 展开节点
                                    myTree.expandPath(treePath);
                                }
                            }
                        }
                    }
                }
            }
        });

//        DefaultActionGroup group = new DefaultActionGroup();
//        CommonActionsManager actionManager = CommonActionsManager.getInstance();
//        TreeExpander treeExpander = new DefaultTreeExpander(myTree);
//        group.add(actionManager.createExpandAllAction(treeExpander, myTree));
//        group.add(actionManager.createCollapseAllAction(treeExpander, myTree));
//
//        ActionToolbar treeToolbar = ActionManager.getInstance().createActionToolbar("JUnit5TestTree", group, true);
//        treeToolbar.setTargetComponent(myTree);
//
//        HintUpdateSupply.installDataContextHintUpdateSupply(myTree);

        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(myTree);
        contentPanel.add(scrollPane);
        return contentPanel;
    }

    /**
     * 获取当前module下所有类及公开非抽象的方法
     */
    private CheckedTreeNode loadCurrentModuleClasses() {
        CheckedTreeNode root = new CheckedTreeNode("root");
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

                CheckedTreeNode classNode = new CheckedTreeNode(psiClass);
                classNode.setChecked(false);

                //获取类的所有方法
                PsiMethod[] methods = psiClass.getMethods();
                for (PsiMethod method : methods) {
                    //排除非public 或 abstract
                    if (!method.isPublic() || method.isAbstract()) {
                        continue;
                    }
                    CheckedTreeNode methodNode = new CheckedTreeNode(method);
                    methodNode.setChecked(false);
                    classNode.add(methodNode);
                }
                //排除没有任何方法的类
                if (classNode.getChildCount() > 0) {
                    root.add(classNode);
                }
            }
        }
        return root;
    }
}
