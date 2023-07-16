package com.mysoft.devtools.inspections;

import com.intellij.codeInspection.*;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.mysoft.devtools.bundles.InspectionBundle;
import com.mysoft.devtools.dtos.QualifiedNames;
import com.mysoft.devtools.utils.idea.psi.PsiClassExtension;
import com.mysoft.devtools.utils.idea.psi.PsiElementExtension;
import com.mysoft.devtools.utils.idea.psi.VirtualFileExtension;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

/**
 * AppService 检查（前置条件：继承自AppService的子类）：
 * 1、非abstract类必须增加@Service注解
 * 2、命名应以AppService结尾
 *
 * @author hezd 2023/4/27
 */
@ExtensionMethod({PsiClassExtension.class, VirtualFileExtension.class, PsiElementExtension.class})
public class AppServiceInspection extends AbstractBaseJavaLocalInspectionTool {
    private final AddAnnotationQuickFix addAnnotationQuickFix = new AddAnnotationQuickFix();

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitClass(PsiClass aClass) {
                Project project = holder.getProject();
                //如果项目未打开获已关闭则忽略
                if (project.isDisposed() || !project.isOpen()) {
                    return;
                }
                //非空判断，有可能是匿名类等场景
                if (aClass == null || aClass.getNameIdentifier() == null) {
                    return;
                }

                //非AppService子类不检查
                if (!aClass.isInheritors(QualifiedNames.APP_SERVICE_QUALIFIED_NAME, project)) {
                    return;
                }

                //抽象类类不处理
                if (!aClass.isAbstract()) {
                    //查找@Service注解
                    PsiAnnotation serviceAnnotation = aClass.getAnnotation(QualifiedNames.SERVICE_QUALIFIED_NAME);
                    //查找@Component注解
                    PsiAnnotation componentAnnotation = aClass.getAnnotation(QualifiedNames.COMPONENT_QUALIFIED_NAME);
                    if (serviceAnnotation == null && componentAnnotation == null) {
                        //注册问题，向开发提示问题原因及严重级别
                        holder.registerProblem(aClass.getNameIdentifier(), InspectionBundle.message("inspection.platform.service.appservice.problem.notfoundserviceannotation.descriptor"), ProblemHighlightType.ERROR, addAnnotationQuickFix);
                    }
                }
            }
        };
    }

    /**
     * 添加注解修复类
     */
    private final static class AddAnnotationQuickFix implements LocalQuickFix {
        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return InspectionBundle.message("inspection.platform.service.appservice.addservice.quickfix");
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
