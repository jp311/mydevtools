package com.mysoft.devtools.Inspections;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInspection.*;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.mysoft.devtools.bundles.InspectionBundle;
import com.mysoft.devtools.dtos.QualifiedNames;
import com.mysoft.devtools.utils.CollectExtension;
import com.mysoft.devtools.utils.StringExtension;
import com.mysoft.devtools.utils.psi.*;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Controller检查（前置条件：继承自Controller的子类）：
 * 1、是否存在Tag注解 @Tag(name = "供应商应用服务")
 * 2、是否存在PubService注解 @PubService(value = "/Budgets", prefix = RequestPrefix.API, businessCode = "02200301")
 * 3、public方法是否存在@PubAction注解，@PubAction(value = "/getBudgetsByProject", method = RequestMethod.POST)
 * 4、参数不能带有buguid、oid关键字
 * 5、businessCode + value全局唯一检查
 * 6、PubAction.value当前controller唯一检查
 * 7、RequestBody、复杂参数检查（不能同时存在2个DTO） TODO 待实现
 * 8、命名需要以Controller结尾
 * <a href="https://dploeger.github.io/intellij-api-doc/com/intellij/codeInspection/AbstractBaseJavaLocalInspectionTool.html">...</a>
 *
 * @author hezd 2023/4/27
 */
@ExtensionMethod({PsiClassExtension.class, CollectExtension.class, StringExtension.class, PsiMethodExtension.class, PsiElementExtension.class, PsiAnnotationValueExtension.class})
public class ControllerInspection extends AbstractBaseJavaLocalInspectionTool {
    private final AddTagAnnotationQuickFix addTagAnnotationQuickFix = new AddTagAnnotationQuickFix();
    private final AddPubServiceAnnotationQuickFix addPubServiceAnnotationQuickFix = new AddPubServiceAnnotationQuickFix();
    private final AddPubActionAnnotationQuickFix addPubActionAnnotationQuickFix = new AddPubActionAnnotationQuickFix();

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {


            @Override
            public void visitClass(PsiClass aClass) {
                if (!isController(aClass, aClass.getProject())) {
                    return;
                }

                //命名规范检查：以Controller结尾
                if (aClass.getName() != null && !aClass.getName().endsWith("Controller")) {
                    holder.registerProblem(aClass.getNameIdentifier(), InspectionBundle.message("inspection.platform.service.controller.problem.name.descriptor"), ProblemHighlightType.WARNING);
                }

                if (aClass.isAbstract()) {
                    return;
                }

                //检查是否有Tag注解
                checkerTagAnnotation(aClass, holder);

                //检查是否存在PubService注解
                checkerPubServiceAnnotation(aClass, holder);

                //检查value + businessCode是否全局唯一
                checkerPubServiceIsUnique(aClass, holder);
            }

            @Override
            public void visitMethod(PsiMethod method) {
                PsiClass aClass = PsiTreeUtil.getParentOfType(method, PsiClass.class);
                if (aClass == null) {
                    return;
                }

                if (!isController(aClass, aClass.getProject())) {
                    return;
                }

                if (!method.isPublic() || method.isStatic()) {
                    return;
                }

                //参数命名关键字冲突检查
                checkerKeyword(method, holder);

                //检查返回值不能是Entity,防止前端大小写的问题
                checkerReturnValue(method, holder);

                if (!method.isAbstract()) {
                    //检查是否缺失PubAction注解
                    checkerPubAction(method, holder);

                    //检查value是否在当前类中唯一
                    checkerPubActionIsUnique(method, holder);
                }
            }
        };
    }

    private void checkerReturnValue(PsiMethod method, ProblemsHolder holder) {
        if (method.getReturnTypeElement() == null) {
            return;
        }

        PsiClass psiClass = PsiUtil.resolveClassInType(method.getReturnType());
        if (psiClass == null) {
            return;
        }
        Project project = psiClass.getProject();

        //普通实体类型
        if (psiClass.isInheritors(QualifiedNames.BASE_ENTITY_QUALIFIED_NAME, project)) {
            holder.registerProblem(method.getReturnTypeElement(), InspectionBundle.message("inspection.platform.service.controller.problem.pubservice.pubaction.return.descriptor"), ProblemHighlightType.ERROR);
        }

        //泛型实体类型
        if (method.getReturnType() instanceof PsiClassType) {
            PsiType[] parameters = ((PsiClassType) method.getReturnType()).getParameters();
            for (PsiType parameterType : parameters) {

                psiClass = PsiUtil.resolveClassInType(parameterType);
                if (psiClass.isInheritors(QualifiedNames.BASE_ENTITY_QUALIFIED_NAME, project)) {
                    holder.registerProblem(method.getReturnTypeElement(), InspectionBundle.message("inspection.platform.service.controller.problem.pubservice.pubaction.return.descriptor"), ProblemHighlightType.ERROR);
                }
            }
        }

    }

    /**
     * 检查value是否在当前类中唯一
     */
    private void checkerPubActionIsUnique(PsiMethod method, ProblemsHolder holder) {
        PsiClass aClass = method.getContainingClass();
        if (aClass == null) {
            return;
        }

        PsiAnnotation annotation = method.getAnnotation(QualifiedNames.PUB_ACTION_QUALIFIED_NAME);
        if (annotation == null) {
            return;
        }
        PsiAnnotationMemberValue valueAttr = annotation.findAttributeValue("value");
        if (valueAttr == null || valueAttr.getValue().isNullOrEmpty()) {
            return;
        }

        PsiMethod[] methods = aClass.getMethods();
        List<PsiMethod> repeatUsages = Arrays.stream(methods).filter(x -> !Objects.equals(x, method) && comprePubActionAnnotation(valueAttr.getValue(), x)).collect(Collectors.toList());
        if (repeatUsages.size() > 0) {
            holder.registerProblem(valueAttr, InspectionBundle.message("inspection.platform.service.controller.problem.pubservice.pubaction.unique.descriptor"), ProblemHighlightType.ERROR);
        }

    }

    private boolean comprePubActionAnnotation(String value, PsiMethod psiMethod) {
        PsiAnnotation annotation = psiMethod.getAnnotation(QualifiedNames.PUB_ACTION_QUALIFIED_NAME);
        if (annotation == null) {
            return false;
        }
        PsiAnnotationMemberValue valueAttr = annotation.findAttributeValue("value");
        if (valueAttr == null || valueAttr.getValue().isNullOrEmpty()) {
            return false;
        }
        return Objects.equals(value, valueAttr.getValue());
    }

    /**
     * 检查value + businessCode是否全局唯一
     */
    private void checkerPubServiceIsUnique(PsiClass aClass, ProblemsHolder holder) {
        PsiAnnotation annotation = aClass.getAnnotation(QualifiedNames.PUB_SERVICE_QUALIFIED_NAME);
        if (annotation == null) {
            return;
        }
        PsiAnnotationMemberValue valueAttr = annotation.findAttributeValue("value");
        if (valueAttr == null || valueAttr.getValue().isNullOrEmpty()) {
            return;
        }

        PsiAnnotationMemberValue businessCodeAttr = annotation.findAttributeValue("businessCode");
        if (businessCodeAttr == null || businessCodeAttr.getValue().isNullOrEmpty()) {
            return;
        }
        Project project = aClass.getProject();
        List<PsiClass> usages = PsiAnnotationExtension.findUsages(QualifiedNames.PUB_SERVICE_QUALIFIED_NAME, project).ofType(PsiClass.class);
        List<PsiClass> repeatUsages = usages.stream().filter(x -> !Objects.equals(x, aClass) && comprePubServiceAnnotation(valueAttr.getValue(), businessCodeAttr.getValue(), x)).collect(Collectors.toList());
        if (repeatUsages.size() > 0) {
            holder.registerProblem(annotation, InspectionBundle.message("inspection.platform.service.controller.problem.pubservice.unique.descriptor"), ProblemHighlightType.ERROR);
        }
    }

    private boolean comprePubServiceAnnotation(String value, String businessCode, PsiClass psiClass) {
        PsiAnnotation annotation = psiClass.getAnnotation(QualifiedNames.PUB_SERVICE_QUALIFIED_NAME);
        if (annotation == null) {
            return false;
        }
        PsiAnnotationMemberValue valueAttr = annotation.findAttributeValue("value");
        if (valueAttr == null || valueAttr.getValue().isNullOrEmpty()) {
            return false;
        }

        PsiAnnotationMemberValue businessCodeAttr = annotation.findAttributeValue("businessCode");
        if (businessCodeAttr == null || businessCodeAttr.getValue().isNullOrEmpty()) {
            return false;
        }

        return Objects.equals(valueAttr.getValue(), value) && Objects.equals(businessCodeAttr.getValue(), businessCode);
    }


    private boolean isController(PsiClass psiClass, Project project) {
        return psiClass.isInheritors(QualifiedNames.CONTROLLER_QUALIFIED_NAME, project);
    }

    /**
     * 检查是否有Tag注解
     */
    private void checkerTagAnnotation(PsiClass psiClass, ProblemsHolder holder) {
        if (psiClass.getNameIdentifier() == null) {
            return;
        }
        PsiAnnotation tagAnnotation = psiClass.getAnnotation(QualifiedNames.TAG_QUALIFIED_NAME);
        if (tagAnnotation == null) {
            holder.registerProblem(psiClass.getNameIdentifier(), InspectionBundle.message("inspection.platform.service.controller.problem.tagannotation.descriptor"), ProblemHighlightType.WARNING, addTagAnnotationQuickFix);
            return;
        }

        PsiAnnotationMemberValue nameAttr = tagAnnotation.findAttributeValue("name");
        if (nameAttr == null) {
            holder.registerProblem(tagAnnotation, InspectionBundle.message("inspection.platform.service.controller.problem.tagnameattr.descriptor"), ProblemHighlightType.WARNING);
            return;
        }

        if (nameAttr.getValue().isNullOrEmpty()) {
            holder.registerProblem(tagAnnotation, InspectionBundle.message("inspection.platform.service.controller.problem.tagnameempty.descriptor"), ProblemHighlightType.WARNING);
        }
    }

    /**
     * 检查是否存在PubService注解
     */
    private void checkerPubServiceAnnotation(PsiClass psiClass, ProblemsHolder holder) {
        if (psiClass.getNameIdentifier() == null) {
            return;
        }
        PsiAnnotation pubServiceAnnotation = psiClass.getAnnotation(QualifiedNames.PUB_SERVICE_QUALIFIED_NAME);
        if (pubServiceAnnotation == null) {
            holder.registerProblem(psiClass.getNameIdentifier(), InspectionBundle.message("inspection.platform.service.controller.problem.pubservice.annotation.descriptor"), ProblemHighlightType.ERROR, addPubServiceAnnotationQuickFix);
            return;
        }

        PsiAnnotationMemberValue valueAttr = pubServiceAnnotation.findAttributeValue("value");
        if (valueAttr == null) {
            holder.registerProblem(pubServiceAnnotation, InspectionBundle.message("inspection.platform.service.controller.problem.pubservice.valueattr.descriptor"), ProblemHighlightType.ERROR);
            return;
        }

        if (valueAttr.getValue().isNullOrEmpty()) {
            holder.registerProblem(pubServiceAnnotation, InspectionBundle.message("inspection.platform.service.controller.problem.pubservice.valueempty.descriptor"), ProblemHighlightType.ERROR);
            return;
        }

        PsiAnnotationMemberValue prefixAttr = pubServiceAnnotation.findAttributeValue("prefix");
        if (prefixAttr == null) {
            holder.registerProblem(pubServiceAnnotation, InspectionBundle.message("inspection.platform.service.controller.problem.pubservice.prefixempty.descriptor"), ProblemHighlightType.ERROR);
            return;
        }

        PsiAnnotationMemberValue businessCodeAttr = pubServiceAnnotation.findAttributeValue("businessCode");
        if (businessCodeAttr == null) {
            holder.registerProblem(pubServiceAnnotation, InspectionBundle.message("inspection.platform.service.controller.problem.pubservice.businesscode.descriptor"), ProblemHighlightType.ERROR);
        }

        if (businessCodeAttr != null && businessCodeAttr.getValue().isNullOrEmpty()) {
            holder.registerProblem(pubServiceAnnotation, InspectionBundle.message("inspection.platform.service.controller.problem.pubservice.businesscodeempty.descriptor"), ProblemHighlightType.ERROR);
        }
    }

    /**
     * 检查是否存在PubAction注解
     */
    private void checkerPubAction(PsiMethod aMethod, ProblemsHolder holder) {
        if (aMethod.getNameIdentifier() == null) {
            return;
        }

        PsiAnnotation pubActionAnnotation = aMethod.getAnnotation(QualifiedNames.PUB_ACTION_QUALIFIED_NAME);
        if (pubActionAnnotation == null) {
            List<PsiMethod> superAnnotationOwners = AnnotationUtil.getSuperAnnotationOwners(aMethod);
            boolean anyMatch = superAnnotationOwners.stream().anyMatch(x ->
                    x.hasAnnotation(QualifiedNames.PUB_ACTION_QUALIFIED_NAME)
                            || x.hasAnnotation(QualifiedNames.GET_MAPPING_QUALIFIED_NAME)
                            || x.hasAnnotation(QualifiedNames.POST_MAPPING_QUALIFIED_NAME)
            );
            if (!anyMatch) {
                holder.registerProblem(aMethod.getNameIdentifier(), InspectionBundle.message("inspection.platform.service.controller.problem.pubservice.pubaction.descriptor"), ProblemHighlightType.ERROR, addPubActionAnnotationQuickFix);
            }

            return;
        }

        PsiAnnotationMemberValue valueAttr = pubActionAnnotation.findAttributeValue("value");
        if (valueAttr == null) {
            holder.registerProblem(aMethod.getNameIdentifier(), InspectionBundle.message("inspection.platform.service.controller.problem.pubservice.pubaction.valueattr.descriptor"), ProblemHighlightType.ERROR);
            return;
        }

        if (valueAttr.getValue().replace("/", "").isNullOrEmpty()) {
            holder.registerProblem(aMethod.getNameIdentifier(), InspectionBundle.message("inspection.platform.service.controller.problem.pubservice.pubaction.valueempty.descriptor"), ProblemHighlightType.ERROR);
            return;
        }
    }

    /**
     * 参数命名关键字冲突检查
     */
    private void checkerKeyword(PsiMethod aMethod, ProblemsHolder holder) {
        PsiParameterList parameterList = aMethod.getParameterList();
        String[] keywords = new String[]{"buguid", "oid"};
        for (PsiParameter parameter : parameterList.getParameters()) {
            if (Arrays.stream(keywords).anyMatch(x -> Objects.equals(parameter.getName().toLowerCase(), x))) {
                holder.registerProblem(parameter, InspectionBundle.message("inspection.platform.service.controller.problem.pubservice.keyword.descriptor", parameter.getName()), ProblemHighlightType.ERROR);
            }
        }
    }

    private final static class AddTagAnnotationQuickFix implements LocalQuickFix {
        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return InspectionBundle.message("inspection.platform.service.controller.addannotation.tag.quickfix");
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiElement psiElement = descriptor.getPsiElement();
            if (!(psiElement.getParent() instanceof PsiClass)) {
                return;
            }

            PsiClass psiClass = (PsiClass) psiElement.getParent();

            PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
            String annString = MessageFormat.format("@Tag(name = \"{0}\")", psiClass.getComment());
            PsiAnnotation pubAnnotation = elementFactory.createAnnotationFromText(annString, null);
            psiClass.addAnnotation(pubAnnotation);
            psiElement.addImportIfNotExist(QualifiedNames.TAG_QUALIFIED_NAME);
        }
    }


    private final static class AddPubServiceAnnotationQuickFix implements LocalQuickFix {
        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return InspectionBundle.message("inspection.platform.service.controller.addannotation.pubservice.quickfix");
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiElement psiElement = descriptor.getPsiElement();
            if (!(psiElement.getParent() instanceof PsiClass)) {
                return;
            }

            PsiClass psiClass = (PsiClass) psiElement.getParent();

            PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
            String annString = MessageFormat.format("@PubService(value = \"/{0}\", prefix = RequestPrefix.API, businessCode = \"这里输入业务单元元数据编码\")", psiClass.getName());
            PsiAnnotation pubAnnotation = elementFactory.createAnnotationFromText(annString, null);
            psiClass.addAnnotation(pubAnnotation);

            psiElement.addImportIfNotExist(QualifiedNames.PUB_SERVICE_QUALIFIED_NAME);
        }
    }

    private final static class AddPubActionAnnotationQuickFix implements LocalQuickFix {
        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return InspectionBundle.message("inspection.platform.service.controller.addannotation.pubaction.quickfix");
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiElement psiElement = descriptor.getPsiElement();
            if (!(psiElement.getParent() instanceof PsiMethod)) {
                return;
            }

            PsiMethod method = (PsiMethod) psiElement.getParent();

            PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
            String annString = MessageFormat.format("@PubAction(value = \"/{0}\", method = RequestMethod.POST)", method.getName());
            PsiAnnotation pubAnnotation = elementFactory.createAnnotationFromText(annString, null);
            method.addAnnotation(pubAnnotation);

            ((PsiJavaFile) psiElement.getContainingFile().getVirtualFile()).addImportIfNotExist(QualifiedNames.PUB_ACTION_QUALIFIED_NAME);
        }
    }
}

