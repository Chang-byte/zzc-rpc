package com.chang.rpc.constants;

/**
 * @author Chang
 * @version 1.0
 * @title LoadBalancerRules
 * @description 负载均衡策略
 */
public interface LoadBalancerRules {
    /**
     * 一致性hash
     */
    String ConsistentHash = "consistentHash";
    String RoundRobin = "roundRobin";
}

