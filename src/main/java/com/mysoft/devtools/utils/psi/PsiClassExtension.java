package com.mysoft.devtools.utils.psi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.psi.util.InheritanceUtil;
import com.intellij.psi.util.PsiUtil;
import com.mysoft.devtools.dtos.QualifiedNames;
import lombok.experimental.ExtensionMethod;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author hezd   2023/5/9
 */
@ExtensionMethod({VirtualFileExtension.class})
public class PsiClassExtension {
    public static boolean isAbstract(PsiClass psiClass){
        PsiModifierList modifierList = psiClass.getModifierList();
        if (modifierList == null){
            return false;
        }

        return modifierList.hasModifierProperty(PsiModifier.ABSTRACT);
    }
    public static String getComment(PsiClass psiClass) {
        PsiDocComment docComment = psiClass.getDocComment();
        if (docComment == null) {
            return "";
        }
        return Arrays.stream(docComment.getDescriptionElements()).map(x -> x.getText().replace(" ", "").replace("\n", "")).collect(Collectors.joining(""));
    }

    public static String getPackageName(PsiClass psiClass) {
        return PsiUtil.getPackageName(psiClass);
    }

    public static boolean isInheritors(PsiClass subClass, String baseName, Project project) {
        GlobalSearchScope scope = ProjectScope.getAllScope(project);
        PsiClass baseClass = JavaPsiFacade.getInstance(project).findClass(baseName, scope);
        if (baseClass == null) {
            return false;
        }
        return isInheritors(subClass, baseClass);
    }

    public static boolean isInheritors(PsiClass subClass, PsiClass baseClass) {
         return InheritanceUtil.isInheritorOrSelf(subClass,baseClass,true);
    }

    /**
     * 是否本项目的文件
     */
    public static boolean isInSourceContent(PsiClass psiClass,Project project){
        return ProjectRootManager.getInstance(project).getFileIndex().isInSourceContent(psiClass.getContainingFile().getVirtualFile());
    }

    public static void addAnnotation(PsiClass aClass, PsiAnnotation annotation) {
        PsiModifierList modifierList = aClass.getModifierList();
        modifierList.addAfter(annotation,null);
    }
}
