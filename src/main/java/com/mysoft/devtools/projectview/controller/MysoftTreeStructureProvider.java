package com.mysoft.devtools.projectview.controller;

import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassOwner;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * <see>FormMergerTreeStructureProvider</see>
 *
 * @author hezd   2023/5/23
 */
public class MysoftTreeStructureProvider implements TreeStructureProvider {
    private final Project project;

    public MysoftTreeStructureProvider(Project project) {
        this.project = project;
    }

    @Override
    public @NotNull Collection<AbstractTreeNode<?>> modify(@NotNull AbstractTreeNode<?> parent, @NotNull Collection<AbstractTreeNode<?>> children, ViewSettings settings) {
        ProjectViewNode<?>[] copy = children.toArray(new ProjectViewNode[0]);
        for (ProjectViewNode<?> element : copy) {
            PsiClass psiClass = null;
            if (element.getValue() instanceof PsiClass) {
                psiClass = (PsiClass) element.getValue();
            } else if (element.getValue() instanceof PsiClassOwner) {
                final PsiClass[] psiClasses = ((PsiClassOwner) element.getValue()).getClasses();
                if (psiClasses.length == 1) {
                    psiClass = psiClasses[0];
                }
            }

            if (psiClass == null) continue;
            String qName = psiClass.getQualifiedName();
            if (qName == null) continue;

            children.add(new JsProxyNode(project, null, null));
        }
        return children;
    }
}
