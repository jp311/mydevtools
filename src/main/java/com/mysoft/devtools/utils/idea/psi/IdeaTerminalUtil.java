package com.mysoft.devtools.utils.idea.psi;

import com.intellij.openapi.project.Project;
import org.jetbrains.plugins.terminal.ShellTerminalWidget;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <a href="https://www.jianshu.com/p/964f0e9d870c">...</a>
 *
 * @author hezd 2023/6/8
 */
public class IdeaTerminalUtil {
    private final static Map<String, ShellTerminalWidget> TERMINAL_TOOLWINDOW_TAB_CACHES = new HashMap<>();

    public static void execute(Project project, String workspacePath, String cmd, String title) throws IOException {
        getWidget(project, workspacePath, title).executeCommand(cmd);
    }

    private static ShellTerminalWidget getWidget(Project project, String workspacePath, String title) {
        if (title == null || title.isBlank()) {
            title = "Terminal";
        }

        String key = project.getBasePath() + title;
        if (!TERMINAL_TOOLWINDOW_TAB_CACHES.containsKey(key)) {
            ShellTerminalWidget terminal = IdeaSdkAdapter.createLocalShellWidget(project, workspacePath, title);
            TERMINAL_TOOLWINDOW_TAB_CACHES.put(key, terminal);
            return terminal;
        }

        ShellTerminalWidget shellTerminalWidget = TERMINAL_TOOLWINDOW_TAB_CACHES.get(key);
        if (!shellTerminalWidget.isValid()) {
            ShellTerminalWidget terminal = IdeaSdkAdapter.createLocalShellWidget(project, workspacePath, title);
            TERMINAL_TOOLWINDOW_TAB_CACHES.replace(key, terminal);
            return terminal;
        }
        return shellTerminalWidget;
    }
}
