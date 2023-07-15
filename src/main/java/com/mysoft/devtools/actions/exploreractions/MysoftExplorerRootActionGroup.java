package com.mysoft.devtools.actions.exploreractions;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author hezd   2023/7/15
 */
public class MysoftExplorerRootActionGroup extends DefaultActionGroup {
    @Override
    public void update(@NotNull AnActionEvent e) {
        final DataContext dataContext = e.getDataContext();
        final Presentation presentation = e.getPresentation();

        final boolean enabled = isAvailable(dataContext);

        presentation.setEnabledAndVisible(enabled);
    }

    protected boolean isAvailable(DataContext dataContext) {
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
        if (editor != null && editor.getSelectionModel().hasSelection()) {
            return false;
        }
        final Project project = CommonDataKeys.PROJECT.getData(dataContext);
        final IdeView view = LangDataKeys.IDE_VIEW.getData(dataContext);
        return project != null && view != null && view.getDirectories().length != 0;
    }
}
