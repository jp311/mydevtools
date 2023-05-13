package com.mysoft.devtools.Inspections;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;

/**
 * Entity声明检查，检查范围：
 * 1类、级别必须存在 @TableName
 * 2、字段列表中只能存在一个@TableId（继承父类存在TableId也不行）
 * 3、必须直接或者间接继承自com.mysoft.framework.mybatis.BaseEntity
 * 4、一键生成Dao
 * 5、一键生成Mapper.xml
 * @author hezd 2023/4/26
 */
public class EntityDeclarationInspection  extends AbstractBaseJavaLocalInspectionTool {
}
