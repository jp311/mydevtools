package com.mysoft.devtools.Inspections;

import com.intellij.codeInspection.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.source.tree.java.PsiNewExpressionImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.mysoft.devtools.bundles.InspectionBundle;
import com.mysoft.devtools.dtos.QualifiedNames;
import com.mysoft.devtools.utils.psi.VirtualFileExtension;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * new entity检查，只能通过EntityFactory.create创建entity实例
 *
 * @author hezd 2023/4/24
 */
@ExtensionMethod(VirtualFileExtension.class)
public class NewEntityInspection extends AbstractBaseJavaLocalInspectionTool {
    private final ReplaceWithEntityFactoryQuickFix myQuickFix = new ReplaceWithEntityFactoryQuickFix();

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitNewExpression(@NotNull PsiNewExpression expression) {
                super.visitNewExpression(expression);
                PsiType type = expression.getType();
                if (type != null && isSubclassOfBaseEntity(type, expression.getProject())) {
                    String message = InspectionBundle.message("inspection.platform.entity.create.problem.descriptor");
                    holder.registerProblem(expression, message, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, myQuickFix);
                }
            }
        };
    }

    /**
     * 判断是否BaseEntity的子类
     *
     * @param type
     * @param project
     * @return
     */
    private boolean isSubclassOfBaseEntity(PsiType type, Project project) {
        PsiClass psiClass = PsiUtil.resolveClassInType(type);
        if (psiClass != null) {
            PsiClass baseEntityClass = JavaPsiFacade.getInstance(project)
                    .findClass(QualifiedNames.BASE_ENTITY_QUALIFIED_NAME, GlobalSearchScope.allScope(project));
            return baseEntityClass != null && psiClass.isInheritor(baseEntityClass, true);
        }
        return false;
    }

    /**
     * EntityFactory.create 修复类
     */
    private static class ReplaceWithEntityFactoryQuickFix implements LocalQuickFix {

        @NotNull
        @Override
        public String getName() {
            return InspectionBundle.message("inspection.platform.entity.create.use.quickfix");
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiParserFacade parserFacade = PsiParserFacade.SERVICE.getInstance(project);
            PsiWhiteSpace newLine = (PsiWhiteSpace) parserFacade.createWhiteSpaceFromText("\n");

            PsiElement psiElement = descriptor.getPsiElement();
            PsiAnonymousClass anonymousClass = PsiTreeUtil.collectElementsOfType(psiElement, PsiAnonymousClass.class).stream().findFirst().orElse(null);
            PsiLocalVariable localVariable = (PsiLocalVariable) psiElement.getParent();

            TextRange textRange = psiElement.getTextRange();

            String entityName = localVariable.getType().getPresentableText();
            String variableName = localVariable.getName();

            PsiElementFactory factory = PsiElementFactory.getInstance(project);
            String newCode = MessageFormat.format("{0} {1} = EntityFactory.create({0}.class);", entityName, variableName);
            PsiStatement newStatement = factory.createStatementFromText(newCode, localVariable);

            List<PsiStatement> properties = new ArrayList<>();
            if (anonymousClass != null) {
                PsiCodeBlock body = PsiTreeUtil.collectElementsOfType(anonymousClass, PsiCodeBlock.class).stream().findFirst().orElse(null);

                if (body != null) {
                    newStatement.addAfter(newLine, newStatement);
                    for (PsiStatement statement : body.getStatements()) {
                        if (statement instanceof PsiExpressionStatement) {
                            PsiExpression expression = ((PsiExpressionStatement) statement).getExpression();
                            if (expression instanceof PsiMethodCallExpression) {
                                PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) expression;
                                String setMethodName = methodCallExpression.getMethodExpression().getText();
                                String propertyValue = PsiTreeUtil.findChildOfType(methodCallExpression, PsiExpressionList.class).getText();

                                newCode = MessageFormat.format("{0}.{1}{2};", variableName, setMethodName, propertyValue);
                                PsiStatement propertySetMethod = factory.createStatementFromText(newCode, null);
                                properties.add(propertySetMethod);
                            }
                        }
                    }
                }
            }


            PsiElement replaceResult = localVariable.replace(newStatement);
            properties.forEach(x -> {
                PsiElement element = replaceResult.getParent().addAfter(x, localVariable);
                element.addAfter(newLine, element);
            });

            PsiFile containingFile = replaceResult.getContainingFile();
            if (!(containingFile instanceof PsiJavaFile)) {
                return;
            }

            CodeStyleManager.getInstance(project).reformatText(containingFile, textRange.getStartOffset(), textRange.getEndOffset() + properties.size());

            ((PsiJavaFile)containingFile).addImportIfNotExist("com.mysoft.framework.mybatis.EntityFactory");
        }


        @NotNull
        @Override
        public String getFamilyName() {
            return getName();
        }
    }
}
