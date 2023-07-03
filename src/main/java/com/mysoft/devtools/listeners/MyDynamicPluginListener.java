package com.mysoft.devtools.listeners;

import com.intellij.ide.plugins.CannotUnloadPluginException;
import com.intellij.ide.plugins.DynamicPluginListener;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import org.jetbrains.annotations.NotNull;

/**
 * com.intellij.ide.plugins.DynamicPluginListener 是一个 IntelliJ IDEA 内置的插件监听器接口，用于监听插件的启用、禁用和卸载等事件。通过实现此接口，我们可以在插件生命周期的不同阶段执行特定的逻辑，例如在插件启用时注册菜单或工具栏等。
 *
 * @author hezd   2023/6/12
 */
public class MyDynamicPluginListener implements DynamicPluginListener {
    @Override
    public void beforePluginLoaded(@NotNull IdeaPluginDescriptor pluginDescriptor) {
        DynamicPluginListener.super.beforePluginLoaded(pluginDescriptor);
    }

    @Override
    public void beforePluginUnload(@NotNull IdeaPluginDescriptor pluginDescriptor, boolean isUpdate) {
        DynamicPluginListener.super.beforePluginUnload(pluginDescriptor, isUpdate);
    }

    @Override
    public void checkUnloadPlugin(@NotNull IdeaPluginDescriptor pluginDescriptor) throws CannotUnloadPluginException {
        DynamicPluginListener.super.checkUnloadPlugin(pluginDescriptor);
    }

    @Override
    public void pluginLoaded(@NotNull IdeaPluginDescriptor pluginDescriptor) {
        DynamicPluginListener.super.pluginLoaded(pluginDescriptor);
    }

    @Override
    public void pluginUnloaded(@NotNull IdeaPluginDescriptor pluginDescriptor, boolean isUpdate) {
        DynamicPluginListener.super.pluginUnloaded(pluginDescriptor, isUpdate);
    }
}
