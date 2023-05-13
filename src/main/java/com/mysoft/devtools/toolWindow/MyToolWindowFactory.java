//package com.mysoft.devtools.toolWindow;
//
//import com.intellij.openapi.project.DumbAware;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.wm.ToolWindow;
//import com.intellij.openapi.wm.ToolWindowFactory;
//import com.intellij.ui.content.Content;
//import com.intellij.ui.content.ContentFactory;
//import com.mysoft.devtools.views.toolwindows.RegexReplaceToolWindow;
//import org.jetbrains.annotations.NotNull;
//
///**
// * <a href="https://plugins.jetbrains.com/docs/intellij/tool-windows.html">...</a>
// *
// * @author hezd 2023/4/22
// */
//public class MyToolWindowFactory implements ToolWindowFactory, DumbAware {
//
//    @Override
//    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
//        RegexReplaceToolWindow window = new RegexReplaceToolWindow();
//        Content content = ContentFactory.SERVICE.getInstance().createContent(window.getContentPanel(), "", false);
//        toolWindow.getContentManager().addContent(content);
//    }
//}
