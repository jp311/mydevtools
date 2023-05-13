package com.mysoft.devtools.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 对应EntityDTO
 *
 * @author hezd 2023/5/11
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntityGenerateDTO {
    private String name;

    private String packageName;

    private String comment;

    private List<EntityFieldDTO> fields;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EntityFieldDTO {
        private String name;

        private String type;

        private String jsonFieldName;

        private String comment;
    }
}
