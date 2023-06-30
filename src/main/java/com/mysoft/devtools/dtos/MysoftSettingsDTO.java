package com.mysoft.devtools.dtos;

import com.mysoft.devtools.utils.FileUtil;

import java.util.List;

/**
 * @author hezd 2023/4/30
 */
public class MysoftSettingsDTO {
    public String sqlPath;

    /**
     * 元数据路径
     */
    public String metadataPath;
    public String metadataSyncClientPath;
    public String sqlToolPath;

    public MysoftSettingsDTO() {
        metadataPath = FileUtil.combine("data", "_metadata");
        sqlPath = FileUtil.combine("data", "sql");
        author = System.getProperty("user.name");

        entityTemplate = FileUtil.readResourceContent("freemarker/entity.ftl");
        jsProxyTemplate = FileUtil.readResourceContent("freemarker/jsproxy.ftl");
        entityDtoTemplate = FileUtil.readResourceContent("freemarker/entitydto.ftl");
    }

    /**
     * 作者姓名（用于代码生成器），默认当前系统登录账号
     */

    public String author;
    public String entityTemplate;
    public String entityDtoTemplate;
    public String jsProxyTemplate;
    public List<DbLinkDTO> dataSources;
}
