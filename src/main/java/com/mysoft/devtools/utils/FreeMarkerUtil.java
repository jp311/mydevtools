package com.mysoft.devtools.utils;

import com.mysoft.devtools.dtos.GenerateContextDTO;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * <a href="http://freemarker.foofun.cn/pgui_config_sharedvariables.html">中文文档</a>
 *
 * @author hezd 2023/5/3
 */
public class FreeMarkerUtil {
    public static void generate(GenerateContextDTO context) throws IOException, TemplateException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
        cfg.setDefaultEncoding("UTF-8");

        Template template = new Template(null, context.getCodeTemplate(), cfg);


        StringWriter out = new StringWriter();
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("context", context);
        template.process(dataModel, out);

        String fileName = FileUtil.combine(context.getFilePath(), context.getFileName());
        FileUtil.writeAllText(fileName, out.toString());
    }
}
