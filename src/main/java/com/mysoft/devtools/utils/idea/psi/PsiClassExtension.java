package com.mysoft.devtools.utils.idea.psi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.util.InheritanceUtil;
import com.intellij.psi.util.PsiUtil;
import lombok.experimental.ExtensionMethod;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author hezd   2023/5/9
 */
@ExtensionMethod({VirtualFileExtension.class})
public class PsiClassExtension {
    public static PsiClass createClassFromText(Project project, String qualifiedName) {
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        return javaPsiFacade.getElementFactory().createClass(qualifiedName);
    }

    public static boolean isAbstract(PsiClass psiClass) {
        PsiModifierList modifierList = psiClass.getModifierList();
        if (modifierList == null) {
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

    public static PsiClass getPsiClass(Project project, String fullName) {
        GlobalSearchScope scope = ProjectScope.getAllScope(project);
        return JavaPsiFacade.getInstance(project).findClass(fullName, scope);
    }

    public static boolean isInheritors(PsiClass subClass, String baseName, Project project) {
        PsiClass baseClass = getPsiClass(project, baseName);
        if (baseClass == null) {
            return false;
        }
        return isInheritors(subClass, baseClass);
    }

    public static boolean isInheritors(PsiClass subClass, PsiClass baseClass) {
        return InheritanceUtil.isInheritorOrSelf(subClass, baseClass, true);
    }

    public static boolean isImpl(PsiClass psiClass, String interfaceName) {
        return Arrays.stream(psiClass.getInterfaces()).anyMatch(x -> Objects.equals(x.getQualifiedName(), interfaceName));
    }

    public static void addImplInterface(PsiClass psiClass, String interfaceName, Project project) {
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
        PsiClass enumFieldInterface = elementFactory.createInterface(interfaceName);
        PsiJavaCodeReferenceElement classReferenceElement = elementFactory.createClassReferenceElement(enumFieldInterface);
        PsiReferenceList implementsList = psiClass.getImplementsList();
        if (implementsList != null) {
            implementsList.add(classReferenceElement);
        }
    }

    /**
     * 是否本项目的文件
     */
    public static boolean isInSourceContent(PsiClass psiClass, Project project) {
        return ProjectRootManager.getInstance(project).getFileIndex().isInSourceContent(psiClass.getContainingFile().getVirtualFile());
    }

    public static void addAnnotation(PsiClass aClass, PsiAnnotation annotation) {
        PsiModifierList modifierList = aClass.getModifierList();
        if (modifierList == null) {
            return;
        }
        modifierList.addAfter(annotation, null);
    }

    public static void refresh(PsiClass aClass) {
        PsiFile containingFile = aClass.getContainingFile();
        VirtualFile virtualFile = containingFile.getVirtualFile();
        virtualFile.refresh(true, false);
    }
}
