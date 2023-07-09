package com.mysoft.devtools.utils.idea;

import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiParserFacade;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.terminal.ShellTerminalWidget;

import java.lang.reflect.Method;

/**
 * @author hezd 2023/5/14
 */
public class IdeaSdkAdapter {
    private final static int MIN_SDK = 222;

    public static PsiParserFacade getPsiParserFacade(@NotNull Project project) {
        PsiParserFacade result = null;
        ApplicationInfo applicationInfo = ApplicationInfo.getInstance();

        Class<?> psiParserFacadeClass;
        try {
            if (applicationInfo.getBuild().getBaselineVersion() >= MIN_SDK) {
                psiParserFacadeClass = Class.forName("com.intellij.psi.PsiParserFacade");
            } else {
                psiParserFacadeClass = Class.forName("com.intellij.psi.PsiParserFacade$SERVICE");
            }
            Method instance = psiParserFacadeClass.getMethod("getInstance", Project.class);
            result = (PsiParserFacade) instance.invoke(null, project);
        } catch (Exception e) {
            IdeaNotifyUtil.dialogError(e.getMessage());
        }
        return result;
    }

    public static ContentFactory getContentFactory(@NotNull Project project) {
        ContentFactory result = null;
        ApplicationInfo applicationInfo = ApplicationInfo.getInstance();

        Class<?> contentFactoryClass;
        try {
            if (applicationInfo.getBuild().getBaselineVersion() >= MIN_SDK) {
                contentFactoryClass = Class.forName("com.intellij.ui.content.ContentFactory");
            } else {
                contentFactoryClass = Class.forName("com.intellij.ui.content.ContentFactory$SERVICE");
            }
            Method instance = contentFactoryClass.getMethod("getInstance", Project.class);
            result = (ContentFactory) instance.invoke(null, project);
        } catch (Exception e) {
            IdeaNotifyUtil.dialogError(e.getMessage());
        }
        return result;
    }

    public static ShellTerminalWidget createLocalShellWidget(@NotNull Project project, String workspacePath, String title) {
        ApplicationInfo applicationInfo = ApplicationInfo.getInstance();
        Class<?> managerClass;
        try {
            if (applicationInfo.getBuild().getBaselineVersion() >= 231) {
                managerClass = Class.forName("org.jetbrains.plugins.terminal.TerminalToolWindowManager");
            } else {
                managerClass = Class.forName("org.jetbrains.plugins.terminal.TerminalView");
            }

            Method instanceMethod = managerClass.getMethod("getInstance", Project.class);
            Object managerInstance = instanceMethod.invoke(null, project);
            Method createLocalShellWidgetMethod = managerInstance.getClass().getMethod("createLocalShellWidget", String.class, String.class);
            return (ShellTerminalWidget) createLocalShellWidgetMethod.invoke(managerInstance, workspacePath, title);
        } catch (Exception e) {
            IdeaNotifyUtil.dialogError(e.getMessage());
        }
        throw new RuntimeException("create ShellTerminalWidget fail!");
    }

}
