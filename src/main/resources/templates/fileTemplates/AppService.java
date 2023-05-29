#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end

import com.mysoft.cbxt.utility.tools.*;
import com.mysoft.framework.service.AppService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.experimental.ExtensionMethod;
import org.springframework.stereotype.Service;

#parse("File Header.java")
@Service
@ExtensionMethod({CollectionExtension.class, StringExtension.class, MapUtils.class, DateTimeUtils.class, BigDecimalExtension.class})
public class ${NAME} extends AppService {

}