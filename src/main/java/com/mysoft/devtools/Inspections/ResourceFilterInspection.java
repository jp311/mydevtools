package com.mysoft.devtools.Inspections;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;

/**
 * 资源话过滤规范检查
 * 1、是否存在注解@PubService(value = "/CgPlanSpResourceFilter", prefix = RequestPrefix.API)
 * 2、是否存在注解@Tag(name = "采购计划审批资源化过滤")
 * @author hezd 2023/4/27
 */
public class ResourceFilterInspection  extends AbstractBaseJavaLocalInspectionTool {
}
