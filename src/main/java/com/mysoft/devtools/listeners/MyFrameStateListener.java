package com.mysoft.devtools.listeners;

import com.intellij.ide.FrameStateListener;
import com.intellij.openapi.wm.IdeFrame;
import org.jetbrains.annotations.NotNull;

/**
 * 代码窗口切换监听器（注意：IDE欢迎页也会触发）
 * @author hezd 2023/4/22
 */
public class MyFrameStateListener implements FrameStateListener {
    @Override
    public void onFrameDeactivated() {
        FrameStateListener.super.onFrameDeactivated();
    }

    @Override
    public void onFrameActivated() {
        FrameStateListener.super.onFrameActivated();
    }
}
