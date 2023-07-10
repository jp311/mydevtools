package com.mysoft.devtools.jobs;

import com.google.gson.Gson;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.dtos.MysoftSettingsDTO;
import com.mysoft.devtools.services.AppSettingsStateService;
import com.mysoft.devtools.utils.idea.BackgroundJobUtil;
import com.mysoft.devtools.utils.idea.IdeaNotifyUtil;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author hezd   2023/7/10
 */
public class CheckUpdateBackgroundJob extends Task.Backgroundable {
    private final static String URL = "https://plugins.jetbrains.com/api/plugins/21811/updates?channel=&size=8";
    private final Project project;
    private final MysoftSettingsDTO settings = AppSettingsStateService.getInstance().getState();

    private final int HOUR = 17; //12小时制也返回17
    private final int MINUTE = 30;
    private final int SECONE = 0;

    private static Timer TIMER;

    public CheckUpdateBackgroundJob(@Nullable Project project) {
        super(project, LocalBundle.message("devtools.jobs.checkupdate.title"));
        this.project = project;

        if (settings != null && settings.checkUpdate && TIMER == null) {
            TIMER = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    LocalTime currentTime = LocalTime.now();
                    if (currentTime.getHour() == HOUR && currentTime.getMinute() == MINUTE && currentTime.getSecond() == SECONE) {
                        BackgroundJobUtil.run(new CheckUpdateBackgroundJob(project));
                        try {
                            Thread.sleep(1000 * 10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, HOUR);
            calendar.set(Calendar.MINUTE, MINUTE);
            calendar.set(Calendar.SECOND, SECONE);
            TIMER.schedule(task, calendar.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
        }
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        if (settings == null || !settings.checkUpdate) {
            return;
        }

        System.out.println("正在执行检查更新调度任务...");
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(3000)
                .setSocketTimeout(10000)
                .build();


        HttpGet httpGet = new HttpGet(URL);
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create()
                    .setDefaultRequestConfig(requestConfig)
                    .build();

            HttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                return;
            }

            String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
            VersionResult[] result = new Gson().fromJson(responseBody, VersionResult[].class);
            String lastVersion = Arrays.stream(result).map(VersionResult::getVersion).max(String::compareTo).orElse(null);
            String versionName = PluginManager.getPluginByClass(CheckUpdateBackgroundJob.class).getVersion();

            if (lastVersion != null && !Objects.equals(lastVersion, versionName)) {
                IdeaNotifyUtil.notifyInfo(LocalBundle.message("devtools.jobs.checkupdate.title"),
                        LocalBundle.message("devtools.jobs.checkupdate.message", lastVersion)
                        , project);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Data
    private static final class VersionResult {
        private String version;
    }
}
