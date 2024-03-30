package com.chang.rpc.protocol.serialization;

import com.chang.rpc.spi.ExtensionLoader;

/**
 * @author Chang
 * @version 1.0
 * @title SerializationFactory
 * @description 序列化工厂
 */
public class SerializationFactory {
    public static RpcSerialization get(String serialization) throws Exception {

        return ExtensionLoader.getInstance().get(serialization);

    }

    public static void init() throws Exception {
        ExtensionLoader.getInstance().loadExtension(RpcSerialization.class);
    }
}
