package com.mysoft.devtools.actions.intentions;

import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.util.IncorrectOperationException;
import com.mysoft.devtools.dtos.QualifiedNames;
import com.mysoft.devtools.utils.FileUtil;
import com.mysoft.devtools.utils.psi.ProjectExtension;
import com.mysoft.devtools.utils.psi.PsiClassExtension;
import com.mysoft.devtools.utils.psi.VirtualFileExtension;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * 根据dao生成mapper
 *
 * @author hezd   2023/7/5
 */
@ExtensionMethod({PsiClassExtension.class, VirtualFileExtension.class, ProjectExtension.class})
public class NewMapperIntention extends BaseIntention {

    private static PsiClass mapperPsiClass;

    @Override
    public @IntentionName @NotNull String getText() {
        return "一键生成Mapper";
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "一键生成Mapper";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        if (!(file instanceof PsiJavaFile)) {
            return false;
        }

        getMapperPsiClass(project);

        if (mapperPsiClass == null) {
            return false;
        }

        PsiClass psiClass = getPsiClass(project, editor, file);

        if (!psiClass.isInheritors(mapperPsiClass)) {
            return false;
        }

        VirtualFile resources = project.getResourcePath(file);
        String fileName = FileUtil.combine(resources.getPath(), "mapper", psiClass.getName() + "Mapper.xml");

        return !new File(fileName).exists();
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiClass psiClass = getPsiClass(project, editor, file);

        FileTemplate template = FileTemplateManager.getInstance(project).getTemplate("mybatis-mapper");
        try {
            Properties properties = new Properties();
            properties.put("FULLNAME", psiClass.getQualifiedName());

            String content = template.getText(properties);

            VirtualFile resources = project.getResourcePath(file);
            String fileName = FileUtil.combine(resources.getPath(), "mapper", psiClass.getName() + "Mapper.xml");
            FileUtil.writeAllText(fileName, content);
            resources.refresh(false, true);

            project.openFileInEditor(fileName);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }

    private static void getMapperPsiClass(Project project) {
        if (mapperPsiClass != null) {
            return;
        }
        mapperPsiClass = project.getPsiClass(QualifiedNames.BASE_MAPPER_QUALIFIED_NAME);
    }
}
