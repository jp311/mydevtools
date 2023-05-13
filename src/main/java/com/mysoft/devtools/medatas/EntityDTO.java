package com.mysoft.devtools.medatas;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.mysoft.devtools.utils.CollectExtention;
import lombok.Data;
import lombok.experimental.ExtensionMethod;

import java.io.Serializable;
import java.util.List;

/**
 * @author hezd 2023/5/2
 */
@Data
@JacksonXmlRootElement(localName = "MetadataEntity")
@ExtensionMethod({CollectExtention.class})
public class EntityDTO implements Serializable {
    /**
     * 根节点属性
     */
    @JacksonXmlProperty(localName = "EntityId", isAttribute = true)
    private String entityId;
    @JacksonXmlProperty(localName = "Name", isAttribute = true)
    private String name;
    private String simpleName;
    @JacksonXmlProperty(localName = "DisplayName", isAttribute = true)
    private String displayName;
    @JacksonXmlProperty(localName = "Application", isAttribute = true)
    private String application;
    @JacksonXmlProperty(localName = "Remark", isAttribute = true)
    private String remark;
    @JacksonXmlProperty(localName = "IsOpenApprove", isAttribute = true)
    private String isOpenApprove;
    @JacksonXmlProperty(localName = "IsOpenOData", isAttribute = true)
    private String isOpenOdata;
    @JacksonXmlProperty(localName = "IsEnableIsolateForCompany", isAttribute = true)
    private String isEnableIsolateForCompany;
    @JacksonXmlProperty(localName = "CreatedBy", isAttribute = true)
    private String createdBy;
    @JacksonXmlProperty(localName = "CreatedOn", isAttribute = true)
    private String createdOn;
    @JacksonXmlProperty(localName = "ModifiedBy", isAttribute = true)
    private String modifiedBy;
    @JacksonXmlProperty(localName = "ModifiedOn", isAttribute = true)
    private String modifiedOn;
    @JacksonXmlProperty(localName = "metadataStatus", isAttribute = true)
    private String metadataStatus;
    @JacksonXmlProperty(localName = "metadataversion", isAttribute = true)
    private String metadataVersion;
    @JacksonXmlProperty(localName = "functionGUID", isAttribute = true)
    private String functionGuid;
    @JacksonXmlProperty(localName = "enableSoftDelete", isAttribute = true)
    private String enableSoftDelete;

    public String getSimpleName() {
        return name.split("_").lastOrDefault();
    }

    /**
     * 元数据属性
     */
    @JacksonXmlElementWrapper(localName = "Attributes")
    public List<EntityAttributeDTO> attributes;


}
