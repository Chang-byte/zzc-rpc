package com.chang.rpc.constants;


/**
 * @author Chang
 * @version 1.0
 * @title FaultTolerantRules
 * @description 容错策略
 */
public interface FaultTolerantRules {

    String Failover = "failover";

    String FailFast = "failFast";

    String Failsafe = "failsafe";
}