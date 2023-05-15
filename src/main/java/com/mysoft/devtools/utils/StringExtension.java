package com.mysoft.devtools.utils;

/**
 * @author hezd   2023/5/15
 */
public class StringExtension {
    public static boolean isNullOrEmpty(String source){
        return source == null || source.isEmpty();
    }
    /**
     * 首字母小写
     */
    public static String firstLowerCase(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        char[] buffer = str.toCharArray();
        buffer[0] = Character.toLowerCase(buffer[0]);
        return new String(buffer);
    }
}
