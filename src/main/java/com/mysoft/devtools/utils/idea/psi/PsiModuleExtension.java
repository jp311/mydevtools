package com.mysoft.devtools.utils.idea.psi;

import com.intellij.ide.projectView.actions.MarkSourceRootAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import org.jetbrains.jps.model.java.JavaSourceRootType;

/**
 * @author hezd   2023/7/9
 */
public class PsiModuleExtension {
    public static PsiDirectory findOrCreateTestDir(Module module) {
        PsiDirectory testDir = null;
        ModifiableRootModel modifiableModel = ModuleRootManager.getInstance(module).getModifiableModel();
        ContentEntry[] contentEntries = modifiableModel.getContentEntries();
        if (contentEntries.length > 0) {
            VirtualFile baseDir = contentEntries[0].getFile();

            PsiDirectory psiBaseDir = PsiManager.getInstance(module.getProject()).findDirectory(baseDir);
            if (psiBaseDir != null) {
                testDir = PsiDirectoryExtension.createSubdirectorys(psiBaseDir, "src/test/java");
                baseDir.refresh(true, true);

                //将目录设置单测源代码目录，否则代码不能正常识别
                VirtualFile virtualFile = testDir.getVirtualFile();
                ContentEntry entry = MarkSourceRootAction.findContentEntry(modifiableModel, virtualFile);
                entry.addSourceFolder(virtualFile, JavaSourceRootType.TEST_SOURCE);
            }
        }
        modifiableModel.commit();
        modifiableModel.dispose();
        return testDir;
    }
}
