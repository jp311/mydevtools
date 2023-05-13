package com.mysoft.devtools.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.util.List;

/**
 * @author hezd 2023/5/3
 */
public class XmlUtil {
    private static final XmlMapper xmlMapper;

    static {
        SimpleModule module = new SimpleModule();
        xmlMapper = XmlMapper
                .xmlBuilder()
                .addModule(module)
                //字段为null，自动忽略，不再序列化
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .defaultUseWrapper(false)
                //转化成加下划线和大写
                .propertyNamingStrategy(new UpperCaseSnackNamingStrategy())
                //是否在反序列化时忽略未知属性。
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false)
                //是否在反序列化时忽略缺少的外部类型标识属性。
                .configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY,false)
                //是否在反序列化时忽略空的创建者属性。
                .configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES,false)
                //设置转换模式
                .enable(MapperFeature.USE_STD_BEAN_NAMING)
                .build();
    }

    public static String toXml(Object obj) throws JsonProcessingException {
        return xmlMapper.writeValueAsString(obj);
    }

    public static <T> T fromXml(String xml, Class<T> clazz) throws JsonProcessingException {
        return xmlMapper.readValue(xml, clazz);
    }

    public static <T> List<T> fromXmlList(String xml, Class<T> clazz) throws JsonProcessingException {
        return xmlMapper.readValue(xml, xmlMapper.getTypeFactory().constructCollectionType(List.class, clazz));
    }

    public static <T> T fromFile(String fileName, Class<T> clazz) throws IOException {
        String xml = FileUtil.readAllText(fileName);
        return fromXml(xml, clazz);
    }

    /**
     * 转大写并加下划线
     */
    public static class UpperCaseSnackNamingStrategy extends PropertyNamingStrategy.PropertyNamingStrategyBase {
        @Override
        public String translate(String input) {
            if (input == null) {
                return input;
            }
            int length = input.length();
            StringBuilder result = new StringBuilder(length * 2);
            int resultLength = 0;
            boolean wasPrevTranslated = false;
            for (int i = 0; i < length; i++) {
                char c = input.charAt(i);
                if (i > 0 || c != '_') {
                    if (Character.isUpperCase(c)) {
                        if (!wasPrevTranslated && resultLength > 0 && result.charAt(resultLength - 1) != '_') {
                            result.append('_');
                            resultLength++;
                        }
                        wasPrevTranslated = true;
                    } else {
                        wasPrevTranslated = false;
                    }
                    c = Character.toUpperCase(c);
                    result.append(c);
                    resultLength++;
                }
            }
            return resultLength > 0 ? result.toString() : input;
        }
    }

}
