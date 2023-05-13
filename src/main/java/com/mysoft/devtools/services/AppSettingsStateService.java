package com.mysoft.devtools.services;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.mysoft.devtools.dtos.MysoftSettingsDTO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * https://www.ideaplugin.com/idea-docs/Part%20II%20%E2%80%94%20Base%20Platform/Settings/Settings%20Tutorial.html#%E6%A6%82%E8%BF%B0
 * 插件设置持久化服务
 *
 * @author hezd 2023/4/22
 */
@State(
        name = "com.mysoft.devtools.services.AppSettingsStateService",
        storages = @Storage("mysoft_settings.xml")
)
public class AppSettingsStateService implements PersistentStateComponent<MysoftSettingsDTO> {
    MysoftSettingsDTO mState = new MysoftSettingsDTO();

    public static AppSettingsStateService getInstance() {
        return ApplicationManager.getApplication().getService(AppSettingsStateService.class);
    }

    @Override
    public @Nullable MysoftSettingsDTO getState() {
        return mState;
    }

    @Override
    public void loadState(@NotNull MysoftSettingsDTO state) {
        mState = state;
    }
}
