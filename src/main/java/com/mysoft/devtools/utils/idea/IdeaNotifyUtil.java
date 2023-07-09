package com.mysoft.devtools.utils.idea;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.HyperlinkListener;
import java.awt.*;

/**
 * <a href="https://plugins.jetbrains.com/docs/intellij/notifications.html">...</a>
 *
 * @author hezd 2023/5/3
 */
public class IdeaNotifyUtil {
    private final static String title = "明源开发工具箱";

    public static void showInfoMessage(String message) {
        Messages.showInfoMessage(message, title);
    }

    public static void showDialog(String message, String[] options, int defaultOptionIndex, @Nullable Icon icon) {
        Messages.showDialog(message, title, options, defaultOptionIndex, icon);
    }

    public static void tipWarn(String message) {
        Messages.showMessageDialog(message, title, Messages.getWarningIcon());
    }

    public static void tipError(String message) {
        Messages.showMessageDialog(message, title, Messages.getErrorIcon());
    }

    public static void tipInfo(String message) {
        Messages.showMessageDialog(message, title, Messages.getInformationIcon());
    }

    public static int dialogQuestion(String message) {
        String[] buttons = new String[]{Messages.getOkButton(), Messages.getCancelButton()};
        return Messages.showDialog(message, title, buttons, 0, Messages.getQuestionIcon());
    }

    public static void dialogInfo(String message) {
        Messages.showMessageDialog(message, title, Messages.getInformationIcon());
    }

    public static void dialogError(String message) {
        Messages.showMessageDialog(message, title, Messages.getErrorIcon());
    }

    public static void dialogWarn(String message) {
        Messages.showMessageDialog(message, title, Messages.getWarningIcon());
    }

    public static void notifyError(String title, String message, Project project) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("mysoft-balloon")
                .createNotification(title, message, NotificationType.ERROR)
                .notify(project);

        //Notifications.Bus.notify(notify,project)
    }

    public static void notifyWarn(String message, Project project) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("mysoft-balloon")
                .createNotification(message, NotificationType.WARNING)
                .notify(project);

        //Notifications.Bus.notify(notify,project)
    }

    public static void notifyInfo(String title, String message, Project project) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("mysoft-balloon")
                .createNotification(title, message, NotificationType.INFORMATION)
                .notify(project);

        //Notifications.Bus.notify(notify,project)
    }

    /**
     * <a href="https://jetbrains.design/intellij/controls/balloon/">官方文档</a>
     * 用完后请手动释放： balloon.dispose()
     *
     * @param component 组件
     * @param message   消息
     * @param listener  x
     * @return x
     */
    public static Balloon balloonMessage(JComponent component, String message, HyperlinkListener listener) {
        BalloonBuilder balloonBuilder = JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(message, MessageType.INFO, listener);
        Balloon balloon = balloonBuilder.createBalloon();
        balloon.show(new RelativePoint(component, new Point(0, 0)), Balloon.Position.below);
        return balloon;
    }
}
