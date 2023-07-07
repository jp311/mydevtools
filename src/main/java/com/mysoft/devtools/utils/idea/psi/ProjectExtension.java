package com.mysoft.devtools.utils.idea.psi;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.mysoft.devtools.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author hezd 2023/5/8
 */
public class ProjectExtension {
    public static VirtualFile getResourcePath(Project project, PsiFile file) {
        VirtualFile[] sourceFolderFiles = ModuleRootManager.getInstance(ModuleUtil.findModuleForFile(file)).getContentEntries()[0].getSourceFolderFiles();
        return Arrays.stream(sourceFolderFiles).filter(x -> x.getPath().endsWith("resources")).findFirst().orElse(null);
    }

    public static void openFileInEditor(Project project, String fileName) {
        File fileToOpen = new File(fileName);
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(fileToOpen);
        FileEditorManager.getInstance(project).openFile(virtualFile, true);
    }

    /**
     * 获取src目录
     *
     * @return
     */
    public static String getCIDirectory(Project project) {
        String root = project.getBasePath();
        String path = FileUtil.combine(root, "ci");
        if (FileUtil.isExist(path)) {
            return path;
        }


        path = FileUtil.combine(new File(root).getParent(), "ci");
        if (FileUtil.isExist(path)) {
            return path;
        }

        return null;
    }

    /**
     * 深度搜索子类
     *
     * @return 所有子类
     */
    public static List<PsiClass> deepSearchInheritorClass(Project project, String fullBaseName) {
        GlobalSearchScope scope = ProjectScope.getAllScope(project);
        PsiClass baseEntityClass = JavaPsiFacade.getInstance(project).findClass(fullBaseName, scope);
        if (baseEntityClass == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(ClassInheritorsSearch.search(baseEntityClass).findAll());
    }
}
