package com.mysoft.devtools.Inspections;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.mysoft.devtools.dtos.QualifiedNames;
import com.mysoft.devtools.utils.StringExtension;
import com.mysoft.devtools.utils.psi.PsiAnnotationValueExtension;
import com.mysoft.devtools.utils.psi.PsiClassExtension;
import com.mysoft.devtools.utils.psi.VirtualFileExtension;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

/**
 * Entity声明检查（前置条件：继承自BaseEntity的子类）：
 * 1 类必须存在 @TableName
 * 2、字段列表中只能存在一个@TableId（继承父类存在TableId也不行）
 * 3、实体类不应该为abstract（特殊情况可添加到白名单不检查）
 * 4、一键生成Dao、一键生成Mapper.xml （TODO 待实现，应该放到IDEA的灯泡建议，不应该放到检查中！）
 *
 * @author hezd 2023/4/26
 */
@ExtensionMethod({PsiClassExtension.class, VirtualFileExtension.class, PsiAnnotationValueExtension.class, StringExtension.class})
public class EntityDeclarationInspection extends AbstractBaseJavaLocalInspectionTool {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitClass(PsiClass aClass) {
                Project project = aClass.getProject();
                if (aClass.getName() == null) {
                    return;
                }

                //非BaseEntity子类不检查
                if (!aClass.isInheritors(QualifiedNames.BASE_ENTITY_QUALIFIED_NAME, project)) {
                    return;
                }

                //1、字段列表中只能存在一个@TableId（继承父类存在TableId也不行）
                checkerTableId(aClass, holder);

                //2、检查是否存在@TableName注解
                checkerTableName(aClass, holder);

                //3、实体类不应该为abstract（特殊情况可添加到白名单不检查）
                checkerAbstract(aClass, holder);
            }
        };
    }

    /**
     * 字段列表中只能存在一个@TableId（继承父类存在TableId也不行）
     */
    private void checkerTableId(PsiClass aClass, ProblemsHolder holder) {

    }

    /**
     * 检查是否存在@TableName注解（抽象类不检查）
     */
    private void checkerTableName(PsiClass aClass, ProblemsHolder holder) {
        if (aClass.isAbstract()) {
            return;
        }

        PsiAnnotation annotation = aClass.getAnnotation(QualifiedNames.TABLE_NAME_QUALIFIED_NAME);
        if (annotation == null) {

            return;
        }

        PsiAnnotationMemberValue valueAttr = annotation.findAttributeValue("value");
        if (valueAttr == null || valueAttr.getValue().isNullOrEmpty()) {

        }
    }

    /**
     * 实体类不应该为abstract（特殊情况可添加到白名单不检查）
     */
    private void checkerAbstract(PsiClass aClass, ProblemsHolder holder) {

    }
}
