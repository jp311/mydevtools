package com.mysoft.devtools.utils.idea.psi;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hezd   2023/7/9
 */
public class PsiDirectoryExtension {
    public static PsiDirectory createSubdirectorys(PsiDirectory source, String path) {
        PsiDirectory result = source;
        String[] split = path.split("/");
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

    public static <T extends PsiFile> List<T> getFiles(PsiDirectory source, Class<T> fileType) {
        List<T> files = new ArrayList<>();
        getFiles(source, fileType, files);

        return files.stream().sorted(Comparator.comparing(PsiFile::getName)).collect(Collectors.toList());
    }

    public static <T extends PsiFile> List<VirtualFile> getVirtualFiles(PsiDirectory source, Class<T> fileType) {
        return getFiles(source, fileType).stream().map(PsiFile::getVirtualFile).sorted(Comparator.comparing(VirtualFile::getName)).collect(Collectors.toList());
    }

    private static <T extends PsiFile> void getFiles(PsiDirectory directory, Class<T> fileType, List<T> files) {
        PsiFile[] psiFiles = directory.getFiles();

        for (PsiFile psiFile : psiFiles) {
            if (fileType.isInstance(psiFile)) {
                files.add(fileType.cast(psiFile));
            }
        }

        PsiDirectory[] subDirectories = directory.getSubdirectories();

        for (PsiDirectory subDirectory : subDirectories) {
            getFiles(subDirectory, fileType, files);
        }
    }
}
