package com.chang.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RpcReference {

    String serviceVersion() default "1.0";

    /**
     * 超时时间
     */
    long timeout() default 5000;


    /**
     * 可选的负载均衡
     */
    String loadBalancer();

    /**
     * 可选的容错策略
     */
    String faultTolerant();


    /**
     * 重试次数
     */
    long retryCount() default 3;
}
