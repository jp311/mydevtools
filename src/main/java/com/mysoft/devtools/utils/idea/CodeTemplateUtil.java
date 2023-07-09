package com.mysoft.devtools.utils.idea;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.Consumer;

import java.io.IOException;
import java.util.Properties;

/**
 * @author hezd   2023/7/8
 */
public class CodeTemplateUtil {
    public static String getText(Project project, String templateName, Consumer<Properties> consumer) throws IOException {
        FileTemplateManager templateManager = FileTemplateManager.getInstance(project);
        FileTemplate template = templateManager.getTemplate(templateName);
        Properties properties = templateManager.getDefaultProperties();

        if (consumer != null) {
            consumer.consume(properties);
        }

        return template.getText(properties);
    }
}
