#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end

import com.mysoft.cbxt.utility.tools.BigDecimalExtension;
import com.mysoft.cbxt.utility.tools.CollectionExtension;
import com.mysoft.cbxt.utility.tools.MapUtils;
import com.mysoft.cbxt.utility.tools.StringExtension;
import com.mysoft.framework.mvc.annotation.ActionRight;
import com.mysoft.framework.mvc.api.Controller;
import com.mysoft.framework.rpc.annotation.PubService;
import com.mysoft.framework.rpc.contants.RequestPrefix;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.experimental.ExtensionMethod;

#parse("File Header.java")
@Tag(name = "")
@PubService(value = "/${NAME}", prefix = RequestPrefix.API, businessCode = "")
@ExtensionMethod({CollectionExtension.class, MapUtils.class, StringExtension.class, BigDecimalExtension.class})
public class ${NAME} extends Controller {

}