package com.mysoft.devtools.utils.psi;

import com.intellij.openapi.project.Project;
import org.jetbrains.plugins.terminal.TerminalView;

import java.io.IOException;

/**
 * <a href="https://www.jianshu.com/p/964f0e9d870c">...</a>
 *
 * @author hezd 2023/6/8
 */
public class IdeaTerminalUtil {
    public static void execute(Project project, String cmd) throws IOException {
        TerminalView.getInstance(project)
                    .createLocalShellWidget(project.getBasePath(), "Terminal")
                    .executeCommand(cmd);
    }

    public static void execute(Project project,String workspacePath, String cmd) throws IOException {
        TerminalView.getInstance(project)
                    .createLocalShellWidget(workspacePath, "Terminal")
                    .executeCommand(cmd);
    }
}
