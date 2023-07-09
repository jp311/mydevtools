package com.mysoft.devtools.utils.idea;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.mysoft.devtools.utils.StringExtension;
import com.mysoft.devtools.utils.idea.psi.*;
import lombok.experimental.ExtensionMethod;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author hezd   2023/7/8
 */
@ExtensionMethod({ProjectExtension.class, PsiClassExtension.class})
public class UnitTestUtil {

    public static String getTestSimpleName(PsiClass psiClass) {
        return psiClass.getName() + "Test";
    }

    public static String getTestQualifiedName(PsiClass psiClass) {
        return MessageFormat.format("{0}.{1}", getTestPackage(psiClass), getTestSimpleName(psiClass));
    }

    public static String getTestPackage(PsiClass psiClass) {
        return MessageFormat.format("{0}.test", psiClass.getPackageName());
    }

    public static PsiClass findTestClass(PsiClass psiClass) {
        Project project = psiClass.getProject();
        String qualifiedName = getTestQualifiedName(psiClass);
        return PsiClassExtension.getPsiClass(project, qualifiedName);
    }

    public static PsiClass createTestPsiClass(PsiClass psiClass) {
        PsiClass testClass = findTestClass(psiClass);
        if (testClass != null) {
            return testClass;
        }

        Project project = psiClass.getProject();

        String testSimpleName = getTestSimpleName(psiClass);
        String testPackageName = getTestPackage(psiClass);
        try {
            String importStr = psiClass.getPackageName() + "." + psiClass.getName();
            String content = CodeTemplateUtil.getText(project, "JUnitTest5Class", x -> {
                x.put("OriginalName", psiClass.getName());
                x.put("originalName", StringExtension.firstLowerCase(psiClass.getName()));
                x.put("NAME", testSimpleName);
                x.put("PACKAGE_NAME", testPackageName);
                x.put("IMPORTS", importStr);
            });

            Module module = ModuleUtil.findModuleForFile(psiClass.getContainingFile());

            PsiDirectory baseDir = PsiModuleExtension.findOrCreateTestDir(module);
            PsiDirectory directory = PackageExtension.findOrCreateDirectoryForPackage(module, testPackageName, baseDir, true);
            if (directory == null) {
                directory = PsiDirectoryExtension.createSubdirectorys(baseDir, testPackageName.replace("\\.", "\\/"));
            }
            PsiFile file = directory.createFile(testSimpleName + ".java");

            VfsUtil.saveText(file.getVirtualFile(), content);

            return PsiTreeUtil.findChildOfType(file, PsiClass.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static PsiClass createOrOpenTestPsiClass(PsiClass psiClass) {
        PsiClass testClass = findTestClass(psiClass);
        if (testClass != null) {
            return testClass;
        }
        return createTestPsiClass(psiClass);
    }

    public static void appendCode(PsiClass testClass, String code) {
        Project project = testClass.getProject();
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
        PsiMethod element = elementFactory.createMethodFromText(code, null);
        ApplicationManager.getApplication().invokeLater(() -> {
            ApplicationManager.getApplication().runWriteAction(() -> {
                testClass.addAfter(element, null);
            });
        });
    }
}
