package com.mysoft.devtools.Inspections;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;

/**
 * LoadDataApi 检查
 * 1、是否存在注解@PubService(value = "/BudgetsHandlers", prefix = RequestPrefix.API, businessCode = "02200301")
 * 2、是否存在@Tag(name = "获取成本的合约规划")
 * @author hezd 2023/4/27
 */
public class DataApiInspection  extends AbstractBaseJavaLocalInspectionTool {
}
