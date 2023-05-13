package com.mysoft.devtools.actions.toolactions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.AllClassesSearch;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.Query;
import com.mysoft.devtools.dtos.QualifiedNames;
import com.mysoft.devtools.medatas.EntityDTO;
import com.mysoft.devtools.utils.MetadataUtil;
import com.mysoft.devtools.utils.psi.ProjectExtension;
import com.mysoft.devtools.utils.psi.VirtualFileExtension;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author hezd 2023/5/6
 */
@ExtensionMethod({VirtualFileExtension.class, ProjectExtension.class})
public class TestAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        //JavaPsiFacade.getInstance(project).findClass("", GlobalSearchScope.allScope(project));


        //checkPubService(project);
//        checkResource(project);
//        checkRepeatName(project);
        checkEntity(project);
        System.out.println("ok");
    }

    private void checkEntity(Project project) {

        List<String> failList = new ArrayList<>();
        List<PsiClass> implEntityList = project.deepSearchInheritorClass(QualifiedNames.BASE_ENTITY_QUALIFIED_NAME);

        List<EntityDTO> entityDTOS = MetadataUtil.loadAll(EntityDTO.class);

        for (PsiClass entityClass : implEntityList) {
            PsiModifierList modifierList = entityClass.getModifierList();
            try {
                if (modifierList != null && modifierList.hasModifierProperty(PsiModifier.ABSTRACT)) {
                    removeAbstractModifier(entityClass);
                }

                PsiAnnotation tableNameAnnotation = entityClass.getAnnotation("com.baomidou.mybatisplus.annotation.TableName");
                if (tableNameAnnotation == null) {
                    failList.add(entityClass.getQualifiedName() + " 没有配置TableName注解");
                    continue;
                }

                PsiAnnotationMemberValue tableNameAnnotationValue = tableNameAnnotation.findAttributeValue("value");
                if (tableNameAnnotationValue == null) {
                    failList.add(entityClass.getQualifiedName() + " TableName注解未配置value值");
                    continue;
                }
                boolean hasTableIdAnnotaion = Arrays.stream(entityClass.getFields()).anyMatch(field -> field.hasAnnotation("com.baomidou.mybatisplus.annotation.TableId"));
                if (hasTableIdAnnotaion == false) {

                    String tableName = tableNameAnnotationValue.getText().replace("\"", "");
                    EntityDTO entityDTO = entityDTOS.stream().filter(entity -> Objects.equals(entity.getName().toLowerCase(), tableName.toLowerCase())).findFirst().orElse(null);
                    if (entityDTO == null || entityDTO.getAttributes() == null){
                        continue;
                    }
                    String primaryKeyName = entityDTO.getAttributes().stream().filter(x -> Objects.equals(x.getIsPrimaryAttribute(), "true")).findFirst().orElse(null).getName();
                    PsiField psiField = Arrays.stream(entityClass.getFields()).filter(x -> Objects.equals(x.getName().toLowerCase(), primaryKeyName.toLowerCase())).findFirst().orElse(null);
                    if (psiField == null) {
                        failList.add(entityClass.getQualifiedName() + " 没有TableId注解");
                    } else {
                        if (psiField.getModifierList() != null) {

                            WriteCommandAction.writeCommandAction(project).run(() -> {
                                PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
                                PsiAnnotation tableIdAnnotation = elementFactory.createAnnotationFromText(
                                        "@TableId(value = \"" + primaryKeyName + "\", type = IdType.INPUT)", psiField);
//                                psiField.addBefore(tableIdAnnotation, psiField.getFirstChild());

                                // 获取字段的最后一个子元素，通常是注释或分号
                                PsiElement lastChild = psiField.getLastChild();

                                // 如果最后一个子元素是注释，则需要添加在该注释和字段之间，否则添加在子元素之后
                                if (lastChild instanceof PsiComment) {
                                    psiField.addAfter(tableIdAnnotation, lastChild);
                                } else {
                                    psiField.addAfter(tableIdAnnotation, psiField.getFirstChild());
                                }

                                ((PsiJavaFile) entityClass.getContainingFile()).addImportIfNotExist("com.baomidou.mybatisplus.annotation.TableId");
                            });
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("");
    }

    void removeAbstractModifier(PsiClass psiClass) {
        WriteCommandAction.writeCommandAction(psiClass.getProject()).run(() -> {
            // 移除 abstract 关键字
            PsiUtil.setModifierProperty(psiClass, PsiModifier.ABSTRACT, false);
        });
    }

    private void checkRepeatName(Project project) {
        List<String> serviceClasses = new ArrayList<>();
        GlobalSearchScope scope = ProjectScope.getAllScope(project);
        Query<PsiClass> searchQuery = AllClassesSearch.search(scope, project);
        for (PsiClass psiClass : searchQuery.findAll()) {
            try {
                if (psiClass.isAnnotationType() || psiClass.isEnum() || psiClass.isInterface() || psiClass.isRecord()) {
                    continue;
                }
                if (psiClass.getQualifiedName() == null || !psiClass.getQualifiedName().startsWith("com.mysoft.czxt")) {
                    continue;
                }

                if (psiClass.getModifierList().hasModifierProperty(PsiModifier.ABSTRACT)) {
                    continue;
                }

                if (psiClass.hasAnnotation(QualifiedNames.COMPONENT_QUALIFIED_NAME) ||
                        psiClass.hasAnnotation(QualifiedNames.SERVICE_QUALIFIED_NAME)) {
                    serviceClasses.add(psiClass.getName());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Map<String, Long> result = serviceClasses.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        System.out.println("");
    }

    private void checkResource(Project project) {
        List<String> failClasses = new ArrayList<>();
        GlobalSearchScope scope = ProjectScope.getAllScope(project);
        Query<PsiClass> searchQuery = AllClassesSearch.search(scope, project);


        PsiType baseDaoType = JavaPsiFacade.getInstance(project)
                .getElementFactory().createTypeFromText(QualifiedNames.BASE_DAO_QUALIFIED_NAME, null);

        PsiType appserviceType = JavaPsiFacade.getInstance(project)
                .getElementFactory().createTypeFromText(QualifiedNames.APP_SERVICE_QUALIFIED_NAME, null);

        PsiType domainserviceType = JavaPsiFacade.getInstance(project)
                .getElementFactory().createTypeFromText(QualifiedNames.DOMAIN_SERVICE_QUALIFIED_NAME, null);

        for (PsiClass psiClass : searchQuery.findAll()) {
            try {
                if (psiClass.isAnnotationType()) {
                    continue;
                }
                if (psiClass.getQualifiedName() == null || !psiClass.getQualifiedName().startsWith("com.mysoft.czxt")) {
                    continue;
                }

                if (psiClass.isInterface()) {
                    if (psiClass.hasAnnotation(QualifiedNames.REMOTE_SERVICE_QUALIFIED_NAME)) {
                        continue;
                    }
                    if (psiClass.getQualifiedName().endsWith("Dao")) {
                        //没有继承自BaseDao接口视为异常！
                        if (Arrays.stream(psiClass.getExtendsListTypes()).noneMatch(i -> i.isAssignableFrom(baseDaoType))) {
                            failClasses.add(psiClass.getQualifiedName() + "\t 没有继承BaseDao");
                        }
                        continue;
                    }

                    List<PsiClass> implClasses = new ArrayList<>(); // 查找实现类
                    findImplClass(psiClass, scope, implClasses);
                    if (implClasses.size() == 0) {
                        failClasses.add(psiClass.getQualifiedName() + "\t 接口没有实现类");
                        continue;
                    }
                    for (PsiClass implClass : implClasses) {
                        if (!implClass.hasAnnotation(QualifiedNames.COMPONENT_QUALIFIED_NAME) &&
                                !implClass.hasAnnotation(QualifiedNames.SERVICE_QUALIFIED_NAME)) {
                            failClasses.add(implClass.getQualifiedName() + "\t 实现类没有@Service注解");
                        }
                    }
                }

                if (Arrays.stream(psiClass.getExtendsListTypes()).anyMatch(i -> i.isAssignableFrom(appserviceType) || i.isAssignableFrom(domainserviceType))) {
                    if (psiClass.getModifierList().hasModifierProperty(PsiModifier.ABSTRACT)) {
                        List<PsiClass> implClasses = new ArrayList<>();
                        findImplClass(psiClass, scope, implClasses);
                        for (PsiClass implClass : implClasses) {
                            if (!implClass.hasAnnotation(QualifiedNames.COMPONENT_QUALIFIED_NAME) &&
                                    !implClass.hasAnnotation(QualifiedNames.SERVICE_QUALIFIED_NAME)) {
                                failClasses.add(implClass.getQualifiedName() + "\t 类没有@Service注解");
                            }
                        }
                    } else {
                        if (!psiClass.hasAnnotation(QualifiedNames.COMPONENT_QUALIFIED_NAME) &&
                                !psiClass.hasAnnotation(QualifiedNames.SERVICE_QUALIFIED_NAME)) {
                            failClasses.add(psiClass.getQualifiedName() + "\t 类没有@Service注解");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("ok");
    }

    private boolean findImplClass(PsiClass psiClass, SearchScope scope, List<PsiClass> result) {
        List<PsiClass> implClasses = ClassInheritorsSearch.search(psiClass, scope, true).findAll().stream().collect(Collectors.toList()); // 查找实现类
        if (implClasses.size() == 0) {
            return false;
        }
        for (PsiClass implClass : implClasses) {
            boolean res = findImplClass(implClass, scope, result);
            if (res == false) {
                result.add(implClass);
            }
        }
        return true;
    }

    /**
     * 检查PubService value相同的类
     *
     * @param project
     */
    private void checkPubService(Project project) {
        Map<String, List<String>> tempClass = new HashMap<>();
        Map<String, List<String>> tempMethod = new HashMap<>();
        PsiClass annotationPsiClass = JavaPsiFacade.getInstance(project).findClass(QualifiedNames.PUB_SERVICE_QUALIFIED_NAME, GlobalSearchScope.allScope(project));
        Collection<PsiClass> psiClasses = AnnotatedElementsSearch.searchPsiClasses(annotationPsiClass, GlobalSearchScope.allScope(project)).findAll();


        for (PsiClass psiClass : psiClasses) {
            PsiAnnotationMemberValue value = psiClass.getAnnotation(QualifiedNames.PUB_SERVICE_QUALIFIED_NAME).findAttributeValue("value");
            if (tempClass.containsKey(value.getText())) {
                tempClass.get(value.getText()).add(psiClass.getQualifiedName());
            } else {
                List<String> list = new ArrayList<>();
                list.add(psiClass.getQualifiedName());
                tempClass.put(value.getText(), list);
            }

            Arrays.stream(psiClass.getAllMethods()).filter(method -> method.hasAnnotation(QualifiedNames.PUB_ACTION_QUALIFIED_NAME)).forEach(method -> {
                String valueStr = psiClass.getQualifiedName() + "." + method.getAnnotation(QualifiedNames.PUB_ACTION_QUALIFIED_NAME).findAttributeValue("value").getText();
                if (tempMethod.containsKey(valueStr)) {
                    tempMethod.get(valueStr).add(psiClass.getQualifiedName() + "." + method.getName());
                } else {
                    List<String> list = new ArrayList<>();
                    list.add(psiClass.getQualifiedName() + "." + method.getName());
                    tempMethod.put(valueStr, list);
                }
            });
        }

        Map<String, List<String>> classValue = tempClass.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, List<String>> methodValue = tempMethod.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        System.out.println("ok");
    }
}
