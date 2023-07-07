package com.mysoft.devtools.actions.intentions;

import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.mysoft.devtools.aimodeling.AITextGenerationClient;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * 单元测试意图
 *
 * @author hezd   2023/7/5
 */
public class UnitTestIntentionJavaFile extends JavaFileBaseIntention {
    @Override
    public @IntentionName @NotNull String getText() {
        return "AI生成测试";
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "AI生成测试";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {

        try {
            PsiClass psiClass = getPsiClass(project, editor, file);
            String unittestCode = AITextGenerationClient.getInstance().invoke(psiClass.getText());
            System.out.println(unittestCode);
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
