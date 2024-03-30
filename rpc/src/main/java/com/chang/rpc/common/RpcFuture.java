package com.chang.rpc.common;

import io.netty.util.concurrent.Promise;

/**
 * @author Chang
 * @version 1.0
 * @title RpcFuture
 * @description 返回结果类
 */
public class RpcFuture<T> {

    // 异步结果
    private Promise<T> promise;
    private long timeout;

    public Promise<T> getPromise() {
        return promise;
    }

    public void setPromise(Promise<T> promise) {
        this.promise = promise;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public RpcFuture() {
    }

    public RpcFuture(Promise<T> promise, long timeout) {
        this.promise = promise;
        this.timeout = timeout;
    }
}
