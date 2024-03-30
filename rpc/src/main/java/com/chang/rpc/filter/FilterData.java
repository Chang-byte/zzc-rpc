package com.chang.rpc.filter;

import com.chang.rpc.common.RpcRequest;
import com.chang.rpc.common.RpcResponse;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Chang
 * @version 1.0
 * @title FilterData
 * @description 上下文数据
 */
public class FilterData {

    private String serviceVersion;
    private long timeout;
    private long retryCount;
    private String className;
    private String methodName;
    private Object args;

    // 服务提供方的附加信息
    private Map<String, Object> serviceAttachments;

    // 服务消费方的附加信息
    private Map<String, Object> clientAttachments;

    // 执行业务逻辑后的数据
    private RpcResponse data;

    public FilterData(RpcRequest request) {
        this.args = request.getData();
        this.className = request.getClassName();
        this.methodName = request.getMethodName();
        this.serviceVersion = request.getServiceVersion();
        this.serviceAttachments = request.getServiceAttachments();
        this.clientAttachments = request.getClientAttachments();
    }

    public FilterData() {

    }

    public RpcResponse getData() {
        return data;
    }

    public void setData(RpcResponse data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "调用: Class: " + className + " Method: " + methodName + " args: " + args +" Version: " + serviceVersion
                +" Timeout: " + timeout +" ServiceAttachments: " + serviceAttachments +
                " ClientAttachments: " + clientAttachments;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(long retryCount) {
        this.retryCount = retryCount;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Map<String, Object> getServiceAttachments() {
        return serviceAttachments;
    }

    public void setServiceAttachments(Map<String, Object> serviceAttachments) {
        this.serviceAttachments = serviceAttachments;
    }

    public Map<String, Object> getClientAttachments() {
        return clientAttachments;
    }

    public void setClientAttachments(Map<String, Object> clientAttachments) {
        this.clientAttachments = clientAttachments;
    }
}
