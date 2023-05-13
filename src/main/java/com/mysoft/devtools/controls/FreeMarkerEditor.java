package com.mysoft.devtools.controls;

import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.EditorTextField;

/**
 * @author hezd   2023/5/6
 */
public class FreeMarkerEditor extends EditorTextField {
    public FreeMarkerEditor(Project project, String text) {
        super(text, project, FileTypeManager.getInstance().findFileTypeByName("FTL"));
        setOneLineMode(false);
    }
}
