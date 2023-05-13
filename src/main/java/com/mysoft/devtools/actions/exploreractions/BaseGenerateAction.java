package com.mysoft.devtools.actions.exploreractions;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.mysoft.devtools.dtos.GenerateContextDTO;
import com.mysoft.devtools.dtos.GenerateDialogDTO;
import com.mysoft.devtools.dtos.MyVector;
import com.mysoft.devtools.utils.FreeMarkerUtil;
import com.mysoft.devtools.utils.psi.IdeaNotifyUtil;
import com.mysoft.devtools.utils.psi.VirtualFileExtension;
import com.mysoft.devtools.views.users.GenerateCodeDialog;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

/**
 * @author hezd 2023/5/9
 */
@ExtensionMethod({VirtualFileExtension.class})
public abstract class BaseGenerateAction extends AnAction {
    protected static GenerateContextDTO context;
    protected static GenerateCodeDialog dialog;

    protected static Project project;

    protected static VirtualFile virtualFile;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        try {
            // 获取当前选中的文件
            virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
            project = e.getProject();
            context = virtualFile.getContext();

            GenerateDialogDTO generateDialogDTO = new GenerateDialogDTO();
            generateDialogDTO.setDialogTitle(getDialogTitle());

            generateDialogDTO.setHeaders(getHeaders());


            generateDialogDTO.setDataSource(getDataSource());
            generateDialogDTO.setDoOKAction(this::generate);

            dialog = new GenerateCodeDialog(generateDialogDTO);
            dialog.show();
        } catch (Exception ex) {
            IdeaNotifyUtil.dialogError(ex.getMessage());
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        presentation.setVisible(e.getProject() != null);
    }

    protected abstract String getCodeTemplate();

    protected abstract String newFileName(MyVector<Object> selectedRow);

    protected abstract String getDialogTitle();

    protected abstract MyVector<String> getHeaders();

    protected abstract MyVector<MyVector<Object>> getDataSource();

    protected Boolean generate(MyVector<MyVector<Object>> vector) {
        boolean isSuccess = true;
        for (MyVector<Object> row : vector) {
            context.setData(row.getTag());
            context.setFileName(newFileName(row));
            context.setCodeTemplate(getCodeTemplate());
            try {
                FreeMarkerUtil.generate(context);
            } catch (Exception e) {
                IdeaNotifyUtil.dialogError(e.getMessage());
                isSuccess = false;
            }
        }

        virtualFile.refreshFiles(project);
        if (!isSuccess) {
            return false;
        }
        dialog.close(DialogWrapper.OK_EXIT_CODE);
        return true;
    }
}
