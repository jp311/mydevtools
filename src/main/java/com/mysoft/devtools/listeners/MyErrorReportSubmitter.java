package com.mysoft.devtools.listeners;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.util.Consumer;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.dtos.ProblemEmailDTO;
import com.mysoft.devtools.utils.FreeMarkerUtil;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Override
    public @NotNull String getReportActionText() {
        return LocalBundle.message("devtools.exception.report.vendor");
    }

    @Override
    public boolean submit(IdeaLoggingEvent @NotNull [] events, @Nullable String additionalInfo, @NotNull Component parentComponent, @NotNull Consumer<? super SubmittedReportInfo> consumer) {

        try {
            // 创建邮件会话
            Properties props = new Properties();
            // 邮件发送设置
            String smtpHost = "smtp.qq.com";
            props.put("mail.smtp.host", smtpHost);
            String smtpPort = "465";
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.auth", "true");
            boolean sslEnabled = true;
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
            // 发件人和收件人
            String from = "121136180@qq.com";
            message.setFrom(new InternetAddress(from));
            String to = "hezd@mingyuanyun.com";
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(LocalBundle.message("devtools.exception.report.title.vendor", System.currentTimeMillis()));

            StringBuilder stack = new StringBuilder();
            for (IdeaLoggingEvent event : events
            ) {
                stack.append(event.toString()).append(System.lineSeparator());
            }

            ApplicationInfo application = ApplicationInfo.getInstance();
            ProblemEmailDTO problemEmailDTO = ProblemEmailDTO.builder()
                    .pluginUrl("https://plugins.jetbrains.com/plugin/21811-mysoft-devtools/versions")
                    .pluginVersion(Objects.requireNonNull(PluginManager.getPluginByClass(MyErrorReportSubmitter.class)).getVersion())
                    .ideInfo(MessageFormat.format("{0} （{1}）{2}", application.getVersionName(), application.getBuild().getProductCode(), application.getFullVersion()))
                    .osInfo(MessageFormat.format("{0} （{1}）", System.getProperty("os.name"), System.getProperty("os.arch")))
                    .jvmInfo(MessageFormat.format("{0} （{1}）", System.getProperty("java.vm.name"), System.getProperty("java.vm.version")))
                    .area(MessageFormat.format("{0}/{1}", System.getProperty("user.language"), System.getProperty("user.country")))
                    .osUser(System.getProperty("user.name"))
                    .operateTime(getOperateTime())
                    .additionalInfo(additionalInfo == null ? "" : additionalInfo)
                    .stack(stack.toString())
                    .build();
            String content = FreeMarkerUtil.sendEmail(problemEmailDTO);
            message.setText(content, "utf-8", "html");

            // 发送邮件
            Transport.send(message);

            // 提示发送成功
            JOptionPane.showMessageDialog(null, LocalBundle.message("devtools.exception.report.vendor.success"), LocalBundle.message("devtools.exception.report.vendor"), JOptionPane.INFORMATION_MESSAGE);

            // 回调
            consumer.consume(new SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.NEW_ISSUE));
            return true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error submitting error report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private String getOperateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
}