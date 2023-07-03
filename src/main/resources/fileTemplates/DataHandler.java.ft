#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end

import com.mysoft.framework.modeling.dataapi.LoadDataApi;
import com.mysoft.framework.modeling.dto.FieldsSetting;
import com.mysoft.framework.modeling.dto.ListDataResult;
import com.mysoft.framework.modeling.dto.LoadDataParams;
import com.mysoft.framework.mvc.api.Controller;
import com.mysoft.framework.rpc.annotation.PubService;
import com.mysoft.framework.rpc.contants.RequestPrefix; 
import io.swagger.v3.oas.annotations.tags.Tag;

#parse("File Header.java")
@Tag(name = "")
@PubService(value = "/${NAME}", prefix = RequestPrefix.API, businessCode = "")
public class ${NAME} extends Controller implements LoadDataApi {
    /**
     * 是否开启项目过滤
     * @return
     */
    @Override
    public boolean projectFilter() {
        return false;
    }

    /**
     * 加载字段api
     * @return
     */
    @Override
    public FieldsSetting loadFields() {
        return null;
    }

    /**
     * 加载数据api
     * @param options
     * @return
     * @throws Exception
     */
    @Override
    public ListDataResult loadData(LoadDataParams options) throws Exception {
        //List<OrganizationRoleRowDTO> data = dataAuthAppService.getOrganizationRoleRows(options);
        ListDataResult result = new ListDataResult();
        result.setData(data);
        result.setTotal(data.size());
        
        return result;
    }
}