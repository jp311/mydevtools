package com.mysoft.devtools.utils;

import java.util.Base64;

/**
 * @author hezd   2023/7/6
 */
public class Base64Util {
    public static String decode(String str) {
        byte[] decodedBytes = Base64.getDecoder().decode(str);
        return new String(decodedBytes);
    }

    public static String encode(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes());
    }
}
