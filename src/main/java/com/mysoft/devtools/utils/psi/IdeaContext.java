package com.mysoft.devtools.utils.psi;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author hezd   2023/5/4
 */
public class IdeaContext {
    public static Project getProject() {
        return ProjectManager.getInstance().getDefaultProject();
    }

    public static VirtualFile[] getSelectedFiles() {
        FileEditorManager editorManager = FileEditorManager.getInstance(getProject());
        return editorManager.getSelectedFiles();
    }
}
