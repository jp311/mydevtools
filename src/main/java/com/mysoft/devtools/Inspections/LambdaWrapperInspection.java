package com.mysoft.devtools.Inspections;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiTypesUtil;
import com.mysoft.devtools.bundles.InspectionBundle;
import com.mysoft.devtools.utils.psi.PsiExpressionExtension;
import com.mysoft.devtools.utils.psi.PsiTypeExtension;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author hezd   2023/5/27
 */
@ExtensionMethod({PsiExpressionExtension.class})
public class LambdaWrapperInspection extends AbstractBaseJavaLocalInspectionTool {
    private final static String COLUMN_NAME = "column";
    private final List<String> VALUE_TYPES = Arrays.asList("Object", "Collection<?>");

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                PsiMethod method = expression.resolveMethod();
                if (method == null || expression.getType() == null) {
                    return;
                }
                PsiClass psiClass = PsiTypesUtil.getPsiClass(expression.getType());
                if (psiClass == null) {
                    return;
                }
                if (!Objects.equals(psiClass.getName(), "LambdaQueryWrapper")) {
                    return;
                }

                PsiExpression[] expressions = expression.getArgumentList().getExpressions();
                if (expressions.length < 2) {
                    return;
                }

                int startIndex = 0;
                //方法签名参数列表
                PsiParameter[] signParameters = method.getParameterList().getParameters();
                for (int index = 0; index < signParameters.length; index++) {
                    if (Objects.equals(COLUMN_NAME, signParameters[index].getName())) {
                        startIndex = index;
                        break;
                    }
                }
                if (startIndex + 1 > signParameters.length) {
                    return;
                }
                PsiType type = signParameters[startIndex + 1].getType();
                if (!VALUE_TYPES.contains(type.getPresentableText())) {
                    return;
                }
                /*
                 * Children isNull(boolean condition, R column)
                 * default Children inSql(R column, String inValue)
                 * Children gtSql(boolean condition, R column, String inValue)
                 * */
                //参数1 一般是列名
                PsiExpression columnPsiExpression = expressions[startIndex];
                PsiType columnPsiType = columnPsiExpression.tryGetPsiType();
                PsiExpression valuePsiExpression = expressions[startIndex + 1];
                PsiType valuePsiType;
                String name = method.getName();
                switch (name) {
                    case "in":
                    case "notIn":
                        //List<UUID>
                        valuePsiType = valuePsiExpression.tryGetPsiType();
                        if (valuePsiType instanceof PsiClassReferenceType) {
                            PsiType[] parameters = ((PsiClassReferenceType) valuePsiType).getParameters();
                            if (parameters.length > 0) {
                                //UUID
                                valuePsiType = parameters[0];
                            }
                        }
                        break;
                    default:
                        valuePsiType = valuePsiExpression.tryGetPsiType();
                        break;
                }

                if (Objects.equals(valuePsiExpression.getText(), "null")) {
                    holder.registerProblem(expression, InspectionBundle.message("inspection.platform.service.lambdawrapper.problem.null.descriptor"), ProblemHighlightType.ERROR);
                }
                if (columnPsiType != null && valuePsiType != null && !PsiTypeExtension.compareTypes(columnPsiType, valuePsiType)) {
                    holder.registerProblem(expression, InspectionBundle.message("inspection.platform.service.lambdawrapper.typediscord.problem.descriptor"
                            , columnPsiType.getPresentableText(), valuePsiType.getPresentableText()), ProblemHighlightType.ERROR);
                }
            }
        };
    }
}
