package com.mysoft.devtools.inspections;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInsight.daemon.HighlightDisplayKey;
import com.intellij.codeInspection.ex.InspectionProfileImpl;
import com.intellij.openapi.project.Project;
import com.intellij.profile.codeInspection.InspectionProfileManager;

import java.util.Arrays;
import java.util.List;

/**
 * @author hezd   2023/5/23
 */
public class OverrideInspection {
    private static boolean IS_OVERRIDE = false;

    public static void doOverride(Project project) {
        if (project == null) {
            return;
        }
        if (project.isDisposed() || !project.isOpen()) {
            return;
        }
        if (IS_OVERRIDE) {
            return;
        }
        InspectionProfileManager manager = InspectionProfileManager.getInstance(project);
        InspectionProfileImpl profile = manager.getCurrentProfile();

        List<String> names = Arrays.asList("EqualsBetweenInconvertibleTypes", "ArrayEquality", "ArrayObjectsEquals", "NewObjectEquality", "NumberEquality", "ObjectEquality", "StringEquality");
        names.forEach(x -> setErrorLevel(profile, project, x));
        IS_OVERRIDE = true;
    }

    private static void setErrorLevel(InspectionProfileImpl profile, Project project, String name) {
        profile.setToolEnabled(name, true);
        profile.setErrorLevel(HighlightDisplayKey.find(name), HighlightDisplayLevel.ERROR, project);
        //profile.setDescription("");
    }
}
