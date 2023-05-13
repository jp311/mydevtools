package com.mysoft.devtools.utils;

import com.intellij.psi.PsiClass;
import com.mysoft.devtools.utils.psi.PsiClassExtension;
import lombok.experimental.ExtensionMethod;

import java.util.Objects;

/**
 * @author hezd   2023/5/9
 */
@ExtensionMethod({PsiClassExtension.class})
public class NameConventValidateUtil {
    public static boolean isController(PsiClass psiClass) {
        return Objects.requireNonNull(psiClass.getName()).endsWith("Controller") && psiClass.getPackageName().endsWith("controller");
    }

    public static boolean isEntity(PsiClass psiClass){
        return psiClass.getPackageName().endsWith("entity");
    }
}
