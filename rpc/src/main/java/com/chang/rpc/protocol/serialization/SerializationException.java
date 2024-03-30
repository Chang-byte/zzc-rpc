package com.chang.rpc.protocol.serialization;

/**
 * @author Chang
 * @version 1.0
 * @title SerializationException
 * @description 序列化异常
 */
public class SerializationException extends RuntimeException{

    private static final long serialVersionUID = -1;

    public SerializationException() {
        super();
    }

    public SerializationException(String msg) {
        super(msg);
    }

    public SerializationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SerializationException(Throwable cause) {
        super(cause);
    }
}
