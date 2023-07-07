package com.mysoft.devtools.utils.psi;

import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author hezd 2023/7/7
 */
public class MapperUtil {
    public static Collection<Mapper> findMappers(Project project) {
        Collection<Mapper> domElements = findDomElements(project, Mapper.class);
        return domElements;
    }

    public static Collection<Mapper> findMappers(Project project, PsiClass clazz) {
        List<Mapper> result = Lists.newArrayList();

        for (Mapper mapper : findMappers(project)) {
            if (Objects.equals(mapper.getNamespace(), clazz.getQualifiedName())) {
                result.add(mapper);
            }
        }
        return result;
    }

    public static void findMappers(Project project, PsiMethod method) {

    }

    public static <T extends DomElement> Collection<T> findDomElements(@NotNull Project project, Class<T> clazz) {
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        List<DomFileElement<T>> elements = DomService.getInstance().getFileElements(clazz, project, scope);
        Collection<T> collection = elements.stream().map((v0) -> v0.getRootElement()).collect(Collectors.toList());
        return collection;
    }

    public static boolean isMybatisFile(@Nullable PsiFile file) {
        Boolean mybatisFile = null;
        if (file == null) {
            mybatisFile = false;
        }
        if (mybatisFile == null && !isXmlFile(file)) {
            mybatisFile = false;
        }
        if (mybatisFile == null) {
            XmlTag rootTag = ((XmlFile) file).getRootTag();
            if (rootTag == null) {
                mybatisFile = false;
            }
            if (mybatisFile == null && !"mapper".equals(rootTag.getName())) {
                mybatisFile = false;
            }
        }
        if (mybatisFile == null) {
            mybatisFile = true;
        }
        return mybatisFile.booleanValue();
    }

    static boolean isXmlFile(@NotNull PsiFile file) {
        return file instanceof XmlFile;
    }


    @Namespace("MybatisXml")
    public static interface Mapper extends DomElement {
        @Attribute("namespace")
        String getNamespace();

    }
}
