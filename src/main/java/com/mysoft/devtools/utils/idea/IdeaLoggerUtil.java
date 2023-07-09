package com.mysoft.devtools.utils.idea;

import com.intellij.openapi.diagnostic.Logger;

/**
 * @author hezd   2023/7/3
 */
public class IdeaLoggerUtil {
    private static final Logger LOGGER = Logger.getInstance(IdeaLoggerUtil.class);

    public static void info(String message) {
        LOGGER.info(message);
    }

    public static void warn(String message) {
        LOGGER.warn(message);
    }

    public static void error(String message) {
        //LOGGER.error(message);
        System.out.println(message);
    }

    public static void error(String message, Throwable t) {
        LOGGER.error(message, t);
    }
}
