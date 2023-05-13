package com.mysoft.devtools.actions.exploreractions;

import com.intellij.psi.*;
import com.mysoft.devtools.dtos.ControllerGenerateDTO;
import com.mysoft.devtools.dtos.MyVector;
import com.mysoft.devtools.dtos.QualifiedNames;
import com.mysoft.devtools.services.AppSettingsStateService;
import com.mysoft.devtools.utils.CollectExtention;
import com.mysoft.devtools.utils.NameConventValidateUtil;
import com.mysoft.devtools.utils.psi.ProjectExtension;
import com.mysoft.devtools.utils.psi.PsiClassExtension;
import com.mysoft.devtools.utils.psi.PsiMethodExtension;
import com.mysoft.devtools.utils.psi.PsiParameterExtension;
import lombok.experimental.ExtensionMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author hezd   2023/5/7
 */
@ExtensionMethod({PsiClassExtension.class, CollectExtention.class, ProjectExtension.class, PsiMethodExtension.class, PsiParameterExtension.class})
public class GenerateJsProxyAction extends BaseGenerateAction {
    @Override
    protected String getDialogTitle() {
        return "JS代理类生成器";
    }

    @Override
    protected MyVector<String> getHeaders() {
        MyVector<String> headerNames = new MyVector<>();
        headerNames.add(""); //全选列
        headerNames.add("controller");
        headerNames.add("package");
        return headerNames;
    }

    @Override
    protected MyVector<MyVector<Object>> getDataSource() {
        List<PsiClass> psiClasses = project.deepSearchInheritorClass(QualifiedNames.CONTROLLER_QUALIFIED_NAME);

        MyVector<MyVector<Object>> result = new MyVector<>();
        psiClasses.forEach(psiClass -> {
            if (!NameConventValidateUtil.isController(psiClass)) {
                return;
            }
            ControllerGenerateDTO controllerGenerateDto = getControllerDto(psiClass);
            MyVector<Object> row = new MyVector<>();
            row.add(false);
            row.add(psiClass.getName());
            row.add(psiClass.getPackageName());
            row.setTag(controllerGenerateDto);
            result.add(row);
        });
        return result;
    }

    @Override
    protected String newFileName(MyVector<Object> selectedRow) {
        return selectedRow.get(1).toString().replace("Controller", "AppService.js");
    }

    @Override
    protected String getCodeTemplate() {
        return Objects.requireNonNull(AppSettingsStateService.getInstance().getState()).jsProxyTemplate;
    }

    private ControllerGenerateDTO getControllerDto(PsiClass psiClass) {
        ControllerGenerateDTO controller = new ControllerGenerateDTO();
        PsiAnnotation annotation = psiClass.getAnnotation(QualifiedNames.PUB_SERVICE_QUALIFIED_NAME);
        if (annotation != null) {
            PsiAnnotationMemberValue value = annotation.findAttributeValue("value");
            if (value != null) {
                controller.setPubServiceValue(value.getText());
            }

            PsiAnnotationMemberValue prefix = annotation.findAttributeValue("prefix");
            if (prefix != null) {
                controller.setPrefix(prefix.getText());
            }

            PsiAnnotationMemberValue businessCode = annotation.findAttributeValue("businessCode");
            if (businessCode != null) {
                controller.setBusinessCode(businessCode.getText());
            }
        }


        controller.setName(psiClass.getName());
        controller.setPackageName(psiClass.getPackageName());
        controller.setComment(psiClass.getComment());
        controller.setMethods(getMethodDto(psiClass));

        return controller;
    }

    private List<ControllerGenerateDTO.ControllerMethodDTO> getMethodDto(PsiClass psiClass) {
        List<ControllerGenerateDTO.ControllerMethodDTO> result = new ArrayList<>();
        PsiMethod[] allMethods = psiClass.getMethods();
        for (PsiMethod method : allMethods) {
            if (!method.isPublic()) {
                continue;
            }

            PsiAnnotation annotation = method.getAnnotation(QualifiedNames.PUB_ACTION_QUALIFIED_NAME);
            if (annotation == null) {
                continue;
            }

            PsiAnnotationMemberValue value = annotation.findAttributeValue("value");
            if (value == null) {
                continue;
            }
            PsiAnnotationMemberValue methodValue = annotation.findAttributeValue("method");
            if (methodValue == null){
                continue;
            }

            ControllerGenerateDTO.ControllerMethodDTO methodDto = mapMethodDto(method);
            methodDto.setMethod(methodValue.getText());
            methodDto.setActionValue(value.getText());
            result.add(methodDto);
        }
        return result;
    }

    public ControllerGenerateDTO.ControllerMethodDTO mapMethodDto(PsiMethod method) {
        List<ControllerGenerateDTO.ParameterDTO> parameterDtoList = Arrays.stream(method.getParameterList().getParameters()).map(this::mapParameterDto).collect(Collectors.toList());

        return ControllerGenerateDTO.ControllerMethodDTO.builder()
                .name(method.getName())
                .comment(method.getComment())
                .returnType(method.getReturnType() == null ? "void" : method.getReturnType().getPresentableText())
                .parameters(parameterDtoList)
                .build();
    }

    private ControllerGenerateDTO.ParameterDTO mapParameterDto(PsiParameter psiParameter) {
        return ControllerGenerateDTO.ParameterDTO.builder()
                .name(psiParameter.getName())
                .type(psiParameter.getType().getPresentableText())
                .comment(psiParameter.getComment())
                .build();
    }
}
