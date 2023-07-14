package com.mysoft.devtools.actions.intentions;

import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.jobs.UnitTestBackgroundJob;
import com.mysoft.devtools.utils.idea.BackgroundJobUtil;
import com.mysoft.devtools.utils.idea.psi.PsiClassExtension;
import com.mysoft.devtools.utils.idea.psi.PsiMethodExtension;
import com.mysoft.devtools.views.users.JUnitMethodsChooseDialog;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

/**
 * 单元测试意图
 *
 * @author hezd   2023/7/5
 */
@ExtensionMethod({PsiClassExtension.class, PsiMethodExtension.class})
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
        int offset = editor.getCaretModel().getOffset();
        PsiElement psiElement = file.findElementAt(offset);
        if (psiElement instanceof PsiIdentifier) {
            if (psiElement.getParent() instanceof PsiClass) {
                PsiClass psiClass = (PsiClass) psiElement.getParent();
                return !psiClass.isAbstract() && psiClass.isPublic();
            }

            if (psiElement.getParent() instanceof PsiMethod) {
                PsiMethod psiMethod = (PsiMethod) psiElement.getParent();
                return !psiMethod.isAbstract() && psiMethod.isPublic();
            }
        }
        return false;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        //这里如何获取光标出PsiElement
        int offset = editor.getCaretModel().getOffset();
        PsiElement psiElement = file.findElementAt(offset);

        if (psiElement instanceof PsiIdentifier) {
            if (psiElement.getParent() instanceof PsiClass) {

                ApplicationManager.getApplication().invokeLater(() -> {
                    new JUnitMethodsChooseDialog(project, file.getVirtualFile()).show();
                });
            }

            if (psiElement.getParent() instanceof PsiMethod) {
                PsiMethod psiMethod = (PsiMethod) psiElement.getParent();
                BackgroundJobUtil.run(new UnitTestBackgroundJob(project, psiMethod));
            }
        }

        if (psiElement instanceof PsiMethod) {
            BackgroundJobUtil.run(new UnitTestBackgroundJob(project, (PsiMethod) psiElement));
        }
        if (psiElement instanceof PsiClass) {
            new JUnitMethodsChooseDialog(project, psiElement);
        }
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
