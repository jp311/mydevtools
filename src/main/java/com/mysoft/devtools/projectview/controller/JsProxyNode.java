package com.mysoft.devtools.projectview.controller;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author hezd   2023/5/26
 */
public class JsProxyNode extends ProjectViewNode<JsProxy> {
    /**
     * Creates an instance of the project view node.
     *
     * @param project      the project containing the node.
     * @param jsProxy      the object (for example, a PSI element) represented by the project view node
     * @param viewSettings the settings of the project view.
     */
    protected JsProxyNode(Project project, @NotNull JsProxy jsProxy, ViewSettings viewSettings) {
        super(project, jsProxy, viewSettings);
    }

    @Override
    public boolean contains(@NotNull VirtualFile file) {
        return false;
    }

    @Override
    public @NotNull Collection<? extends AbstractTreeNode<?>> getChildren() {
        return null;
    }

    @Override
    protected void update(@NotNull PresentationData presentation) {

    }
}
