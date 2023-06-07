package com.mysoft.devtools.utils;

import com.intellij.openapi.project.Project;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.dtos.MysoftSettingsDTO;
import com.mysoft.devtools.medatas.EntityDTO;
import com.mysoft.devtools.services.AppSettingsStateService;
import com.mysoft.devtools.utils.psi.IdeaContext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hezd 2023/5/3
 */
public class MetadataUtil {
    public static <T> List<T> loadAll(Class<T> clazz) {
        List<T> result = new ArrayList<>();

        if (clazz == EntityDTO.class) {
            String path = getMetadataPath(clazz);
            List<File> allEntity = FileUtil.getAllFiles(path);

            //处理.metadata.design.config的情况
            List<File> entities = allEntity.stream()
                    .filter(file -> !file.getName().endsWith(".metadata.config") || allEntity.stream().noneMatch(n -> file.getName().startsWith(n.getName().substring(0, n.getName().indexOf('.'))) && n.getName().endsWith(".metadata.design.config")))
                    .distinct()
                    .collect(Collectors.toList());

            entities.forEach(file -> {
                try {
                    T entity = XmlUtil.fromFile(file.getAbsolutePath(), clazz);
                    result.add(entity);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        }
        return result;
    }

    public static String getProductMetadataRootPath() {
        MysoftSettingsDTO settings = AppSettingsStateService.getInstance().getState();
        if (settings == null) {
            throw new RuntimeException(LocalBundle.message("devtools.generate.settings.notfound"));
        }

        if (FileUtil.isExist(settings.metadataPath)) {
            return settings.metadataPath;
        }

        Project project = IdeaContext.getActiveProject();
        String basePath = project.getBasePath();
        if (basePath == null || basePath.isEmpty()) {
            throw new RuntimeException(LocalBundle.message("devtools.generate.metadata.notfound"));
        }
        if (basePath.toLowerCase().endsWith("src")) {
            basePath = FileUtil.getParent(basePath);
        }
        var path = FileUtil.combine(basePath, settings.metadataPath);
        if (!FileUtil.isExist(path)) {
            throw new RuntimeException(LocalBundle.message("devtools.generate.metadata.notfound"));
        }
        return path;
    }

    public static <T> String getMetadataPath(Class<T> clazz) {

        String metadataType;
        if (clazz == EntityDTO.class) {
            metadataType = "Entity";
        } else {
            throw new RuntimeException(LocalBundle.message("devtools.generate.metadata.notsupport"));
        }

        String productMetadataRootPath = getProductMetadataRootPath();
        var path = FileUtil.combine(productMetadataRootPath, metadataType);
        return path;
    }
}
