package com.mysoft.devtools.utils.psi;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;

/**
 * <a href="https://plugins.jetbrains.com/docs/intellij/run-configuration-execution.html#displaying-process-output">...</a>
 *
 * @author hezd 2023/5/31
 */
public class IdeaConsoleUtil {
    public static void test(Project project) {
        ConsoleView console = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        console.attachToProcess(null);
        console.print("", ConsoleViewContentType.ERROR_OUTPUT);
    }
}
