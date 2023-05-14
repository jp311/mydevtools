package com.mysoft.devtools.Inspections;

import com.intellij.codeInspection.*;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import com.mysoft.devtools.bundles.InspectionBundle;
import com.mysoft.devtools.dtos.QualifiedNames;
import com.mysoft.devtools.utils.psi.IdeaNotifyUtil;
import com.mysoft.devtools.utils.psi.ProjectExtension;
import com.mysoft.devtools.utils.psi.PsiClassExtension;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 变量名称检查
 * 1、针对@Resource注入的变量名称推荐和类型命名一致，防止出现错误
 * 2、依赖注入的类型是否有@Service或Component注解
 * 3、不能同时有Service和Component注解
 * 4、类名必须全局唯一，或配置了自定义名称
 *
 * @author hezd 2023/4/27
 */
@ExtensionMethod({PsiClassExtension.class, ProjectExtension.class})
public class ResourceInspection extends AbstractBaseJavaLocalInspectionTool {
    private final RemoveResourceQuickFix removeResourceQuickFix = new RemoveResourceQuickFix();
    private final AddServiceAnnotationQuickFix addServiceAnnotationQuickFix = new AddServiceAnnotationQuickFix();

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitField(PsiField field) {
                if (!field.hasAnnotation(QualifiedNames.AUTOWIRED_QUALIFIED_NAME) && !field.hasAnnotation(QualifiedNames.RESOURCE_QUALIFIED_NAME)) {
                    super.visitField(field);
                    return;
                }

                PsiType type = field.getType();
                PsiClass psiClass = PsiUtil.resolveClassInType(type);
                if (psiClass == null) {
                    holder.registerProblem(field, getProblemName(), ProblemHighlightType.GENERIC_ERROR_OR_WARNING, removeResourceQuickFix,addServiceAnnotationQuickFix);
                    return;
                }
                Project project = psiClass.getProject();

                if (psiClass.isInterface()) {
                    boolean isBaseDao = psiClass.isInheritors(QualifiedNames.BASE_DAO_QUALIFIED_NAME, project);
                    if (isBaseDao || psiClass.getAnnotation(QualifiedNames.REMOTE_SERVICE_QUALIFIED_NAME) != null) {
                        super.visitField(field);
                        return;
                    } else {
                        List<PsiClass> psiClasses = project.deepSearchInheritorClass(psiClass.getQualifiedName());
                        boolean isService = psiClasses.stream().anyMatch(x -> isServiceOrComponent(x));
                        if (isService) {
                            holder.registerProblem(field, getProblemName(), ProblemHighlightType.GENERIC_ERROR_OR_WARNING, removeResourceQuickFix,addServiceAnnotationQuickFix);
                            return;
                        }
                    }

                    super.visitField(field);
                    return;
                }


                if (isServiceOrComponent(psiClass)) {
                    holder.registerProblem(field, getProblemName(), ProblemHighlightType.GENERIC_ERROR_OR_WARNING, removeResourceQuickFix,addServiceAnnotationQuickFix);
                }
            }
        };
    }

    private String getProblemName() {
        return InspectionBundle.message("inspection.platform.service.resource.display.name") + "：" + System.lineSeparator() + InspectionBundle.message("inspection.platform.service.resource.problem.descriptor");
    }

    private boolean isServiceOrComponent(PsiClass psiClass) {
        PsiAnnotation serviceAnnotation = psiClass.getAnnotation(QualifiedNames.SERVICE_QUALIFIED_NAME);
        PsiAnnotation componentAnnotation = psiClass.getAnnotation(QualifiedNames.COMPONENT_QUALIFIED_NAME);

        return serviceAnnotation == null && componentAnnotation == null;
    }

    private static class RemoveResourceQuickFix implements LocalQuickFix {
        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return InspectionBundle.message("inspection.platform.service.resource.use.quickfix_remove");
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiField field = (PsiField) descriptor.getPsiElement();
            PsiAnnotation resourceAnnotation = field.getAnnotation(QualifiedNames.RESOURCE_QUALIFIED_NAME);
            PsiAnnotation autowiredAnnotation = field.getAnnotation(QualifiedNames.AUTOWIRED_QUALIFIED_NAME);
            if (resourceAnnotation != null) {
                resourceAnnotation.delete();
            }
            if (autowiredAnnotation != null) {
                autowiredAnnotation.delete();
            }
        }
    }

    private static class AddServiceAnnotationQuickFix implements LocalQuickFix {
        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return InspectionBundle.message("inspection.platform.service.resource.use.quickfix_add_annotation");
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            IdeaNotifyUtil.dialogWarn(InspectionBundle.message("inspection.platform.service.resource.use.quickfix_add_annotation_warn"));
        }
    }
}
