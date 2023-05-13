package com.mysoft.devtools.medatas;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

/**
 * @author hezd 2023/5/3
 */
@Data
public class EntityAttributeDTO {
    @JacksonXmlProperty(localName = "metadataStatus", isAttribute = true)
    private String metadataStatus;

    @JacksonXmlProperty(localName = "AttributeId")
    private String attributeId;
    @JacksonXmlProperty(localName = "Name")
    private String name;
    @JacksonXmlProperty(localName = "DisplayName")
    private String displayName;
    @JacksonXmlProperty(localName = "AttributeType")
    private String attributeType;
    @JacksonXmlProperty(localName = "DbType")
    private String dbType;
    @JacksonXmlProperty(localName = "Remark")
    private String remark;
    @JacksonXmlProperty(localName = "Length")
    private String length;
    @JacksonXmlProperty(localName = "IsNullable")
    private String isNullable;
    @JacksonXmlProperty(localName = "DefaultValue")
    private String defaultValue;
    @JacksonXmlProperty(localName = "ColumnNumber")
    private String columnNumber;
    @JacksonXmlProperty(localName = "IsPrimaryAttribute")
    private String isPrimaryAttribute;
    @JacksonXmlProperty(localName = "DecimalPrecision")
    private String decimalPrecision;
    @JacksonXmlProperty(localName = "RelationshipId")
    private String relationshipId;
    @JacksonXmlProperty(localName = "AllowQuickFind")
    private String allowQuickFind;
    @JacksonXmlProperty(localName = "MultiSelect")
    private String multiSelect;
    @JacksonXmlProperty(localName = "LookupPrimaryEntityId")
    private String lookupPrimaryEntityId;
    @JacksonXmlProperty(localName = "IsThousandth")
    private String isThousandth;
    @JacksonXmlProperty(localName = "IsRedundance")
    private String isRedundance;
    @JacksonXmlProperty(localName = "IsIdentity")
    private String isIdentity;
    @JacksonXmlProperty(localName = "IsEnum")
    private String isEnum;
    @JacksonXmlProperty(localName = "IsEncryption")
    private String isEncryption;
    @JacksonXmlProperty(localName = "UnencryptionSide")
    private String unencryptionSide;
    @JacksonXmlProperty(localName = "UnencryptedLength")
    private String unencryptedLength;

}
