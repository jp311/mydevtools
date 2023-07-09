package com.mysoft.devtools.utils.idea.psi;

import com.intellij.ide.util.PackageUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.PackageIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Query;
import org.jetbrains.annotations.Nullable;

/**
 * @author hezd   2023/7/8
 */
public class PackageExtension {
    public static @Nullable PsiPackage findPackage(Project project, String packageName) {
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        return javaPsiFacade.findPackage(packageName);
    }

    public static @Nullable String findPackageByPath(VirtualFile virtualFile) {
        StringBuilder packageName = new StringBuilder();
        VirtualFile directory = virtualFile.isDirectory() ? virtualFile : virtualFile.getParent();
        while (directory != null && !"src".equals(directory.getName())) {
            packageName.insert(0, "." + directory.getName());
            directory = directory.getParent();
        }
        packageName = new StringBuilder(packageName.substring(1));
        return packageName.toString().replaceAll("main\\.java\\.", "");
    }

    public static Query<VirtualFile> getDirsByPackageName(Project project, String packageName) {
        return PackageIndex.getInstance(project).getDirsByPackageName(packageName, GlobalSearchScope.allScope(project));
    }

    public static PsiDirectory findOrCreateDirectoryForPackage(Module module, String packageName, PsiDirectory baseDir, boolean filterSourceDirsForBaseTestDirectory) {
        return PackageUtil.findOrCreateDirectoryForPackage(module, packageName, baseDir, false, filterSourceDirsForBaseTestDirectory);
    }
}
