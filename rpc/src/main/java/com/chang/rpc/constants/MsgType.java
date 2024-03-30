package com.chang.rpc.constants;


/**
 * 消息类型
 */
public enum MsgType {

    REQUEST,
    RESPONSE,
    HEARTBEAT;


    public static MsgType findByType(int type){
        return MsgType.values()[type];
    }
}
