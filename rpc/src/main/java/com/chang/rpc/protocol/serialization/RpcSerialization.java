package com.chang.rpc.protocol.serialization;

import java.io.IOException;

/**
 * @author Chang
 * @version 1.0
 * @title RpcSerialization
 * @description 序列化接口
 */
public interface RpcSerialization {

    <T> byte[] serialize(T obj) throws IOException;

    <T> T deserialize(byte[] data, Class<T> clz) throws IOException;
}

