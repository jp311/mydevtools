package com.mysoft.devtools.actions.toolactions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.dtos.MysoftSettingsDTO;
import com.mysoft.devtools.services.AppSettingsStateService;
import com.mysoft.devtools.settings.MetadataConfigurable;
import com.mysoft.devtools.utils.CollectExtension;
import com.mysoft.devtools.utils.psi.IdeaNotifyUtil;
import com.mysoft.devtools.views.users.ExecuteSqlDialog;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

/**
 * @author hezd 2023/5/31
 */
@ExtensionMethod(CollectExtension.class)
public class ExecuteSqlAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        MysoftSettingsDTO settings = AppSettingsStateService.getInstance().getState();
        if (settings == null || settings.sqlToolPath == null || settings.sqlToolPath.isBlank()) {
            int res = IdeaNotifyUtil.dialogQuestion(LocalBundle.message("devtools.menutools.sqltool.settings.fail"));
            if (Messages.NO == res) {
                return;
            }
            ShowSettingsUtil.getInstance().showSettingsDialog(project, MetadataConfigurable.class);
            return;
        }

        ExecuteSqlDialog dialog = new ExecuteSqlDialog(project);
        dialog.show();
//        Caret caret = e.getData(PlatformDataKeys.CARET);
//        String injectableWithUnderscores = "_" + caret.getSelectedText() + "_";
//        VirtualFile file = e.getData(PlatformDataKeys.VIRTUAL_FILE);
//        Project project = e.getProject();
//        JSFile jsFile = (JSFile) PsiManager.getInstance(project).findFile(file);
//
//        String content = jsFile.getText();  // 获取 JavaScript 文件内容
//        JSCallExpression defineFunc = PsiTreeUtil.findChildOfType(jsFile, JSCallExpression.class);
//
//
//        GlobalSearchScope scope = ProjectScope.getAllScope(project);
//        PsiClass baseClass = JavaPsiFacade.getInstance(project).findClass("com.mysoft.czxt.cgplanmng.service.appservice.BudgetsAppService", scope);
//        CommonSpringModel psiClassSpringModel = SpringModelUtils.getInstance().getPsiClassSpringModel(baseClass);

        //检查是否存在具有给定名称/类型的 Bean
        //SpringModelSearchers.doesBeanExist(null,null);
    }


}
