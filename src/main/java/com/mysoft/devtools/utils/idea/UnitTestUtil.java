package com.mysoft.devtools.utils.idea;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.DocumentUtil;
import com.mysoft.devtools.utils.idea.psi.ProjectExtension;
import com.mysoft.devtools.utils.idea.psi.PsiClassExtension;
import com.mysoft.devtools.utils.idea.psi.PsiDirectoryExtension;
import com.mysoft.devtools.utils.idea.psi.PsiModuleExtension;
import lombok.experimental.ExtensionMethod;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.stream.Collectors;

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
            //业务类import
            StringBuilder importStr = new StringBuilder();
            importStr.append(psiClass.getPackageName()).append(".").append(psiClass.getName()).append(";");

            //业务类文件中声明的import
            PsiImportList psiImportList = PsiTreeUtil.findChildOfType(psiClass.getContainingFile(), PsiImportList.class);
            if (psiImportList != null) {
                for (PsiImportStatementBase imp : psiImportList.getAllImportStatements()) {
                    importStr.append(imp.getText()).append(System.lineSeparator());
                }
            }

            //业务类文件中的字段
            Collection<PsiField> psiFields = PsiTreeUtil.findChildrenOfAnyType(psiClass.getContainingFile(), PsiField.class);
            String fieldsStr = psiFields.stream().map(PsiElement::getText).collect(Collectors.joining(System.lineSeparator()));

            //构建代码模板必须的参数
            String content = CodeTemplateUtil.getText(project, "JUnitTest5Class", x -> {
                x.put("ORIGINAL_NAME", psiClass.getName());
                x.put("NAME", testSimpleName);
                x.put("PACKAGE_NAME", testPackageName);
                x.put("IMPORTS", importStr.toString());
                x.put("CLASS_FIELDS", fieldsStr);
            });

            //当前Module
            Module module = ModuleUtil.findModuleForFile(psiClass.getContainingFile());

            //获取或创建单测文件目录
            PsiDirectory baseDir = PsiModuleExtension.findOrCreateTestDir(module);

            //按单测类命名空间查找目录，如不存在则创建
            PsiDirectory directory = PsiDirectoryExtension.createSubdirectorys(baseDir, testPackageName.replaceAll("\\.", "/"));

            String fileName = testSimpleName + ".java";

            //按代码模板创建单测类
            PsiFile file = directory.findFile(fileName);
            if (file != null) {
                return PsiTreeUtil.findChildOfType(file, PsiClass.class);
            }

            file = directory.createFile(fileName);

            //将AI生成的单测代码保存到单测类中
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
        PsiFile psiFile = testClass.getContainingFile();
        Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);

        DocumentUtil.writeInRunUndoTransparentAction(() -> {
            PsiMethod[] methods = testClass.getMethods();
            if (methods.length == 0) {
                int startOffsetInParent = testClass.getRBrace().getTextOffset() - 1;
                document.insertString(startOffsetInParent, "\n" + code);
            } else {
                PsiMethod lastMethod = methods[methods.length - 1];
                TextRange lastMethodRange = lastMethod.getTextRange();
                int offset = lastMethodRange.getEndOffset();
                document.insertString(offset, "\n" + code);
            }

            PsiDocumentManager.getInstance(project).commitDocument(document);
            CodeStyleManager.getInstance(project).reformat(psiFile);
        });
    }

    /**
     * 优化import
     *
     * @param psiClass
     */
    public static void optimizeImports(PsiClass psiClass) {

    }
}

