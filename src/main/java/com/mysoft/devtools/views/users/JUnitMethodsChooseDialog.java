package com.mysoft.devtools.views.users;

import com.intellij.icons.AllIcons;
import com.intellij.ide.CommonActionsManager;
import com.intellij.ide.DefaultTreeExpander;
import com.intellij.ide.TreeExpander;
import com.intellij.ide.ui.search.SearchUtil;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassImpl;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.ui.*;
import com.intellij.util.ui.UIUtil;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.jobs.UnitTestBackgroundJob;
import com.mysoft.devtools.utils.idea.BackgroundJobUtil;
import com.mysoft.devtools.utils.idea.IdeaNotifyUtil;
import com.mysoft.devtools.utils.idea.psi.PsiClassExtension;
import com.mysoft.devtools.utils.idea.psi.PsiDirectoryExtension;
import com.mysoft.devtools.utils.idea.psi.PsiMethodExtension;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
import java.util.stream.Collectors;

/**
 * @author hezd   2023/7/9
 */
@ExtensionMethod({PsiClassExtension.class, PsiMethodExtension.class, PsiDirectoryExtension.class})
public class JUnitMethodsChooseDialog extends BaseDialogComponent {
    private JPanel contentPanel;
    private final Object context;
    private final Project project;

    private CheckboxTree myTree;

    private final List<CheckedTreeNode> datas = new ArrayList<>();

    private TreeSpeedSearch treeSpeedSearch = null;

    public JUnitMethodsChooseDialog(Project project, Object context) {
        this.context = context;
        this.project = project;
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


        myTree = new CheckboxTree(new CheckboxTree.CheckboxTreeCellRenderer(true) {
            @Override
            public void customizeRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                SimpleTextAttributes attributes = SimpleTextAttributes.REGULAR_ATTRIBUTES;
                Color background = UIUtil.getTreeBackground(selected, true);
                UIUtil.changeBackGround(this, background);

                if (!(value instanceof MyCheckedTreeNode)) {
                    return;
                }

                ColoredTreeCellRenderer textRenderer = getTextRenderer();
                MyCheckedTreeNode node = (MyCheckedTreeNode) value;

                String text = node.getName();
                String hint = node.getHint();
                textRenderer.setIcon(node.getIcon());

                String filterString = null;
                if (treeSpeedSearch != null) {
                    filterString = treeSpeedSearch.getEnteredPrefix();
                }
                SearchUtil.appendFragments(filterString,
                        text,
                        attributes.getStyle(),
                        attributes.getFgColor(),
                        background,
                        textRenderer);

                if (hint != null) {
                    SearchUtil.appendFragments(null,
                            " " + hint,
                            SimpleTextAttributes.GRAYED_ATTRIBUTES.getStyle(),
                            SimpleTextAttributes.GRAYED_ATTRIBUTES.getFgColor(),
                            background,
                            textRenderer);
                }

            }
        }, root);

        treeSpeedSearch = new TreeSpeedSearch(myTree) {
            @Override
            protected boolean isMatchingElement(Object element, String pattern) {
                if (element instanceof TreePath) {
                    Object lastComponent = ((TreePath) element).getLastPathComponent();
                    if (lastComponent instanceof DefaultMutableTreeNode) {
                        Object userObject = ((DefaultMutableTreeNode) lastComponent).getUserObject();
                        if (userObject instanceof PsiClass) {
                            String pkg = ((PsiClass) userObject).getName();
                            return pkg != null && pkg.contains(pattern);
                        }

                        if (userObject instanceof PsiMethod) {
                            String pkg = ((PsiMethod) userObject).getName();
                            return pkg.contains(pattern);
                        }
                    }
                }
                return false;
            }
        };

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

        DefaultActionGroup group = new DefaultActionGroup();
        CommonActionsManager actionManager = CommonActionsManager.getInstance();
        TreeExpander treeExpander = new DefaultTreeExpander(myTree);

        group.add(actionManager.createExpandAllAction(treeExpander, myTree));
        group.add(actionManager.createCollapseAllAction(treeExpander, myTree));

        ActionToolbar treeToolbar = ActionManager.getInstance().createActionToolbar("JUnit5TestTree", group, true);
        treeToolbar.setTargetComponent(myTree);


        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(myTree);

        JComponent toolbarComponent = treeToolbar.getComponent();
        Dimension preferredSize = toolbarComponent.getPreferredSize();
        preferredSize.height = 50;

        Panel toolbarPanel = new Panel();
        toolbarPanel.setPreferredSize(preferredSize);
        toolbarPanel.setLayout(new BorderLayout());
        toolbarPanel.setBackground(contentPanel.getBackground());
        toolbarPanel.add(toolbarComponent, BorderLayout.CENTER);

        contentPanel.add(toolbarPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        return contentPanel;
    }

    /**
     * 获取当前module下所有类及公开非抽象的方法
     */
    private MyCheckedTreeNode loadCurrentModuleClasses() {
        MyCheckedTreeNode root = new MyCheckedTreeNode(null);
        Collection<VirtualFile> virtualFiles;
        if (context instanceof PsiDirectory) {
            PsiDirectory directory = (PsiDirectory) context;

            virtualFiles = directory.getVirtualFiles(PsiJavaFile.class);

        } else if (context instanceof VirtualFile) {
            virtualFiles = new ArrayList<>();
            virtualFiles.add((VirtualFile) context);
        } else {
            throw new RuntimeException("unknown context type!");
        }

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
                    root.add(classNode);
                }
            }
        }
        return root;
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
    }
}
