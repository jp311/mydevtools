package com.mysoft.devtools.views.users;

import com.intellij.ide.structureView.StructureView;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.impl.StructureViewFactoryImpl;
import com.intellij.ide.structureView.impl.java.JavaFileTreeModel;
import com.intellij.ide.structureView.newStructureView.StructureViewComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiEditorUtil;
import com.intellij.ui.components.JBViewport;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.utils.idea.IdeaContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * @author hezd   2023/7/9
 */
public class JUnitMethodsChooseDialog extends BaseDialogComponent {
    private JPanel contentPanel;
    private JBViewport report;
    private Module module;
    private Project project;

    public JUnitMethodsChooseDialog(Module module) {
        this.module = module;
        this.project = module.getProject();

        setTitle(LocalBundle.message("devtools.ai.backgroundjob.unittest.title"));
        init();
        contentPanel.setPreferredSize(new Dimension(960, 600));
    }

    private void createUIComponents() {


    }

    @Override
    protected JComponent createCenterPanel() {
        VirtualFile selectedFile = IdeaContext.getSelectedFiles()[0];
        PsiJavaFile psiJavaFile = (PsiJavaFile) PsiManager.getInstance(project).findFile(selectedFile);

        Editor editor = PsiEditorUtil.findEditor(psiJavaFile);
        JavaFileTreeModel model = new JavaFileTreeModel(psiJavaFile, editor);
        FileEditor selectedEditor = FileEditorManager.getInstance(project).getSelectedEditor();
        StructureView structureView = StructureViewFactoryImpl.getInstance(project).createStructureView(selectedEditor, model, project);
        report.setView(structureView.getComponent());
        return contentPanel;
    }

    private final class MyStructureViewComponent extends StructureViewComponent {
        public MyStructureViewComponent() {
            super(null, null, null, false);
        }

        public MyStructureViewComponent(@Nullable FileEditor editor, @NotNull StructureViewModel structureViewModel, @NotNull Project project, boolean showRootNode) {
            super(editor, structureViewModel, project, showRootNode);
        }

        @Override
        public boolean isCycleRoot() {
            return super.isCycleRoot();
        }
    }
}
