package com.chang.rpc.demo;

import com.chang.rpc.annotation.EnableConsumerRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Chang
 * @version 1.0
 * @title RpcConsumerDemoApplication
 * @description Demo启动类
 */
@SpringBootApplication
@EnableConsumerRpc
public class RpcConsumerDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcConsumerDemoApplication.class, args);
    }


}
