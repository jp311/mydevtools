package com.mysoft.devtools.listeners;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.mysoft.devtools.Inspections.OverrideInspection;
import org.jetbrains.annotations.NotNull;

/**
 * @author hezd 2023/5/26
 */
public class MyStartupActivity implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        OverrideInspection.doOverride(project);
    }
}
