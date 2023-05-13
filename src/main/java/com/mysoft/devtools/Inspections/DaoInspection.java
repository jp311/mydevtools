package com.mysoft.devtools.Inspections;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;

/**
 * Dao规范检查
 * 1、如果不存在mapper.xml可一键创建（补充mybatis插件没有这个功能）
 * 2、@Param检查，如果xml中参数在当前方法中不存在就提示增加参数！
 * @author hezd 2023/4/27
 */
public class DaoInspection  extends AbstractBaseJavaLocalInspectionTool {
}
