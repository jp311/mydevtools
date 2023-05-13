package com.mysoft.devtools.dtos;

import com.mysoft.devtools.utils.FileUtil;

/**
 * @author hezd 2023/4/30
 */
public class MysoftSettingsDTO {
    public MysoftSettingsDTO() {
        metadataPath = FileUtil.combine("data", "_metadata");
        author = System.getProperty("user.name");

        entityTemplate = FileUtil.readResourceContent("templates/entity.ftl");
        jsProxyTemplate = FileUtil.readResourceContent("templates/jsproxy.ftl");
        entityDtoTemplate = FileUtil.readResourceContent("templates/entitydto.ftl");
    }

    /**
     * 元数据路径
     */
    public String metadataPath;
    /**
     * 作者姓名（用于代码生成器），默认当前系统登录账号
     */

    public String author;
    public String entityTemplate;
    public String entityDtoTemplate;
    public String jsProxyTemplate;

}
