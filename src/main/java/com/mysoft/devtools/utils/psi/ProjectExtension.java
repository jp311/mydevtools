package com.mysoft.devtools.utils.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hezd 2023/5/8
 */
public class ProjectExtension {
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

        throw new RuntimeException(LocalBundle.message("devtools.exception.notfoundpath.ci"));
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
