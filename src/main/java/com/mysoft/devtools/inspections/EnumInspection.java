package com.mysoft.devtools.inspections;

import com.intellij.codeInspection.*;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.mysoft.devtools.bundles.InspectionBundle;
import com.mysoft.devtools.dtos.QualifiedNames;
import com.mysoft.devtools.utils.psi.PsiClassExtension;
import com.mysoft.devtools.utils.psi.PsiElementExtension;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

/**
 * 枚举检查
 * 1、非enum不能打EnumOption注解
 * 2、必须实现EnumFieldInterface接口
 *
 * @author hezd 2023/5/9
 */
@ExtensionMethod({PsiClassExtension.class, PsiElementExtension.class})
public class EnumInspection extends AbstractBaseJavaLocalInspectionTool {
    private final RemoveEnumOptionQuickFix removeEnumOptionQuickFix = new RemoveEnumOptionQuickFix();

    private final AddEnumFieldInterfaceQuickFix addEnumFieldInterfaceQuickFix = new AddEnumFieldInterfaceQuickFix();

    private final AddEnumOptionQuickFix addEnumOptionQuickFix = new AddEnumOptionQuickFix();

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitClass(PsiClass aClass) {
                if (aClass.getNameIdentifier() == null) {
                    return;
                }


                if (aClass.hasAnnotation(QualifiedNames.ENUM_OPTION_QUALIFIED_NAME)) {
                    if (!aClass.isEnum()) {
                        if (!aClass.isImpl(QualifiedNames.ENUM_FIELD_INTERFACE_QUALIFIED_NAME)) {
                            holder.registerProblem(aClass.getNameIdentifier(), InspectionBundle.message("inspection.platform.enum.class.problem.descriptor"), ProblemHighlightType.ERROR, removeEnumOptionQuickFix);
                        } else {
                            holder.registerProblem(aClass.getNameIdentifier(), InspectionBundle.message("inspection.platform.enum.enum.problem.descriptor"), ProblemHighlightType.ERROR);
                        }
                        return;
                    }
                }

                if (!aClass.isEnum() && aClass.isImpl(QualifiedNames.ENUM_OPTION_QUALIFIED_NAME)) {
                    holder.registerProblem(aClass.getNameIdentifier(), InspectionBundle.message("inspection.platform.enum.interface.problem.descriptor"), ProblemHighlightType.ERROR, removeEnumOptionQuickFix);
                    return;
                }

                if (aClass.isEnum() && aClass.isImpl(QualifiedNames.ENUM_FIELD_INTERFACE_QUALIFIED_NAME) && !aClass.hasAnnotation(QualifiedNames.ENUM_OPTION_QUALIFIED_NAME)) {
                    holder.registerProblem(aClass.getNameIdentifier(), InspectionBundle.message("inspection.platform.enum.noioption.problem.descriptor"), ProblemHighlightType.ERROR, addEnumOptionQuickFix);
                    return;
                }

                if (aClass.isEnum() && aClass.hasAnnotation(QualifiedNames.ENUM_OPTION_QUALIFIED_NAME) && !aClass.isImpl(QualifiedNames.ENUM_FIELD_INTERFACE_QUALIFIED_NAME)) {
                    holder.registerProblem(aClass.getNameIdentifier(), InspectionBundle.message("inspection.platform.enum.nointerface.problem.descriptor"), ProblemHighlightType.ERROR, addEnumFieldInterfaceQuickFix);
                    return;
                }
            }
        };
    }

    private static final class RemoveEnumOptionQuickFix implements LocalQuickFix {
        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return InspectionBundle.message("inspection.platform.enum.removeoption.quickfix");
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiElement psiElement = descriptor.getPsiElement();
            if (!(psiElement.getParent() instanceof PsiClass)) {
                return;
            }

            PsiClass aClass = (PsiClass) psiElement.getParent();
            PsiAnnotation annotation = aClass.getAnnotation(QualifiedNames.ENUM_OPTION_QUALIFIED_NAME);
            if (annotation == null) {
                return;
            }
            annotation.delete();
        }
    }

    private static final class AddEnumFieldInterfaceQuickFix implements LocalQuickFix {
        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return InspectionBundle.message("inspection.platform.enum.addinterface.quickfix");
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiElement psiElement = descriptor.getPsiElement();
            if (!(psiElement.getParent() instanceof PsiClass)) {
                return;
            }

            PsiClass aClass = (PsiClass) psiElement.getParent();
            aClass.addImplInterface("EnumFieldInterface", project);
            psiElement.addImportIfNotExist(QualifiedNames.ENUM_FIELD_INTERFACE_QUALIFIED_NAME);
        }
    }

    private static final class AddEnumOptionQuickFix implements LocalQuickFix {
        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return InspectionBundle.message("inspection.platform.enum.addoption.quickfix");
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiElement psiElement = descriptor.getPsiElement();
            if (!(psiElement.getParent() instanceof PsiClass)) {
                return;
            }

            PsiClass aClass = (PsiClass) psiElement.getParent();
            PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
            PsiAnnotation serviceAnnotation = elementFactory.createAnnotationFromText("@EnumOption(application = \"请输入系统编号\", description = \"\")", null);
            aClass.addAnnotation(serviceAnnotation);
            psiElement.addImportIfNotExist(QualifiedNames.ENUM_OPTION_QUALIFIED_NAME);
        }
    }
}
