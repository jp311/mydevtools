package com.mysoft.devtools.dtos;

import lombok.Builder;
import lombok.Data;

/**
 * @author hezd   2023/5/24
 */
@Data
@Builder
public class ProblemEmailDTO {
    private String mysoftAppInfo;

    private String pluginUrl;

    private String pluginVersion;

    //IntelliJ IDEA （IU） 2022.3.1
    private String ideInfo;

    //Windows 10 （22H2） 19045.2965
    private String osInfo;

    private String jvmInfo;

    private String area;

    private String osUser;

    private String operateTime;

    private String additionalInfo;

    private String stack;
}
