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
import org.jetbrains.lang.manifest.psi.ManifestTokenType;

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

            PsiElement psiElement = descriptor.getPsiElement();
            PsiAnonymousClass anonymousClass = PsiTreeUtil.collectElementsOfType(psiElement, PsiAnonymousClass.class).stream().findFirst().orElse(null);
            PsiLocalVariable localVariable = (PsiLocalVariable) psiElement.getParent();

            String entityName = localVariable.getType().getPresentableText();
            String variableName = localVariable.getName();

            PsiNewExpression newExpr = (PsiNewExpression) psiElement;
            PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);

            PsiCodeBlock body = PsiTreeUtil.collectElementsOfType(anonymousClass, PsiCodeBlock.class).stream().findFirst().orElse(null);

            List<PsiStatement> propertys = new ArrayList<>();
            if (body != null) {
                for (PsiStatement statement : body.getStatements()) {
                    if (statement instanceof PsiExpressionStatement) {
                        PsiExpression expression = ((PsiExpressionStatement) statement).getExpression();
                        if (expression instanceof PsiMethodCallExpression) {
                            PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) expression;
                            String setMethodName = methodCallExpression.getMethodExpression().getText();
                            String propertyValue = PsiTreeUtil.findChildOfType(methodCallExpression, PsiExpressionList.class).getText();

                            String newCode = MessageFormat.format("{0}.{1}{2};", variableName, setMethodName, propertyValue);
                            PsiStatement propertySetMethod = elementFactory.createStatementFromText(newCode, null);
                            propertys.add(propertySetMethod);
                        }
                    }
                }
            }

            PsiExpression createCall = elementFactory.createExpressionFromText(String.format("EntityFactory.create(%s.class)", entityName), null);
            PsiElement result = newExpr.replace(createCall);

            PsiFile containingFile = result.getContainingFile();
            PsiElement nextSibling = localVariable.getNextSibling();
            if (propertys.size() > 0) {
                for (PsiStatement property : propertys) {
                    if (!(nextSibling instanceof PsiWhiteSpace)) {
                        nextSibling = localVariable.addAfter(parserFacade.createWhiteSpaceFromText("\n"), result);
                    }
                    localVariable.getNode().addLeaf(ManifestTokenType.NEWLINE, "\n", null);
                    localVariable.addAfter(property, nextSibling);
                }
            }

            if (containingFile == null){
                return;
            }

            ((PsiJavaFile) containingFile).addImportIfNotExist("com.mysoft.framework.mybatis.EntityFactory");

            PsiMethod method = PsiTreeUtil.getParentOfType(localVariable, PsiMethod.class);
            CodeStyleManager.getInstance(project).reformatText(method.getContainingFile(), method.getTextRange().getStartOffset(), method.getTextRange().getEndOffset());
        }


        @NotNull
        @Override
        public String getFamilyName() {
            return getName();
        }
    }
}
