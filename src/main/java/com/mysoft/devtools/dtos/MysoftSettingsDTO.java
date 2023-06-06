package com.mysoft.devtools.dtos;

import com.mysoft.devtools.utils.FileUtil;

import java.util.List;

/**
 * @author hezd 2023/4/30
 */
public class MysoftSettingsDTO {
    public MysoftSettingsDTO() {
        metadataPath = FileUtil.combine("data", "_metadata");
        author = System.getProperty("user.name");

        entityTemplate = FileUtil.readResourceContent("templates/freemarker/entity.ftl");
        jsProxyTemplate = FileUtil.readResourceContent("templates/freemarker/jsproxy.ftl");
        entityDtoTemplate = FileUtil.readResourceContent("templates/freemarker/entitydto.ftl");
    }

    /**
     * 元数据路径
     */
    public String metadataPath;
    public String metadataSyncClientPath;
    /**
     * 作者姓名（用于代码生成器），默认当前系统登录账号
     */

    public String author;
    public String entityTemplate;
    public String entityDtoTemplate;
    public String jsProxyTemplate;
    public List<DbLinkDTO> dataSources;
}
