package com.mysoft.devtools.inspections;

import com.intellij.codeInspection.*;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import com.mysoft.devtools.bundles.InspectionBundle;
import com.mysoft.devtools.dtos.QualifiedNames;
import com.mysoft.devtools.utils.InspectionWhiteUtil;
import com.mysoft.devtools.utils.StringExtension;
import com.mysoft.devtools.utils.psi.PsiAnnotationValueExtension;
import com.mysoft.devtools.utils.psi.PsiClassExtension;
import com.mysoft.devtools.utils.psi.PsiElementExtension;
import com.mysoft.devtools.views.users.InspectionWhiteDialog;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Arrays;

/**
 * Entity声明检查（前置条件：继承自BaseEntity的子类）：
 * 1 类必须存在 @TableName
 * 2、字段列表中只能存在一个@TableId（继承父类存在TableId也不行）
 * 3、实体类不应该为abstract（特殊情况可按类名或包名添加到白名单不检查）
 * 4、一键生成Dao、一键生成Mapper.xml （TODO 待实现，应该放到IDEA的灯泡建议，不应该放到检查中！）
 *
 * @author hezd 2023/4/26
 */
@ExtensionMethod({PsiClassExtension.class, PsiElementExtension.class, PsiAnnotationValueExtension.class, StringExtension.class})
public class EntityDeclarationInspection extends AbstractBaseJavaLocalInspectionTool {
    private final AddTableNameQuickFix addTableNameQuickFix = new AddTableNameQuickFix();
    private final RemoveAbstractQuickFix removeAbstractQuickFix = new RemoveAbstractQuickFix();

    private final AddWhiteQuickFix addWhiteDeclareQuickFix = new AddWhiteQuickFix(InspectionWhiteUtil.ENTITY_DECLARE);

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitClass(PsiClass aClass) {
                Project project = aClass.getProject();
                if (aClass.getName() == null) {
                    return;
                }

                if (aClass instanceof PsiTypeParameter) {
                    return;
                }
                //非BaseEntity子类不检查
                if (!aClass.isInheritors(QualifiedNames.BASE_ENTITY_QUALIFIED_NAME, project)) {
                    return;
                }

                //白名单检查
                boolean isWhite = InspectionWhiteUtil.isWhite(InspectionWhiteUtil.ENTITY_DECLARE, aClass.getQualifiedName(), aClass.getPackageName(), project);
                if (isWhite) {
                    return;
                }
                //1、字段列表中只能存在一个@TableId（继承父类存在TableId也不行）
                checkerTableId(aClass, holder);

                //2、检查是否存在@TableName注解
                checkerTableName(aClass, holder);

                //3、实体类不应该为abstract（特殊情况可添加到白名单不检查）
                checkerAbstract(aClass, holder);
            }
        };
    }

    /**
     * 字段列表中只能存在一个@TableId（继承父类存在TableId也不行）
     */
    private void checkerTableId(PsiClass aClass, ProblemsHolder holder) {
        if (aClass == null || aClass.getNameIdentifier() == null) {
            return;
        }
        if (aClass.isAbstract()) {
            return;
        }

        PsiField[] allFields = aClass.getAllFields();

        long count = Arrays.stream(allFields).filter(x -> x.hasAnnotation(QualifiedNames.TABLE_ID_QUALIFIED_NAME)).count();
        if (count == 0) {
            holder.registerProblem(aClass.getNameIdentifier(), InspectionBundle.message("inspection.platform.service.entity.problem.notableid.descriptor"), ProblemHighlightType.ERROR, addWhiteDeclareQuickFix);
        }

        if (count > 1) {
            holder.registerProblem(aClass.getNameIdentifier(), InspectionBundle.message("inspection.platform.service.entity.problem.moretableid.descriptor"), ProblemHighlightType.ERROR, addWhiteDeclareQuickFix);
        }
    }

    /**
     * 检查是否存在@TableName注解（抽象类不检查）
     */
    private void checkerTableName(PsiClass aClass, ProblemsHolder holder) {
        if (aClass == null || aClass.getNameIdentifier() == null) {
            return;
        }

        if (aClass.isAbstract()) {
            return;
        }

        PsiAnnotation annotation = aClass.getAnnotation(QualifiedNames.TABLE_NAME_QUALIFIED_NAME);
        if (annotation == null) {
            holder.registerProblem(aClass.getNameIdentifier(), InspectionBundle.message("inspection.platform.service.entity.problem.lacktablename.descriptor"), ProblemHighlightType.ERROR, addTableNameQuickFix, addWhiteDeclareQuickFix);
            return;
        }

        PsiAnnotationMemberValue valueAttr = annotation.findAttributeValue("value");
        if (valueAttr == null || valueAttr.getValue().isNullOrEmpty()) {
            holder.registerProblem(annotation, InspectionBundle.message("inspection.platform.service.entity.problem.lacktablevalue.descriptor"), ProblemHighlightType.ERROR, addWhiteDeclareQuickFix);
        }
    }

    /**
     * 实体类不应该为abstract（特殊情况可添加到白名单不检查）
     */
    private void checkerAbstract(PsiClass aClass, ProblemsHolder holder) {
        if (aClass == null || aClass.getNameIdentifier() == null) {
            return;
        }

        if (!aClass.isAbstract()) {
            return;
        }

        holder.registerProblem(aClass.getNameIdentifier(), InspectionBundle.message("inspection.platform.service.entity.problem.abstract.descriptor"), ProblemHighlightType.ERROR, removeAbstractQuickFix, addWhiteDeclareQuickFix);
    }

    private final static class AddTableNameQuickFix implements LocalQuickFix {
        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return InspectionBundle.message("inspection.platform.service.entity.addtablename.quickfix");
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiElement psiElement = descriptor.getPsiElement();
            PsiClass psiClass = (PsiClass) psiElement.getParent();
            PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
            String annString = "@TableName(\"请在此处这里填写数据库表名\")";
            PsiAnnotation pubAnnotation = elementFactory.createAnnotationFromText(annString, null);
            psiClass.addAnnotation(pubAnnotation);

            psiElement.addImportIfNotExist(QualifiedNames.TABLE_NAME_QUALIFIED_NAME);
        }
    }

    private final static class RemoveAbstractQuickFix implements LocalQuickFix {
        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return InspectionBundle.message("inspection.platform.service.entity.removeabstract.quickfix");
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiElement psiElement = descriptor.getPsiElement();
            PsiClass psiClass = (PsiClass) psiElement.getParent();
            PsiUtil.setModifierProperty(psiClass, PsiModifier.ABSTRACT, false);
        }
    }

    private final static class AddWhiteQuickFix implements LocalQuickFix {
        private String scope;

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return InspectionBundle.message("inspection.platform.service.addwhite.quickfix");
        }

        public AddWhiteQuickFix(String scope) {
            this.scope = scope;
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiElement psiElement = descriptor.getPsiElement();
            PsiClass aClass = (PsiClass) psiElement.getParent();


            InspectionWhiteDialog dialog = new InspectionWhiteDialog(scope, aClass.getQualifiedName(), aClass.getPackageName());
            SwingUtilities.invokeLater(() -> {
                if (dialog.showAndGet()) {
                    aClass.refresh();
                }
            });
        }
    }
}
