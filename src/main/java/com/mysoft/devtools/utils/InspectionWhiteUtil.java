package com.mysoft.devtools.utils;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.intellij.openapi.project.Project;
import com.mysoft.devtools.utils.psi.ProjectExtension;
import lombok.Data;
import lombok.experimental.ExtensionMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author hezd   2023/5/17
 */
@ExtensionMethod(ProjectExtension.class)
public class InspectionWhiteUtil {
    private static String FILE_NAME;
    private static WhiteSettingsDTO WHITE_CACHE;

    private static List<String> INIT_PROJECTS = new ArrayList<>();

    private static void loadSettings(Project project) {
        if (INIT_PROJECTS.contains(project.getBasePath())) {
            return;
        }
        FILE_NAME = FileUtil.combine(project.getCIDirectory(), "white_settings.xml");
        try {
            if (FileUtil.isExist(FILE_NAME)) {
                WHITE_CACHE = XmlUtil.fromFile(FILE_NAME, WhiteSettingsDTO.class);
                INIT_PROJECTS.add(project.getBasePath());
            } else {
                WHITE_CACHE = new WhiteSettingsDTO();
            }
            if (WHITE_CACHE.getPackages() == null) {
                WHITE_CACHE.setPackages(new ArrayList<>());
            }
            if (WHITE_CACHE.getQualifiedNames() == null) {
                WHITE_CACHE.setQualifiedNames(new ArrayList<>());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isWhite(String qualifiedName, String packageName, Project project) {
        loadSettings(project);
        return WHITE_CACHE.getQualifiedNames().stream().anyMatch(x -> Objects.equals(qualifiedName, x))
                || WHITE_CACHE.getPackages().stream().anyMatch(x -> Objects.equals(packageName, x));
    }

    public static void appendName(String qualifiedName) throws IOException {
        WHITE_CACHE.getQualifiedNames().add(qualifiedName);
        XmlUtil.toFile(FILE_NAME, WHITE_CACHE);
    }

    public static void appendPackage(String packageName) throws IOException {
        WHITE_CACHE.getPackages().add(packageName);
        XmlUtil.toFile(FILE_NAME, WHITE_CACHE);
    }

    @Data
    @JacksonXmlRootElement(localName = "WhiteSettings")
    private static final class WhiteSettingsDTO {
        @JacksonXmlProperty(localName = "QualifiedNames")
        private List<String> qualifiedNames;
        @JacksonXmlProperty(localName = "Packages")
        private List<String> packages;
    }
}
