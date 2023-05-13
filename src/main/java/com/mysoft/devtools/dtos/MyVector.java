package com.mysoft.devtools.dtos;

import java.util.Vector;

/**
 * @author hezd 2023/5/3
 */
public final class MyVector<T> extends Vector<T> {

    private Object tag;

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }
}