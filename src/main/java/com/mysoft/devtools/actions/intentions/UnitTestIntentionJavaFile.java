package com.mysoft.devtools.actions.intentions;

import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.util.IncorrectOperationException;
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
        return "AI生成单元测试";
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "AI生成单元测试";
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
        //String unittestCode = AITextGenerationClient.getInstance().invoke(psiClass.getText());

        PsiMethod[] methods = psiClass.getMethods();
        BackgroundJobUtil.run(new UnitTestBackgroundJob(project, methods));

        //System.out.println(unittestCode);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
