package com.mysoft.devtools.dtos;

import lombok.Data;

/**
 * @author hezd 2023/5/3
 */
@Data
public class GenerateContextDTO {

    /**
     * 包名
     */
    private String packageName;
    /**
     * 保存路径
     */
    private String filePath;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 作者
     */
    private String author;
    /**
     * 日期
     */
    private String date;
    /**
     * 数据
     */
    private Object data;

    private String codeTemplate;
}
