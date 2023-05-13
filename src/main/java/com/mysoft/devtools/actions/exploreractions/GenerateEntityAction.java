package com.mysoft.devtools.actions.exploreractions;

import com.intellij.CommonBundle;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.dtos.MyVector;
import com.mysoft.devtools.medatas.EntityDTO;
import com.mysoft.devtools.services.AppSettingsStateService;
import com.mysoft.devtools.utils.CollectExtention;
import com.mysoft.devtools.utils.MetadataUtil;
import com.mysoft.devtools.utils.psi.VirtualFileExtension;
import lombok.experimental.ExtensionMethod;

import java.util.List;
import java.util.Objects;

/**
 * 生成实体
 *
 * @author hezd 2023/4/26
 */
@ExtensionMethod({VirtualFileExtension.class, CollectExtention.class})
public class GenerateEntityAction extends BaseGenerateAction {

    @Override
    protected String getDialogTitle() {
        return LocalBundle.message("devtools.generate.entity.dialogtitle");
    }

    @Override
    protected MyVector<String> getHeaders() {
        MyVector<String> headerNames = new MyVector<>();
        headerNames.add(""); //全选列
        headerNames.add(LocalBundle.message("devtools.generate.entity.header1"));
        headerNames.add(LocalBundle.message("devtools.generate.entity.header2"));
        return headerNames;
    }

    @Override
    protected MyVector<MyVector<Object>> getDataSource() {
        List<EntityDTO> entities = MetadataUtil.loadAll(EntityDTO.class);
        MyVector<MyVector<Object>> result = new MyVector<>();
        entities.forEach(x -> {
            MyVector<Object> row = new MyVector<>();
            row.add(false);
            row.add(x.getName());
            row.add(x.getDisplayName());
            row.setTag(x);
            result.add(row);
        });
        return result;
    }

    @Override
    protected String newFileName(MyVector<Object> selectedRow) {
        return selectedRow.get(1).toString().split("_").lastOrDefault() + ".java";
    }

    @Override
    protected String getCodeTemplate() {
        return Objects.requireNonNull(AppSettingsStateService.getInstance().getState()).entityTemplate;
    }
}
