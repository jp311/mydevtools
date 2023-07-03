package com.mysoft.devtools.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hezd   2023/6/2
 */
@Data
@EqualsAndHashCode
public class DbLinkDTO {
    private String evnType;

    private String alias;

    private String provider;

    private String serverIp;

    private String serverPort;

    private String userName;

    private String password;

    private String dbName;
}
