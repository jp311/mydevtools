package com.mysoft.devtools.utils;

import com.intellij.openapi.project.Project;
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
 * @author hezd
 * @date 2023/5/3
 */
public class MetadataUtil {
    public static <T> List<T> loadAll(Class<T> clazz) {
        List<T> result = new ArrayList<>();

        if (clazz == EntityDTO.class) {
            String path = getMetadataPath(clazz);
            List<File> allEntitys = FileUtil.getAllFiles(path);

            //处理.metadata.design.config的情况
            List<File> entitys = allEntitys.stream()
                    .filter(file -> !file.getName().endsWith(".metadata.config") || allEntitys.stream().noneMatch(n -> file.getName().startsWith(n.getName().substring(0, n.getName().indexOf('.'))) && n.getName().endsWith(".metadata.design.config")))
                    .distinct()
                    .collect(Collectors.toList());

            entitys.forEach(file -> {
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


    public static <T> String getMetadataPath(Class<T> clazz) {
        MysoftSettingsDTO settings = AppSettingsStateService.getInstance().getState();

        String metadataType = null;
        if (clazz == EntityDTO.class) {
            metadataType = "Entity";
        } else {
            throw new RuntimeException("不支持的元数据类型！");
        }

        String path = FileUtil.combine(settings.metadataPath, metadataType);
        if (FileUtil.isExist(path)) {
            return path;
        }

        Project project = IdeaContext.getProject();
        String basePath = project.getBasePath();
        if (basePath.toLowerCase().endsWith("src")) {
            basePath = FileUtil.getParent(basePath);
        }
        path = FileUtil.combine(basePath, settings.metadataPath, metadataType);
        if (!FileUtil.isExist(path)) {
            throw new RuntimeException("元数据目录获取失败！");
        }
        return path;
    }
}
