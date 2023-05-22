package com.mysoft.devtools.listeners;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.util.Consumer;
import com.mysoft.devtools.bundles.LocalBundle;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Properties;


/**
 * 提交错误报告
 *
 * @author hezd   2023/5/22
 */
public class MyErrorReportSubmitter extends ErrorReportSubmitter {

    // 邮件地址和密码
    private final String email = "121136180@qq.com";
    // 邮件发送设置
    private final String smtpHost = "smtp.qq.com";
    private final String smtpPort = "465";
    private final boolean sslEnabled = true;

    // 发件人和收件人
    private final String from = "121136180@qq.com";
    private final String to = "hezd@mingyuanyun.com";

    @Override
    public @NotNull String getReportActionText() {
        return LocalBundle.message("devtools.exception.report.vendor");
    }

    @Override
    public boolean submit(IdeaLoggingEvent @NotNull [] events, @Nullable String additionalInfo, @NotNull Component parentComponent, @NotNull Consumer<? super SubmittedReportInfo> consumer) {

        try {
            // 创建邮件会话
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.auth", "true");
            if (sslEnabled) {
                props.put("mail.smtp.ssl.enable", "true");
            }

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(email, LocalBundle.message("devtools.exception.report.email.token"));
                }
            });
            // 创建邮件内容
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("mysoft-devtools error report");

            ApplicationInfo application = ApplicationInfo.getInstance();
            StringBuilder sb = new StringBuilder();
            sb.append("environment：").append(System.lineSeparator())
                    .append("user：").append(System.getProperty("user.name")).append(System.lineSeparator())
                    .append(MessageFormat.format("ide product name：{0}", application.getVersionName())).append(System.lineSeparator())
                    .append(MessageFormat.format("ide product code：{0}", application.getBuild().getProductCode())).append(System.lineSeparator())
                    .append(MessageFormat.format("ide version：{0}", application.getFullVersion())).append(System.lineSeparator())
                    .append("plugin version：").append(Objects.requireNonNull(PluginManager.getPluginByClass(MyErrorReportSubmitter.class)).getVersion()).append(System.lineSeparator()).append(System.lineSeparator()).append(System.lineSeparator());

            for (IdeaLoggingEvent event : events
            ) {
                sb.append("error infomation：").append(System.lineSeparator()).append(event.toString()).append(System.lineSeparator()).append(System.lineSeparator());
            }


            message.setText(sb.toString());

            // 发送邮件
            Transport.send(message);

            // 提示发送成功
            JOptionPane.showMessageDialog(null, LocalBundle.message("devtools.exception.report.vendor.success"), LocalBundle.message("devtools.exception.report.vendor"), JOptionPane.INFORMATION_MESSAGE);

            // 回调
            consumer.consume(new SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.NEW_ISSUE));
            return true;

        } catch (MessagingException e) {
            JOptionPane.showMessageDialog(null, "Error submitting error report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}