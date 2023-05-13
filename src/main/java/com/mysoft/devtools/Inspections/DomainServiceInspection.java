package com.mysoft.devtools.Inspections;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;

/**
 * DomainService 检查
 * 1、是否存在@Service注解
 * 2、是否继承DomainService
 * 3、是否以DomainService结尾
 * @author hezd 2023/4/27
 */
public class DomainServiceInspection extends AbstractBaseJavaLocalInspectionTool {
}
