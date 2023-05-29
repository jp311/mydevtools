package com.mysoft.devtools.Inspections;

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
 * DomainService 检查（前置条件：继承自DomainService的子类）：
 * 1、非abstract类必须增加@Service注解
 * 2、命名应以DomainService结尾
 * @author hezd 2023/4/27
 */
@ExtensionMethod({PsiClassExtension.class, PsiElementExtension.class})
public class DomainServiceInspection extends AbstractBaseJavaLocalInspectionTool {
    private final AddAnnotationQuickFix addAnnotationQuickFix = new AddAnnotationQuickFix();

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

                //非DomainService子类不检查
                if (!aClass.isInheritors(QualifiedNames.DOMAIN_SERVICE_QUALIFIED_NAME, project)) {
                    return;
                }


                PsiAnnotation serviceAnnotation = aClass.getAnnotation(QualifiedNames.SERVICE_QUALIFIED_NAME);
                PsiAnnotation componentAnnotation = aClass.getAnnotation(QualifiedNames.COMPONENT_QUALIFIED_NAME);

                if (!aClass.isAbstract()) {
                    if (serviceAnnotation == null && componentAnnotation == null) {
                        holder.registerProblem(aClass.getNameIdentifier(), InspectionBundle.message("inspection.platform.service.domainservice.problem.notfoundserviceannotation.descriptor"), ProblemHighlightType.ERROR, addAnnotationQuickFix);
                    }
                }

//                if (!aClass.getName().endsWith("DomainService")) {
//                    holder.registerProblem(aClass.getNameIdentifier(), InspectionBundle.message("inspection.platform.service.domainservice.problem.name.descriptor"), ProblemHighlightType.WARNING);
//                }
            }
        };
    }

    private final static class AddAnnotationQuickFix implements LocalQuickFix {
        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return InspectionBundle.message("inspection.platform.service.domainservice.addservice.quickfix");
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiElement psiElement = descriptor.getPsiElement();
            if (!(psiElement instanceof PsiIdentifier)) {
                return;
            }

            PsiClass psiClass = (PsiClass) psiElement.getParent();

            PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
            PsiAnnotation serviceAnnotation = elementFactory.createAnnotationFromText("@Service", null);
            psiClass.addAnnotation(serviceAnnotation);
            psiElement.addImportIfNotExist(QualifiedNames.SERVICE_QUALIFIED_NAME);
        }
    }
}
