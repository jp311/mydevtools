package com.mysoft.devtools.utils.idea.psi;

import com.intellij.psi.PsiAnnotationMemberValue;

/**
 * @author hezd   2023/5/16
 */
public class PsiAnnotationValueExtension {
    public static String getValue(PsiAnnotationMemberValue memberValue) {
        if (memberValue == null || memberValue.getText() == null) {
            return "";
        }
        return memberValue.getText().replace("\"", "").trim();
    }
}
