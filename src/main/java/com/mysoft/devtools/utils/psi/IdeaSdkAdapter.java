package com.mysoft.devtools.utils.psi;

import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiParserFacade;

import java.lang.reflect.Method;

/**
 * @author hezd 2023/5/14
 */
public class IdeaSdkAdapter {
    public static PsiParserFacade getPsiParserFacade(Project project) {
        PsiParserFacade result = null;
        ApplicationInfo applicationInfo = ApplicationInfo.getInstance();

        Class<?> psiParserFacadeClass;
        try {
            if (applicationInfo.getBuild().getBaselineVersion() >= 222) {
                psiParserFacadeClass = Class.forName("com.intellij.psi.PsiParserFacade");
            } else {
                psiParserFacadeClass = Class.forName("com.intellij.psi.PsiParserFacade$SERVICE");
            }
            Method getInstanceMethod = psiParserFacadeClass.getMethod("getInstance",Project.class);
            result = (PsiParserFacade) getInstanceMethod.invoke(null,project);
        } catch (Exception e) {
            IdeaNotifyUtil.dialogError(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}
