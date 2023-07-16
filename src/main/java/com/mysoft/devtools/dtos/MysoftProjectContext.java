package com.mysoft.devtools.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hezd   2023/7/15
 */
public class MysoftProjectContext {

    public static void init(Project project) {
        VirtualFile bootstrapFile = FilenameIndex.getVirtualFilesByName("bootstrap.yml", GlobalSearchScope.allScope(project)).stream().findAny().orElse(null);
        appName = new HashMap<>() {{
            put("0000", "platform");
            put("0201", "cbxt");
            put("0011", "slxt");
            put("0206", "fyxt");
            put("0220", "Cgztb");
            put("0221", "clxt");
            put("0501", "gtxt");
            put("0202", "jhxt");
            put("0301", "xmk");
            put("0801", "tzsy");
        }};

        if (bootstrapFile != null) {
            // 创建 ObjectMapper 对象，并使用 YAMLFactory 初始化
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

            // 加载 YAML 文件并将其转换为 PersonDTO 对象
            File file = new File(bootstrapFile.getPath());
            try {
                YamlRoot bootstrap = mapper.readValue(file, YamlRoot.class);
                appCode = bootstrap.mysoft.application.code;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String appCode;
    private static Map<String, String> appName;

    /**
     * 系统编号，如：0220
     */
    public static String getAppCode() {
        return appCode;
    }

    /**
     * 系统名称，如果Cgztb
     */
    public static String getAppName() {
        if (appName == null) {
            return "";
        }
        return appName.get(appCode);
    }

    @Data
    @EqualsAndHashCode
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class YamlRoot {
        private Mysoft mysoft;
    }

    @Data
    @EqualsAndHashCode
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class Mysoft {
        private MysoftApplication application;
    }

    @Data
    @EqualsAndHashCode
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class MysoftApplication {
        private String code;

        private List<String> basePackages;
    }
}