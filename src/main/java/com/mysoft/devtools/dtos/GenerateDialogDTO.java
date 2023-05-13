package com.mysoft.devtools.dtos;

import java.util.function.Function;

/**
 * @author hezd 2023/5/3
 */
public final class GenerateDialogDTO {
    /**
     * 对话框标题
     */
    private String dialogTitle;
    private MyVector<String> headers;
    /**
     * 数据源
     */
    private MyVector<MyVector<Object>> dataSource;
    /**
     * 确定回调
     */
    private Function<MyVector<MyVector<Object>>, Boolean> doOKAction;

    public String getDialogTitle() {
        return dialogTitle;
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    public MyVector<String> getHeaders() {
        return headers;
    }

    public void setHeaders(MyVector<String> headers) {
        this.headers = headers;
    }

    public MyVector<MyVector<Object>> getDataSource() {
        return dataSource;
    }

    public void setDataSource(MyVector<MyVector<Object>> dataSource) {
        this.dataSource = dataSource;
    }

    public Function<MyVector<MyVector<Object>>, Boolean> getDoOKAction() {
        return doOKAction;
    }

    public void setDoOKAction(Function<MyVector<MyVector<Object>>, Boolean> doOKAction) {
        this.doOKAction = doOKAction;
    }
}
