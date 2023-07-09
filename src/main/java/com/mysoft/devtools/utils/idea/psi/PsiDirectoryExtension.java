package com.mysoft.devtools.utils.idea.psi;

import com.intellij.psi.PsiDirectory;

/**
 * @author hezd   2023/7/9
 */
public class PsiDirectoryExtension {
    public static PsiDirectory createSubdirectorys(PsiDirectory source, String path) {
        PsiDirectory result = source;
        String[] split = path.split("\\/");
        for (String item : split) {
            PsiDirectory subdirectory = result.findSubdirectory(item);
            if (subdirectory == null) {
                result = result.createSubdirectory(item);
            } else {
                result = subdirectory;
            }
        }
        return result;
    }
}
