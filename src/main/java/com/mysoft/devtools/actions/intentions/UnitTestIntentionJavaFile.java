package com.mysoft.devtools.actions.intentions;

import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.util.IncorrectOperationException;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.jobs.UnitTestBackgroundJob;
import com.mysoft.devtools.utils.idea.BackgroundJobUtil;
import org.jetbrains.annotations.NotNull;

/**
 * 单元测试意图
 *
 * @author hezd   2023/7/5
 */
public class UnitTestIntentionJavaFile extends JavaFileBaseIntention {
    @Override
    public @IntentionName @NotNull String getText() {
        return LocalBundle.message("devtools.ai.backgroundjob.unittest.title");
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return LocalBundle.message("devtools.ai.backgroundjob.unittest.title");
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {

        PsiClass psiClass = getPsiClass(project, editor, file);
        if (psiClass == null) {
            return;
        }

        PsiMethod[] methods = psiClass.getMethods();
        BackgroundJobUtil.run(new UnitTestBackgroundJob(project, methods));
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
