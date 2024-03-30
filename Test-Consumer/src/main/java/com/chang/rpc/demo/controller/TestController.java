package com.chang.rpc.demo.controller;

import com.chang.rpc.annotation.RpcReference;
import com.chang.rpc.constants.FaultTolerantRules;
import com.chang.rpc.constants.LoadBalancerRules;
import com.chang.rpc.demo.Test2Service;
import com.chang.rpc.demo.TestService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Chang
 * @version 1.0
 * @title TestController
 * @description 测试
 */
@RestController
public class TestController {
    @RpcReference(timeout = 10000L,faultTolerant = FaultTolerantRules.Failover,loadBalancer = LoadBalancerRules.RoundRobin)
    TestService testService;

    @RpcReference(loadBalancer = LoadBalancerRules.ConsistentHash)
    Test2Service test2Service;

    /**
     * 轮询
     * 会触发故障转移,提供方模拟异常
     * @param key
     * @return
     */
    @RequestMapping("test/{key}")
    public String test(@PathVariable String key){
        testService.test(key);
        return "test1 ok";
    }

    /**
     * 一致性哈希
     * @param key
     * @return
     */
    @RequestMapping("test2/{key}")
    public String test2(@PathVariable String key){

        return test2Service.test(key);
    }

    /**
     * 轮询,无如何异常
     * @param key
     * @return
     */
    @RequestMapping("test3/{key}")
    public String test3(@PathVariable String key){
        testService.test2(key);
        return "test2 ok";
    }
}
