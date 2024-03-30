package com.chang.rpc.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProviderRpc
public class RpcProviderDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcProviderDemoApplication.class, args);
    }
}