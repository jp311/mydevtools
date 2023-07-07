package com.mysoft.devtools.dtos;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.util.NlsActions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.function.Supplier;

/**
 * @author hezd   2023/7/8
 */
public abstract class MyAnAction<T> extends AnAction {
    public MyAnAction() {
        super();
    }

    public MyAnAction(Icon icon) {
        super(icon);
    }

    public MyAnAction(@Nullable @NlsActions.ActionText String text) {
        super(text);
    }

    public MyAnAction(@Nullable @NlsActions.ActionText String text, T tag) {
        super(text);
        this.setTag(tag);
    }

    public MyAnAction(@NotNull Supplier<@NlsActions.ActionText String> dynamicText) {
        super(dynamicText);
    }

    public MyAnAction(@Nullable @NlsActions.ActionText String text, @Nullable @NlsActions.ActionDescription String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    public MyAnAction(@NotNull Supplier<@NlsActions.ActionText String> dynamicText, @Nullable Icon icon) {
        super(dynamicText, icon);
    }

    public MyAnAction(@NotNull Supplier<@NlsActions.ActionText String> dynamicText, @NotNull Supplier<@NlsActions.ActionDescription String> dynamicDescription, @Nullable Icon icon) {
        super(dynamicText, dynamicDescription, icon);
    }

    private T tag;


    public T getTag() {
        return tag;
    }

    public void setTag(T tag) {
        this.tag = tag;
    }
}
