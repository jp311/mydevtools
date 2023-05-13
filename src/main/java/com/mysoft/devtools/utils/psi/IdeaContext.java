package com.mysoft.devtools.utils.psi;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;

/**
 * @author hezd   2023/5/4
 */
public class IdeaContext {
    public static Project getProject(){
        return  CommonDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext());
    }
}
