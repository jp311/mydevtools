package com.mysoft.devtools.listeners;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.mysoft.devtools.inspections.OverrideInspection;
import org.jetbrains.annotations.NotNull;

/**
 * IDEA 启动活动监听器（注：runActivity触发时插件可能还没有加载完成）
 *
 * @author hezd 2023/5/26
 */
public class MyStartupActivity implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {

        OverrideInspection.doOverride(project);

//        Application application = ApplicationManager.getApplication();
//        MessageBusConnection connect = application.getMessageBus().connect();
//        connect.subscribe(ProjectManager.TOPIC, new ProjectManagerListener() {
//            @Override
//            public void projectOpened(@NotNull Project project) {
//                OverrideInspection.doOverride(project);
//            }
//        });
    }
}
