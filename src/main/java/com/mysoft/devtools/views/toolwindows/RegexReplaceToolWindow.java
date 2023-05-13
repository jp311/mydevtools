package com.mysoft.devtools.views.toolwindows;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.util.NlsActions;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.SideBorder;
import com.mysoft.devtools.views.BaseComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author hezd 2023/4/22
 */
public class RegexReplaceToolWindow extends BaseComponent {

    private JPanel contentPanel;

    @Override
    public JComponent getContentPanel() {

        AnAction[] actionsArray = new AnAction[2];
        actionsArray[0] = new MyAction("Play", AllIcons.Actions.Execute) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                super.actionPerformed(e);
            }
        };

        actionsArray[1] = new MyAction("Stop", AllIcons.Actions.Suspend) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                super.actionPerformed(e);
            }
        };

        DefaultActionGroup actions = DefaultActionGroup.createFlatGroup(() -> "Simple group");
        actions.addAll(actionsArray);

        DefaultActionGroup toolbarActions = new DefaultActionGroup();
        toolbarActions.addAll(actions);

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("TOP", toolbarActions, true);
        toolbar.setTargetComponent(contentPanel);
        JComponent toolbarComponent = toolbar.getComponent();
        toolbarComponent.setBorder(IdeBorderFactory.createBorder(SideBorder.BOTTOM));


        contentPanel.add(toolbarComponent);

        return contentPanel;
    }


    private static class MyAction extends DumbAwareAction {
        private MyAction(@Nullable @NlsActions.ActionText String name, @Nullable Icon icon) {
            super(name, null, icon);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            System.out.println(e.getPresentation().getDescription());
        }

        public MyAction withDefaultDescription() {
            getTemplatePresentation().setDescription(getTemplateText() + " description");
            return this;
        }

        public MyAction withDescription(@Nullable @NlsActions.ActionDescription String description) {
            getTemplatePresentation().setDescription(description);
            return this;
        }

        public MyAction withShortCut(@NotNull String shortCut) {
            setShortcutSet(CustomShortcutSet.fromString(shortCut));
            return this;
        }
    }

    private static class MyToggleAction extends MyAction implements Toggleable {
        protected boolean selected;

        private MyToggleAction(String name, Icon icon) {
            super(name, icon);
        }
    }
}
