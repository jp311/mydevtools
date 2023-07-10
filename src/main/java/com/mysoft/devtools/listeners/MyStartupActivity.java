package com.mysoft.devtools.listeners;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.startup.StartupManager;
import com.mysoft.devtools.inspections.OverrideInspection;
import com.mysoft.devtools.jobs.CheckUpdateBackgroundJob;
import com.mysoft.devtools.utils.idea.BackgroundJobUtil;
import org.jetbrains.annotations.NotNull;

/**
 * IDEA 启动活动监听器（注：runActivity触发时插件可能还没有加载完成）
 *
 * @author hezd 2023/5/26
 */
public class MyStartupActivity implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {

        //2022.3.3 开始已过时
        StartupManager.getInstance(project).runWhenProjectIsInitialized(() -> {
            OverrideInspection.doOverride(project);
            BackgroundJobUtil.run(new CheckUpdateBackgroundJob(project));
            //ActionManager.getInstance().registerAction("UnitTestAction", new UnitTestAction());
        });

        //2023.1.3 已标记内部方法
//        StartupManager.getInstance(project).runAfterOpened(() -> {
//            OverrideInspection.doOverride(project);
//        });
    }
}
