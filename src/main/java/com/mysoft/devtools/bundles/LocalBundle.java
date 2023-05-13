package com.mysoft.devtools.bundles;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

/**
 * @author hezd   2023/5/13
 */
public class LocalBundle extends DynamicBundle {

    private static final LocalBundle OUR_INSTANCE = new LocalBundle();

    @NonNls
    public static final String BUNDLE = "messages.LocalBundle";

    private LocalBundle() {
        super(BUNDLE);
    }

    public static @Nls String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key,
                                      Object @NotNull ... params) {
        return OUR_INSTANCE.getMessage(key, params);
    }
}
