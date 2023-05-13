package com.mysoft.devtools.utils;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author hezd 2023/5/3
 */
public class CollectExtention {
    public static <T> T lastOrDefault(Collection<T> source){
        return source.stream().reduce((first,second) -> second).orElse(null);
    }

    public static <T> T lastOrDefault(T[] source){
        return Arrays.stream(source).reduce((first,second) -> second).orElse(null);
    }
}
