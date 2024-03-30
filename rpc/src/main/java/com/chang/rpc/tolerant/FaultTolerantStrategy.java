package com.chang.rpc.tolerant;


/**
 * 容错策略
 */
public interface FaultTolerantStrategy {

    void handler();

}