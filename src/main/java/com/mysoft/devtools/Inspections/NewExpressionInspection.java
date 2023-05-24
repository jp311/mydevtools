package com.mysoft.devtools.Inspections;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInspection.*;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.mysoft.devtools.bundles.InspectionBundle;
import com.mysoft.devtools.dtos.QualifiedNames;
import com.mysoft.devtools.utils.InspectionWhiteUtil;
import com.mysoft.devtools.utils.psi.IdeaSdkAdapter;
import com.mysoft.devtools.utils.psi.PsiClassExtension;
import com.mysoft.devtools.utils.psi.VirtualFileExtension;
import com.mysoft.devtools.views.users.InspectionWhiteDialog;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.lang.manifest.psi.ManifestTokenType;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * new entity检查，只能通过EntityFactory.create创建entity实例
 *
 * @author hezd 2023/4/24
 */
@ExtensionMethod({VirtualFileExtension.class, PsiClassExtension.class})
public class NewExpressionInspection extends AbstractBaseJavaLocalInspectionTool {
    private final ReplaceWithEntityFactoryQuickFix myQuickFix = new ReplaceWithEntityFactoryQuickFix();
    private final AddWhiteQuickFix addWhiteQuickFix = new AddWhiteQuickFix();

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitNewExpression(@NotNull PsiNewExpression expression) {
                PsiType type = expression.getType();
                if (type == null) {
                    return;
                }

                PsiClass psiClass = PsiUtil.resolveClassInType(type);
                if (psiClass == null || psiClass instanceof PsiTypeParameter) {
                    return;
                }
                if (psiClass.isEnum() || psiClass.isInterface() || psiClass.isAnnotationType() || psiClass.isRecord()) {
                    return;
                }
                Project project = psiClass.getProject();

                //白名单检查
                boolean isWhite = InspectionWhiteUtil.isWhite(psiClass.getQualifiedName(), psiClass.getPackageName(), project);
                if (isWhite) {
                    return;
                }

                if (psiClass.isInheritors(QualifiedNames.BASE_ENTITY_QUALIFIED_NAME, project)) {
                    String message = InspectionBundle.message("inspection.platform.entity.create.problem.descriptor");
                    holder.registerProblem(expression, message, ProblemHighlightType.ERROR, myQuickFix, addWhiteQuickFix);
                }

                List<PsiClass> superAnnotationOwners = new ArrayList<>(AnnotationUtil.getSuperAnnotationOwners(psiClass));
                superAnnotationOwners.add(psiClass);
                boolean isService = superAnnotationOwners.stream().anyMatch(x -> x.hasAnnotation(QualifiedNames.COMPONENT_QUALIFIED_NAME)
                        || x.hasAnnotation(QualifiedNames.SERVICE_QUALIFIED_NAME)
                );

                if (isService) {
                    String message = InspectionBundle.message("inspection.platform.service.create.problem.descriptor");
                    holder.registerProblem(expression, message, ProblemHighlightType.ERROR, addWhiteQuickFix);
                }

            }
        };
    }

    /**
     * EntityFactory.create 修复类
     */
    private final static class ReplaceWithEntityFactoryQuickFix implements LocalQuickFix {

        @NotNull
        @Override
        public String getName() {
            return InspectionBundle.message("inspection.platform.entity.create.use.quickfix");
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiElement psiElement = descriptor.getPsiElement();
            PsiAnonymousClass anonymousClass = PsiTreeUtil.collectElementsOfType(psiElement, PsiAnonymousClass.class).stream().findFirst().orElse(null);
            PsiLocalVariable localVariable = (PsiLocalVariable) psiElement.getParent();

            String entityName = localVariable.getType().getPresentableText();
            String variableName = localVariable.getName();

            PsiNewExpression newExpr = (PsiNewExpression) psiElement;
            PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);

            PsiCodeBlock body = PsiTreeUtil.collectElementsOfType(anonymousClass, PsiCodeBlock.class).stream().findFirst().orElse(null);

            List<PsiStatement> properties = new ArrayList<>();
            if (body != null) {
                for (PsiStatement statement : body.getStatements()) {
                    if (statement instanceof PsiExpressionStatement) {
                        PsiExpression expression = ((PsiExpressionStatement) statement).getExpression();
                        if (expression instanceof PsiMethodCallExpression) {
                            PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) expression;
                            String setMethodName = methodCallExpression.getMethodExpression().getText();
                            String propertyValue = "";
                            PsiExpressionList childOfType = PsiTreeUtil.findChildOfType(methodCallExpression, PsiExpressionList.class);
                            if (childOfType != null) {
                                propertyValue = childOfType.getText();
                            }
                            String newCode = MessageFormat.format("{0}.{1}{2};", variableName, setMethodName, propertyValue);
                            PsiStatement propertySetMethod = elementFactory.createStatementFromText(newCode, null);
                            properties.add(propertySetMethod);
                        }
                    }
                }
            }

            PsiExpression createCall = elementFactory.createExpressionFromText(String.format("EntityFactory.create(%s.class)", entityName), null);
            PsiElement result = newExpr.replace(createCall);

            PsiFile containingFile = result.getContainingFile();
            PsiElement nextSibling = localVariable.getNextSibling();
            if (properties.size() > 0) {
                PsiParserFacade parserFacade = IdeaSdkAdapter.getPsiParserFacade(project);
                for (PsiStatement property : properties) {
                    if (!(nextSibling instanceof PsiWhiteSpace)) {
                        nextSibling = localVariable.addAfter(parserFacade.createWhiteSpaceFromText("\n"), result);
                    }
                    localVariable.getNode().addLeaf(ManifestTokenType.NEWLINE, "\n", null);
                    localVariable.addAfter(property, nextSibling);
                }
            }

            if (containingFile == null) {
                return;
            }

            ((PsiJavaFile) containingFile).addImportIfNotExist("com.mysoft.framework.mybatis.EntityFactory");

            PsiMethod method = PsiTreeUtil.getParentOfType(localVariable, PsiMethod.class);
            if (method != null) {
                CodeStyleManager.getInstance(project).reformatText(containingFile, method.getTextRange().getStartOffset(), method.getTextRange().getEndOffset());
            }
        }


        @NotNull
        @Override
        public String getFamilyName() {
            return getName();
        }
    }

    public final static class AddWhiteQuickFix implements LocalQuickFix {
        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return InspectionBundle.message("inspection.platform.service.addwhite.quickfix");
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiElement psiElement = descriptor.getPsiElement();
            if (!(psiElement instanceof PsiNewExpression)) {
                return;
            }
            PsiNewExpression newExpression = (PsiNewExpression) psiElement;

            PsiType type = newExpression.getType();
            if (type == null) {
                return;
            }


            PsiClass aClass = PsiUtil.resolveClassInType(type);
            if (aClass == null) {
                return;
            }
            InspectionWhiteDialog dialog = new InspectionWhiteDialog(aClass.getQualifiedName(), aClass.getPackageName());
            if (dialog.showAndGet()) {
                aClass.refresh();
            }
        }
    }
}
