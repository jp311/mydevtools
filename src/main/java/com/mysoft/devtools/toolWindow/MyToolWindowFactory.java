package com.mysoft.devtools.toolWindow;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.mysoft.devtools.utils.idea.IdeaSdkAdapter;
import com.mysoft.devtools.views.toolwindows.MiddlewareToolWindow;
import org.jetbrains.annotations.NotNull;

/**
 * <a href="https://plugins.jetbrains.com/docs/intellij/tool-windows.html">...</a>
 *
 * @author hezd 2023/4/22
 */
public class MyToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        MiddlewareToolWindow window = new MiddlewareToolWindow();
        Content content = IdeaSdkAdapter.getContentFactory(project).createContent(window.getContentPanel(), window.getDisplayName(), window.isLockable());
        toolWindow.getContentManager().addContent(content);
    }
}
