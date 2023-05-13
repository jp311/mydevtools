package com.mysoft.devtools.Inspections;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;

/**
 * AppService 检查
 * 1、是否存在@Service注解
 * 2、是否继承AppService
 * 3、是否以AppService结尾
 * @author hezd 2023/4/27
 */
public class AppServiceInspection  extends AbstractBaseJavaLocalInspectionTool {
}
