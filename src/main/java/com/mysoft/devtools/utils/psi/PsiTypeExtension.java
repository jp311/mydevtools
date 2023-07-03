package com.mysoft.devtools.utils.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiTypesUtil;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author hezd   2023/5/27
 */
public class PsiTypeExtension {

    public static PsiType createTypeFromText(Project project, String qualifiedName) {
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        return javaPsiFacade.getElementFactory().createTypeFromText(qualifiedName, null);
    }

    public static boolean compareTypes(PsiType leftType, PsiType rightType) {
        if (leftType == null || rightType == null) {
            return false;
        }

        if (Objects.equals(leftType, rightType)) {
            return true;
        }

        return PsiTypesUtil.compareTypes(leftType, rightType, true)
                || isAssignableFromEx(leftType, rightType)
                || isConvertGenericsType(leftType, rightType);
    }

    public static boolean isRefType(PsiType psiType) {
        return psiType instanceof PsiClassType;
    }

    public static boolean isAssignableFromEx(PsiType leftType, PsiType rightType) {
        PsiClass leftPsiClass = PsiTypesUtil.getPsiClass(leftType);
        PsiClass rightPsiClass = PsiTypesUtil.getPsiClass(rightType);
        if (leftPsiClass != null && rightPsiClass != null) {
            if (leftPsiClass.isEquivalentTo(rightPsiClass) || leftPsiClass.isInheritor(rightPsiClass, true)) {
                return true;
            }
        }
        return leftType.isAssignableFrom(rightType) || "Object".equals(rightType.getPresentableText());
    }

    /**
     * 判断类型是否能转换成指定的目标类型（泛型）
     * List<CgSolution> -->  List<T> or List<?> or List<T extends CgSolution> or List<T implements CgSolution>
     *
     * @param leftType
     * @param rightType 泛型类型
     * @return
     */
    public static boolean isConvertGenericsType(PsiType leftType, PsiType rightType) {
        if (!(rightType instanceof PsiClassReferenceType)) {
            return false;
        }
        PsiClass resolve = ((PsiClassReferenceType) rightType).resolve();
        if (!(resolve instanceof PsiTypeParameter)) {
            return false;
        }

        PsiClass leftPsiClass = PsiTypesUtil.getPsiClass(leftType);
        PsiClass rightPsiClass = PsiTypesUtil.getPsiClass(rightType);

        if (leftPsiClass == null || rightPsiClass == null) {
            return false;
        }

        PsiClass superClass = rightPsiClass.getSuperClass();
        if (superClass != null) {
            if (!PsiClassExtension.isInheritors(leftPsiClass, superClass)) {
                return false;
            }
        }

        PsiClass[] interfaces = rightPsiClass.getInterfaces();
        if (interfaces.length > 0) {
            return Arrays.stream(interfaces).anyMatch(x -> PsiClassExtension.isImpl(leftPsiClass, x.getQualifiedName()));
        }

        return true;
    }
}
