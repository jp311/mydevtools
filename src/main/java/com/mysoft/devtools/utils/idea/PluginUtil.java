package com.mysoft.devtools.utils.idea;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.project.Project;
import com.mysoft.devtools.utils.FileUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author hezd   2023/7/7
 */
public class PluginUtil {
    /*"com.baomidou.plugin.idea.mybatisx"*/

    public static IdeaPluginDescriptor findPluginById(String pluginId) {
        return Arrays.stream(PluginManager.getPlugins()).filter(x -> Objects.equals(x.getPluginId().getIdString(), pluginId)).findFirst().orElse(null);
    }

    public static Object invokeMethod(String methodFullName, Object[] args) {
        return null;
    }

    public static int loadPlugin(String pluginId, String jarName) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        IdeaPluginDescriptor plugin = findPluginById(pluginId);
        if (plugin == null) {
            return -1;
        }

        String pluginPath = plugin.getPluginPath().toString();
        String jar = FileUtil.combine(pluginPath, "lib", jarName);
        File file = new File(jar);
        URL jarUrl = file.toURI().toURL();
        URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl});
        Class<?> loadedClass = classLoader.loadClass("com.baomidou.plugin.idea.mybatisx.util.MapperUtils");

        Object result = loadedClass.getMethod("findMappers", Project.class).invoke(null, IdeaContext.getActiveProject());
        return 0;
    }
}
