package com.mysoft.devtools.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author hezd   2023/5/10
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ControllerGenerateDTO {
    private String pubServiceValue;

    private String businessCode;

    private String prefix;

    private String name;

    private String packageName;

    private String comment;

    private List<ControllerMethodDTO> methods;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public final static class ControllerMethodDTO {
        private String name;

        private String returnType;

        private String comment;

        private List<ParameterDTO> parameters;

        private String actionValue;

        private String method;
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ParameterDTO {
        private String name;

        private String type;

        private String comment;
    }
}
