package com.chang.rpc.common;

import java.io.Serializable;

/**
 * @author Chang
 * @version 1.0
 * @title RpcResponse
 * @description 响应体
 */
public class RpcResponse implements Serializable {

    private Object data;
    private Class dataClass;
    private String message;
    private Exception exception;

    public Class getDataClass() {
        return dataClass;
    }

    public void setDataClass(Class dataClass) {
        this.dataClass = dataClass;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
