package com.mysoft.devtools.utils.idea.psi;

import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * @author hezd   2023/7/7
 */
public class PsiEditorExtension {
    public static FileEditor[] openInEditor(Project project, VirtualFile virtualFile) {
        return FileEditorManager.getInstance(project).openFile(virtualFile, true);
    }

    public static boolean isOpenInEditor(Project project, PsiFile file) {
        FileEditorManager editorManager = FileEditorManager.getInstance(project);
        FileEditor[] editors = editorManager.getAllEditors();

        for (FileEditor editor : editors) {
            if (editor.getFile().equals(file.getVirtualFile())) {
                return true;
            }
        }

        return false;
    }

    public static PsiClass getActivePsiClass(Editor editor) {
        PsiFile psiFile = getPsiFile(editor);
        if (psiFile instanceof PsiJavaFile) {
            CaretModel caretModel = editor.getCaretModel();
            int offset = caretModel.getOffset();
            PsiElement elementAt = psiFile.findElementAt(offset);
            return PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
        }
        return null;
    }

    public static PsiMethod getActivePsiMethod(Editor editor) {
        PsiFile psiFile = getPsiFile(editor);
        if (psiFile instanceof PsiJavaFile) {
            CaretModel caretModel = editor.getCaretModel();
            int offset = caretModel.getOffset();
            PsiElement elementAt = psiFile.findElementAt(offset);
            return PsiTreeUtil.getParentOfType(elementAt, PsiMethod.class);
        }
        return null;
    }

    public static PsiFile getPsiFile(Editor editor) {
        Document document = editor.getDocument();
        return PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(document);
    }
}
