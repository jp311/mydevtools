package com.mysoft.devtools.utils.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.searches.AnnotationTargetsSearch;

import java.util.Collection;

/**
 * @author hezd   2023/5/16
 */
public class PsiAnnotationExtension {
    public static Collection<PsiModifierListOwner> findUsages(String qualifiedName, Project project) {
        PsiClass annotationClass = JavaPsiFacade.getInstance(project).findClass(qualifiedName, GlobalSearchScope.allScope(project));

        return AnnotationTargetsSearch.search(annotationClass, ProjectScope.getAllScope(project)).findAll();
    }
}
