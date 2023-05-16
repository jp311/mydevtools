package com.mysoft.devtools.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hezd 2023/5/3
 */
public class CollectExtension {
    public static <T, R> List<R> ofType(Collection<T> source, Class<R> clazz) {
        return source.stream()
                .filter(clazz::isInstance)
                .map(x -> (R) x)
                .collect(Collectors.toList());
    }


    public static <T> T lastOrDefault(Collection<T> source) {
        return source.stream().reduce((first, second) -> second).orElse(null);
    }

    public static <T> T lastOrDefault(T[] source) {
        return Arrays.stream(source).reduce((first, second) -> second).orElse(null);
    }
}
