package ${context.packageName};

import com.baomidou.mybatisplus.annotation.*;
import com.mysoft.framework.mybatis.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
* ${context.data.displayName} 实体对象
* @author ${context.author} ${context.date}
*/
@Data
@TableName("${context.data.name}")
public class ${context.data.simpleName} extends BaseEntity implements Serializable{

<#--声明变量：需要忽略的字段-->
<#assign ingoreFields = ["CreatedGUID","CreatedName","CreatedTime","ModifiedGUID","ModifiedName","ModifiedTime","VersionNumber"]>

<#list context.data.attributes as obj>
    <#if (ingoreFields?seq_contains(obj.name))>
        <#continue>
    </#if>
    /**
    * ${obj.displayName}
    */
    <#if (obj.isPrimaryAttribute == "true")>
    <#-- 主键增加TableId注解 -->
    @TableId(value = "${obj.name}" ,type = IdType.INPUT)
    <#else>
    <#-- 普通字段增加TableField注解 -->
    @TableField("${obj.name}")
    </#if>
<#--dbType转java type逻辑-->
    <#switch obj.dbType>
        <#case "uniqueidentifier">
            <#assign javaType = "UUID">
            <#break>
        <#case "bit">
            <#assign javaType = "Boolean">
        <#case "tinyint">
            <#assign javaType = "Byte">
            <#break>
        <#case "smallint">
            <#assign javaType = "short">
            <#break>
        <#case "int">
            <#assign javaType = "Integer">
            <#break>
        <#case "bigint">
            <#assign javaType = "Long">
            <#break>
        <#case "decimal">
        <#case "numeric">
            <#assign javaType = "BigDecimal">
            <#break>
        <#case "real">
            <#assign javaType = "Float">
            <#break>
        <#case "float">
        <#case "double">
            <#assign javaType = "Double">
            <#break>
        <#case "datetime">
        <#case "smalldatetime">
        <#case "date">
        <#case "time">
        <#case "datetime2">
        <#case "datetimeoffset">
            <#assign javaType = "Date">
            <#break>
        <#case "char">
        <#case "nchar">
        <#case "varchar">
        <#case "nvarchar">
        <#case "text">
        <#case "ntext">
            <#assign javaType = "String">
            <#break>
        <#case "binary">
        <#case "varbinary">
        <#case "image" >
            <#assign javaType = "byte[]">
            <#break>
        <#default>
            <#assign javaType = "Object">
    </#switch>
    <#--字段名称首字母小写-->
    private ${javaType} ${obj.name?uncap_first};
</#list>
<#if (context.data.enableSoftDelete == "true")??>
    @TableLogic
    private Integer __IsDeleted;
</#if>

}