package com.mysoft.devtools.actions.exploreractions;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.dtos.EntityGenerateDTO;
import com.mysoft.devtools.dtos.MyVector;
import com.mysoft.devtools.dtos.QualifiedNames;
import com.mysoft.devtools.services.AppSettingsStateService;
import com.mysoft.devtools.utils.CollectExtension;
import com.mysoft.devtools.utils.NameConventValidateUtil;
import com.mysoft.devtools.utils.psi.*;
import lombok.experimental.ExtensionMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author hezd 2023/4/27
 */
@ExtensionMethod({PsiClassExtension.class, PsiAnnotationValueExtension.class, CollectExtension.class, ProjectExtension.class, PsiMethodExtension.class, PsiParameterExtension.class, PsiFieldExtension.class})
public class GenerateEntityDtoAction extends BaseGenerateAction {
    @Override
    protected String getCodeTemplate() {
        return Objects.requireNonNull(AppSettingsStateService.getInstance().getState()).entityDtoTemplate;
    }

    @Override
    protected String newFileName(MyVector<Object> selectedRow) {
        return selectedRow.get(1).toString() + "EntityDTO.java";
    }

    @Override
    protected String getDialogTitle() {
        return LocalBundle.message("devtools.userview.generate.entitydto.dialogtitle");
    }

    @Override
    protected MyVector<String> getHeaders() {
        MyVector<String> headerNames = new MyVector<>();
        headerNames.add(""); //全选列
        headerNames.add("entity");
        headerNames.add("package");
        return headerNames;
    }

    @Override
    protected MyVector<MyVector<Object>> getDataSource() {
        List<PsiClass> psiClasses = project.deepSearchInheritorClass(QualifiedNames.BASE_ENTITY_QUALIFIED_NAME);

        MyVector<MyVector<Object>> result = new MyVector<>();
        psiClasses.forEach(psiClass -> {
            if (!NameConventValidateUtil.isEntity(psiClass)) {
                return;
            }
            EntityGenerateDTO entityGenerateDto = getEntityDto(psiClass);
            MyVector<Object> row = new MyVector<>();
            row.add(false);
            row.add(psiClass.getName());
            row.add(psiClass.getPackageName());
            row.setTag(entityGenerateDto);
            result.add(row);
        });
        return result;
    }

    private EntityGenerateDTO getEntityDto(PsiClass psiClass) {
         List<EntityGenerateDTO.EntityFieldDTO> fieldDtoList = new ArrayList<>();

        PsiField[] allFields = psiClass.getFields();
        for (PsiField field : allFields) {
            String jsonFieldName = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
            PsiAnnotation tableidAnnotation = field.getAnnotation("com.baomidou.mybatisplus.annotation.TableId");
            if (tableidAnnotation != null){
                PsiAnnotationMemberValue value = tableidAnnotation.findAttributeValue("value");
                if (value != null){
                    jsonFieldName = value.getValue();
                }
            }

             PsiAnnotation tableFieldAnnotation = field.getAnnotation("com.baomidou.mybatisplus.annotation.TableField");
            if (tableFieldAnnotation != null){
                PsiAnnotationMemberValue value = tableFieldAnnotation.findAttributeValue("value");
                if (value != null){
                    String tmp = value.getValue();
                    if (!tmp.isBlank()){
                        jsonFieldName = tmp;
                    }
                }
            }

            EntityGenerateDTO.EntityFieldDTO fieldDto = EntityGenerateDTO.EntityFieldDTO.builder()
                    .name(field.getName())
                    .jsonFieldName(jsonFieldName)
                    .type(field.getType().getPresentableText())
                    .comment(field.getComment())
                    .build();
            fieldDtoList.add(fieldDto);
        }

        return EntityGenerateDTO.builder()
                .name(psiClass.getName())
                .packageName(psiClass.getPackageName())
                .comment(psiClass.getComment())
                .fields(fieldDtoList)
                .build();
    }
}
