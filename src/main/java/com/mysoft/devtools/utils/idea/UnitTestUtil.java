package com.mysoft.devtools.utils.idea;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.mysoft.devtools.utils.idea.psi.ProjectExtension;
import com.mysoft.devtools.utils.idea.psi.PsiClassExtension;
import lombok.experimental.ExtensionMethod;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * @author hezd   2023/7/8
 */
@ExtensionMethod({ProjectExtension.class, PsiClassExtension.class})
public class UnitTestUtil {

    public static String getTestQualifiedName(PsiClass psiClass) {
        return MessageFormat.format("{0}.test.{1}Test", psiClass.getPackageName(), psiClass.getName());
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
        FileTemplate template = FileTemplateManager.getInstance(project).getTemplate("JUnitTest5Class");
        try {
            Properties properties = new Properties();
            properties.put("OriginalName", psiClass.getQualifiedName());
            properties.put("originalName", psiClass.getQualifiedName());

            String content = template.getText(properties);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static PsiClass createOrOpenTestPsiClass(PsiClass psiClass) {
        PsiClass testClass = findTestClass(psiClass);
        if (testClass != null) {
            return testClass;
        }
        return createTestPsiClass(psiClass);
    }

    public static void appendCode(PsiClass testClass, String code) {

    }
}
