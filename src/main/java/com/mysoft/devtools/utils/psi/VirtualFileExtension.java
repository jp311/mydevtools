package com.mysoft.devtools.utils.psi;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiJavaFile;
import com.mysoft.devtools.dtos.GenerateContextDTO;
import com.mysoft.devtools.dtos.MysoftSettingsDTO;
import com.mysoft.devtools.services.AppSettingsStateService;
import org.jetbrains.java.generate.psi.PsiAdapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author hezd
 * @date 2023/5/3
 */
public class VirtualFileExtension {
    public static void addImportIfNotExist(PsiJavaFile psiJavaFile, String importStatement) {
        boolean existImport = PsiAdapter.hasImportStatement(psiJavaFile, importStatement);
        if (!existImport) {
            PsiAdapter.addImportStatement(psiJavaFile, importStatement);
        }
    }

    public static String getPackageName(VirtualFile file) {
        String packageName = "";
        VirtualFile directory = file.isDirectory() ? file : file.getParent();
        while (directory != null && !directory.getName().equals("src")) {
            packageName = "." + directory.getName() + packageName;
            directory = directory.getParent();
        }
        packageName = packageName.substring(1);
        return packageName.replaceAll("main\\.java\\.", "");
    }

    public static  GenerateContextDTO getContext(VirtualFile file) {
        MysoftSettingsDTO settings = AppSettingsStateService.getInstance().getState();

        GenerateContextDTO context = new GenerateContextDTO();
        if (file.isDirectory()) {
            context.setFilePath(file.getPath());
        } else {
            context.setFilePath(file.getParent().getPath());
        }
        context.setPackageName(getPackageName(file));

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        context.setDate(now.format(formatter));

        context.setAuthor(settings.author);

        return context;
    }

    /**
     * 刷新目录文件(添加文件后刷新idea状态）
     *
     * @param virtualFile
     * @param project
     */
    public static void refreshFiles(VirtualFile virtualFile, Project project) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            virtualFile.refresh(false, false); // 刷新 VirtualFile 对象
            VirtualFile parent = virtualFile.getParent();
            parent.getChildren(); // 获取父目录下子节点
            parent.getChildren(); // 添加虚拟文件到父目录下，添加 PsiFile 对象也是同样的
        });
    }

    public static Module getModule(VirtualFile virtualFile, Project project){
        return ModuleUtil.findModuleForFile(virtualFile, project);
    }
}