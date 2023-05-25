package com.mysoft.devtools.Inspections;

import com.intellij.codeInspection.*;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.mysoft.devtools.bundles.InspectionBundle;
import com.mysoft.devtools.dtos.QualifiedNames;
import com.mysoft.devtools.utils.StringExtension;
import com.mysoft.devtools.utils.psi.ProjectExtension;
import com.mysoft.devtools.utils.psi.PsiClassExtension;
import com.mysoft.devtools.utils.psi.PsiCommonUtil;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 变量名称检查
 * 1、针对@Resource注入的变量名称推荐和类型命名一致，防止出现错误
 * 2、todo 依赖注入的类型是否有@Service或Component注解 （需要检查spring ioc容器所有的bean才准确，但是bean注入规则多又复杂一时怕搞不全，另外性能也差，代码检查中不应该有耗性能的操作）
 * 3、类名必须全局唯一，或配置了自定义名称
 *
 * @author hezd 2023/4/27
 */
@ExtensionMethod({PsiClassExtension.class, ProjectExtension.class, StringExtension.class})
public class ResourceInspection extends AbstractBaseJavaLocalInspectionTool {
    private final RemoveResourceQuickFix removeResourceQuickFix = new RemoveResourceQuickFix();

    private final RenameAllFieldsQuickFix renameAllFieldsQuickFix = new RenameAllFieldsQuickFix();

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitField(PsiField field) {
                if (!field.hasAnnotation(QualifiedNames.AUTOWIRED_QUALIFIED_NAME) && !field.hasAnnotation(QualifiedNames.RESOURCE_QUALIFIED_NAME)) {
                    super.visitField(field);
                    return;
                }

                PsiType fieldType = field.getType();
                PsiClass fieldTypeClass = PsiUtil.resolveClassInType(fieldType);
                if (fieldTypeClass == null) {
                    holder.registerProblem(field.getNameIdentifier(), InspectionBundle.message("inspection.platform.service.resource.problem.descriptor"), ProblemHighlightType.ERROR, removeResourceQuickFix);
                }

                //字段命名规范
                if (fieldTypeClass != null && !Objects.equals(getTypeNameFirstLowerCase(fieldType), field.getName())) {
                    holder.registerProblem(field.getNameIdentifier(), InspectionBundle.message("inspection.platform.service.resource.name.problem.descriptor"), ProblemHighlightType.ERROR, new RenameFieldQuickFix(field), renameAllFieldsQuickFix);
                }

//                Project project = fieldTypeClass.getProject();
//                if (fieldTypeClass.isInterface()) {
//                    boolean isBaseDao = fieldTypeClass.isInheritors(QualifiedNames.BASE_DAO_QUALIFIED_NAME, project);
//                    if (isBaseDao || fieldTypeClass.getAnnotation(QualifiedNames.REMOTE_SERVICE_QUALIFIED_NAME) != null) {
//                        super.visitField(field);
//                        return;
//                    } else {
//                        List<PsiClass> psiClasses = project.deepSearchInheritorClass(fieldTypeClass.getQualifiedName());
//                        boolean isService = psiClasses.stream().anyMatch(x -> isServiceOrComponent(x));
//                        if (isService) {
//                            holder.registerProblem(field, getProblemName(), ProblemHighlightType.ERROR, removeResourceQuickFix, addServiceAnnotationQuickFix);
//                            return;
//                        }
//                    }
//
//                    super.visitField(field);
//                    return;
//                }


//                if (isServiceOrComponent(fieldTypeClass)) {
//                    holder.registerProblem(field, getProblemName(), ProblemHighlightType.ERROR, removeResourceQuickFix, addServiceAnnotationQuickFix);
//                }
            }
        };
    }

    private static String getTypeNameFirstLowerCase(PsiType psiType) {
        PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(psiType);
        if (psiClass == null || psiClass.getName() == null) {
            return "";
        }
        return psiClass.getName().firstLowerCase();
    }

    private static class RemoveResourceQuickFix implements LocalQuickFix {
        @Override
        public @IntentionFamilyName
        @NotNull String getFamilyName() {
            return InspectionBundle.message("inspection.platform.service.resource.use.quickfix_remove");
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiElement psiElement = descriptor.getPsiElement();
            if (!(psiElement.getParent() instanceof PsiField)) {
                return;
            }
            PsiField psiField = (PsiField) psiElement.getParent();
            PsiAnnotation resourceAnnotation = psiField.getAnnotation(QualifiedNames.RESOURCE_QUALIFIED_NAME);
            PsiAnnotation autowiredAnnotation = psiField.getAnnotation(QualifiedNames.AUTOWIRED_QUALIFIED_NAME);
            if (resourceAnnotation != null) {
                resourceAnnotation.delete();
            }
            if (autowiredAnnotation != null) {
                autowiredAnnotation.delete();
            }
        }
    }

    private static class RenameAllFieldsQuickFix implements LocalQuickFix {
        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return InspectionBundle.message("inspection.platform.service.resource.allfields.use.quickfix_rename");
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiElement psiElement = descriptor.getPsiElement();
            if (!(psiElement.getParent() instanceof PsiField)) {
                return;
            }

            PsiClass psiClass = PsiTreeUtil.getParentOfType(psiElement, PsiClass.class);
            if (psiClass == null || psiClass.getNameIdentifier() == null || psiClass instanceof PsiTypeParameter) {
                return;
            }

            PsiField[] fields = psiClass.getFields();

            for (PsiField psiField : fields) {
                if (!psiField.hasAnnotation(QualifiedNames.AUTOWIRED_QUALIFIED_NAME) && !psiField.hasAnnotation(QualifiedNames.RESOURCE_QUALIFIED_NAME)) {
                    continue;
                }
                PsiType fieldType = psiField.getType();
                String newName = getTypeNameFirstLowerCase(fieldType);

                if (Objects.equals(psiField.getName(), newName)) {
                    continue;
                }
                PsiCommonUtil.rename(psiField, newName, true, false);
            }
        }
    }

    private static class RenameFieldQuickFix implements LocalQuickFix {

        private final String newName;

        public RenameFieldQuickFix(PsiField psiField) {
            PsiType fieldType = psiField.getType();
            newName = getTypeNameFirstLowerCase(fieldType);
        }

        @Override
        public @IntentionFamilyName
        @NotNull String getFamilyName() {
            return InspectionBundle.message("inspection.platform.service.resource.use.quickfix_rename", newName);
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiElement psiElement = descriptor.getPsiElement();
            if (!(psiElement.getParent() instanceof PsiField)) {
                return;
            }
            PsiField psiField = (PsiField) psiElement.getParent();
            if (Objects.equals(newName, "")) {
                return;
            }

            PsiCommonUtil.rename(psiField, newName, true, false);
        }
    }
}
