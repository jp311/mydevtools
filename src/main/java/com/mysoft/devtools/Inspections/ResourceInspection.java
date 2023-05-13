package com.mysoft.devtools.Inspections;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;

/**
 * 变量名称检查
 * 1、针对@Resource注入的变量名称推荐和类型命名一致，防止出现错误
 * 2、依赖注入的类型是否有@Service或Component注解
 * 3、不能同时有Service和Component注解
 * 4、类名必须全局唯一，或配置了自定义名称
 * @author hezd 2023/4/27
 */
public class ResourceInspection  extends AbstractBaseJavaLocalInspectionTool {
}
