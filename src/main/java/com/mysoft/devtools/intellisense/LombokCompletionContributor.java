package com.mysoft.devtools.intellisense;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.IconManager;
import com.intellij.util.ProcessingContext;
import com.mysoft.devtools.dtos.QualifiedNames;
import com.mysoft.devtools.utils.psi.PsiClassObjectAccessExpressionExtension;
import com.mysoft.devtools.utils.psi.PsiMethodExtension;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <a href="https://github.com/JetBrains/intellij-sdk-code-samples/tree/main/simple_language_plugin">...</a>
 * lombokMethod@x.svg 扩展方法智能提示
 *
 * @author hezd   2023/5/21
 */
@ExtensionMethod({PsiMethodExtension.class, PsiClassObjectAccessExpressionExtension.class})
public class LombokCompletionContributor extends CompletionContributor {

    public LombokCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new LombokCompletionProvider());

    }

    private static class LombokCompletionProvider extends CompletionProvider<CompletionParameters> {

        private final static Icon LOMBOK_METHOD_ICON = IconManager.getInstance().getIcon("/icons/lombokMethod", LombokCompletionContributor.class);

        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {

            PsiFile originalFile = parameters.getOriginalFile();
            if (!(originalFile instanceof PsiJavaFile)) {
                return;
            }

            PsiElement position = parameters.getPosition();

            PsiClass psiClass = PsiTreeUtil.getParentOfType(position, PsiClass.class);
            if (psiClass == null) {
                return;
            }
            PsiAnnotation annotation = psiClass.getAnnotation(QualifiedNames.EXTENSION_METHOD_QUALIFIED_NAME);
            if (annotation == null) {
                return;
            }

            PsiAnnotationMemberValue attributeValue = annotation.findAttributeValue("value");
            if (attributeValue == null) {
                return;
            }

            PsiType elementType = null;
            if (position.getContext() instanceof PsiReferenceExpressionImpl) {
                PsiExpression qualifierExpression = ((PsiReferenceExpressionImpl) position.getContext()).getQualifierExpression();
                if (qualifierExpression != null) {
                    elementType = qualifierExpression.getType();
                }
            }
            if (elementType == null) {
                return;
            }
            PsiType finalElementType = elementType;

            List<PsiClassObjectAccessExpression> extensionClasses = Arrays.stream(attributeValue.getChildren()).filter(x -> x instanceof PsiClassObjectAccessExpression).map(x -> (PsiClassObjectAccessExpression) x).collect(Collectors.toList());
            List<PsiMethod> extensionMethods = extensionClasses.stream().flatMap(x ->
                    Arrays.stream(x.getPsiClass().getAllMethods()).filter(m ->
                            m.isPublic() && m.isStatic()
                                    && m.getParameterList().getParameters().length > 0
                                    && finalElementType.isConvertibleFrom(m.getParameterList().getParameters()[0].getType())
                    )
            ).collect(Collectors.toList());


            extensionMethods.forEach(m -> {
                List<String> parameterTokenList = Arrays.stream(m.getParameterList().getParameters()).skip(1).map(p -> p.getType().getPresentableText() + " " + p.getName()).collect(Collectors.toList());
                String paramterTokenString = "(" + String.join(", ", parameterTokenList) + ")";

                String returnType = "void";
                if (m.getReturnType() != null) {
                    returnType = m.getReturnType().getPresentableText();
                }
                result.addElement(LookupElementBuilder.create(m).withIcon(LOMBOK_METHOD_ICON).withTypeText(returnType).withTailText(paramterTokenString));
            });
        }
    }
}
