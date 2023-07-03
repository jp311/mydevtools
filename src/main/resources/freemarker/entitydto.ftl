package ${context.packageName};

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mysoft.framework.service.dto.DTO;
import lombok.Data;

import java.io.Serializable;
import java.util.*;

/**
 * ${context.data.comment}
 *
 * @author ${context.author} ${context.date}
 */
@Data
public class ${context.data.name}EntityDTO extends DTO implements Serializable {
    <#list context.data.fields as field>
    /**
     * ${field.comment}
     */
    @JsonProperty("${field.jsonFieldName}")
    private ${field.type} ${field.name};
    </#list>
}