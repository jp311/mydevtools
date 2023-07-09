package com.mysoft.devtools.utils.idea;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.FocusManager;
import java.awt.*;

/**
 * @author hezd   2023/5/4
 */
public class IdeaContext {
    public static Project getActiveProject() {
        Component focusOwner = FocusManager.getCurrentManager().getFocusOwner();

        DataContext dataContext = DataManager.getInstance().getDataContext(focusOwner);

        return CommonDataKeys.PROJECT.getData(dataContext);
    }

    public static VirtualFile[] getSelectedFiles() {
        FileEditorManager editorManager = FileEditorManager.getInstance(getActiveProject());
        return editorManager.getSelectedFiles();
    }
}
