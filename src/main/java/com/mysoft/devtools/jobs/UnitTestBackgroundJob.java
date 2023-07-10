package com.mysoft.devtools.jobs;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.mysoft.devtools.aimodeling.AITextGenerationClient;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.dtos.MyAnAction;
import com.mysoft.devtools.utils.idea.BackgroundJobUtil;
import com.mysoft.devtools.utils.idea.IdeaNotifyUtil;
import com.mysoft.devtools.utils.idea.UnitTestUtil;
import com.mysoft.devtools.utils.idea.psi.PsiEditorExtension;
import com.mysoft.devtools.utils.idea.psi.PsiMethodExtension;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;

/**
 * AI生成单元测试后台任务
 *
 * @author hezd   2023/7/7
 */
@ExtensionMethod({PsiMethodExtension.class})
public class UnitTestBackgroundJob extends Task.Backgroundable {
    private PsiMethod[] methods;

    public UnitTestBackgroundJob(@Nullable Project project, PsiMethod... methods) {
        super(project, LocalBundle.message("devtools.ai.backgroundjob.unittest.title"));
        this.methods = methods;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        Project project = this.getProject();

        for (int i = 0; i < methods.length; i++) {
            PsiMethod method = methods[i];

            String simpleName = ApplicationManager.getApplication().runReadAction((Computable<String>) () -> method.getSimpleName());
            String methodCode = ApplicationManager.getApplication().runReadAction((Computable<String>) method::getText);


            try {
                indicator.setFraction((double) (i + 1) / methods.length);
                indicator.setText(MessageFormat.format("Processing:{0}/{1}", (i + 1), methods.length));
                indicator.setText2(LocalBundle.message("devtools.ai.backgroundjob.unittest.title") + "：" + simpleName);

                String unittestCode = AITextGenerationClient.getInstance().invoke(methodCode);

                ApplicationManager.getApplication().invokeLater(() -> {
                    ApplicationManager.getApplication().runWriteAction(() -> {
                        PsiClass testPsiClass = UnitTestUtil.createOrOpenTestPsiClass(method.getContainingClass());
                        PsiEditorExtension.openInEditor(project, testPsiClass.getContainingFile().getVirtualFile());
                        UnitTestUtil.appendCode(testPsiClass, unittestCode);
                    });
                });

                IdeaNotifyUtil.notifyInfo(LocalBundle.message("devtools.ai.backgroundjob.unittest.title") + "成功", simpleName, project);
            } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
                String content = MessageFormat.format("方法：{0}<br/>原因：{1}", simpleName, e.getMessage());

                Notification notification = new Notification(
                        "mysoft-balloon",
                        LocalBundle.message("devtools.ai.backgroundjob.unittest.title") + "失败",
                        content,
                        NotificationType.ERROR
                );

                notification.addAction(new MyAnAction<>("重试", method) {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e) {
                        PsiMethod tag = this.getTag();
                        BackgroundJobUtil.run(new UnitTestBackgroundJob(project, tag));
                        notification.expire();
                    }
                });

                Notifications.Bus.notify(notification);
            }
        }
    }
}
