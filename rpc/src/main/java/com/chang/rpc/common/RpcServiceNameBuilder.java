package com.chang.rpc.common;

/**
 * @author Chang
 * @version 1.0
 * @title RpcServiceNameBuilder
 * @description 构建key
 */
public class RpcServiceNameBuilder {

    // key: 服务名 value: 服务提供方s
    public static String buildServiceKey(String serviceName, String serviceVersion) {
        return String.join("$", serviceName, serviceVersion);
    }
}
