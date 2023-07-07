package com.mysoft.devtools.utils.idea;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;

/**
 * @author hezd   2023/7/7
 */
public class BackgroundJobUtil {
    public static void run(Task.Backgroundable job) {
        ApplicationManager.getApplication().invokeLater(() -> {
            ProgressManager.getInstance().run(job);
        });
    }
}
