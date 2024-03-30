package com.chang.rpc.demo.service;

import com.chang.rpc.demo.Test2Service;

/**
 * @author Chang
 * @version 1.0
 * @title TestServiceImpl2
 * @description 测试接口实现类2
 */
public class TestServiceImpl2 implements Test2Service {
    @Override
    public String test(String key) {
        System.out.println("服务提供1 test2 测试成功 :" + key);
        return key;
    }
}
