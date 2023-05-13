package com.mysoft.devtools.Inspections;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.mysoft.devtools.dtos.QualifiedNames;
import com.mysoft.devtools.utils.psi.PsiClassExtension;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Controller检查：
 * 1、是否存在Tag注解 @Tag(name = "供应商应用服务")
 * 2、是否存在PubService注解 @PubService(value = "/Budgets", prefix = RequestPrefix.API, businessCode = "02200301")
 * 3、public方法是否存在@PubAction注解，@PubAction(value = "/getBudgetsByProject", method = RequestMethod.POST)
 * 4、参数不能带有buguid、oid关键字
 * 5、businessCode + value全局唯一检查
 * 6、PubAction.value当前controller唯一检查
 * 7、RequestBody、复杂参数检查（不能同时存在2个DTO）
 * <a href="https://dploeger.github.io/intellij-api-doc/com/intellij/codeInspection/AbstractBaseJavaLocalInspectionTool.html">...</a>
 *
 * @author hezd 2023/4/27
 */
@ExtensionMethod({PsiClassExtension.class})
public class ControllerInspection extends AbstractBaseJavaLocalInspectionTool {
    @Override
    public ProblemDescriptor @Nullable [] checkClass(@NotNull PsiClass aClass, @NotNull InspectionManager manager, boolean isOnTheFly) {
        Project project = aClass.getProject();
        boolean isController = aClass.isInheritors(QualifiedNames.CONTROLLER_QUALIFIED_NAME,project);
        if (!isController){
            return super.checkClass(aClass, manager, isOnTheFly);
        }

        aClass.getAnnotation("");
        return super.checkClass(aClass, manager, isOnTheFly);
    }

    @Override
    public ProblemDescriptor @Nullable [] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
        return super.checkMethod(method, manager, isOnTheFly);
    }


}

