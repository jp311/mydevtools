package com.mysoft.devtools.actions.toolactions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.mysoft.devtools.utils.psi.IdeaNotifyUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author hezd   2023/6/30
 */
public class DocumentAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        try {
            if (!Desktop.isDesktopSupported()) {
                IdeaNotifyUtil.dialogError("Desktop is not supported");
                return;
            }
            Desktop.getDesktop().browse(new URI("https://docs.mingyuanyun.com/pages/viewpage.action?pageId=151236868"));
        } catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
}
