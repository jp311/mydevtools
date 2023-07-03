package com.mysoft.devtools.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.intellij.openapi.project.Project;
import com.mysoft.devtools.utils.psi.ProjectExtension;
import lombok.Data;
import lombok.experimental.ExtensionMethod;

import java.io.IOException;
import java.util.*;

/**
 * @author hezd   2023/5/17
 */
@ExtensionMethod(ProjectExtension.class)
public class InspectionWhiteUtil {
    private static String FILE_NAME;
    private static Map<String, WhiteSettingsDTO> WHITE_CACHE;

    private static final List<String> INIT_PROJECTS = new ArrayList<>();

    public final static String NEW_SERVICE = "NewService";
    public final static String NEW_ENTITY = "NewEntity";

    public final static String ENTITY_DECLARE = "EntityDeclare";

    private synchronized static void loadSettings(Project project) {
        if (project == null) {
            return;
        }
        if (INIT_PROJECTS.contains(project.getBasePath())) {
            return;
        }
        String ciDirectory = project.getCIDirectory();
        //可能是非明源项目
        if (ciDirectory == null) {
            return;
        }
        FILE_NAME = FileUtil.combine(ciDirectory, "white_settings.xml");
        try {
            TypeReference<Map<String, WhiteSettingsDTO>> typeReference = new TypeReference<>() {
            };
            if (FileUtil.isExist(FILE_NAME)) {
                WHITE_CACHE = XmlUtil.fromFile(FILE_NAME, typeReference);
            } else {
                WHITE_CACHE = new HashMap<>();
            }
            INIT_PROJECTS.add(project.getBasePath());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isWhite(String socpe, String qualifiedName, String packageName, Project project) {
        if (project == null) {
            return false;
        }
        loadSettings(project);
        if (WHITE_CACHE == null || !WHITE_CACHE.containsKey(socpe)) {
            return false;
        }
        WhiteSettingsDTO whiteSettingsDTO = WHITE_CACHE.get(socpe);
        if (whiteSettingsDTO.getQualifiedNames() != null && whiteSettingsDTO.getQualifiedNames().stream().anyMatch(x -> Objects.equals(qualifiedName, x))) {
            return true;
        }
        return whiteSettingsDTO.getPackages() != null && whiteSettingsDTO.getPackages().stream().anyMatch(x -> Objects.equals(packageName, x));
    }

    public static void appendName(String scope, String qualifiedName) throws IOException {
        WhiteSettingsDTO whiteSettingsDTO;
        if (!WHITE_CACHE.containsKey(scope)) {
            whiteSettingsDTO = new WhiteSettingsDTO();
            WHITE_CACHE.put(scope, whiteSettingsDTO);
        } else {
            whiteSettingsDTO = WHITE_CACHE.get(scope);
        }

        if (whiteSettingsDTO.getQualifiedNames().contains(qualifiedName)) {
            return;
        }

        whiteSettingsDTO.getQualifiedNames().add(qualifiedName);
        XmlUtil.toFile(FILE_NAME, WHITE_CACHE);
    }

    public static void appendPackage(String scope, String packageName) throws IOException {
        WhiteSettingsDTO whiteSettingsDTO;
        if (!WHITE_CACHE.containsKey(scope)) {
            whiteSettingsDTO = new WhiteSettingsDTO();
            WHITE_CACHE.put(scope, whiteSettingsDTO);
        } else {
            whiteSettingsDTO = WHITE_CACHE.get(scope);
        }

        if (whiteSettingsDTO.getPackages().contains(packageName)) {
            return;
        }

        whiteSettingsDTO.getPackages().add(packageName);
        XmlUtil.toFile(FILE_NAME, WHITE_CACHE);
    }

    @Data
    @JacksonXmlRootElement(localName = "WhiteSettings")
    private static final class WhiteSettingsDTO {
        public WhiteSettingsDTO() {
            qualifiedNames = new ArrayList<>();
            packages = new ArrayList<>();
        }

        @JacksonXmlProperty(localName = "QualifiedNames")
        private List<String> qualifiedNames;
        @JacksonXmlProperty(localName = "Packages")
        private List<String> packages;
    }
}
