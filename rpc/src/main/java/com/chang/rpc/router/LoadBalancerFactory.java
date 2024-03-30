package com.chang.rpc.router;

import com.chang.rpc.spi.ExtensionLoader;

/**
 * @author Chang
 * @version 1.0
 * @title LoadBalancerFactory
 * @description 负载均衡工厂
 */
public class LoadBalancerFactory {

    public static LoadBalancer get(String serviceLoadBalancer) throws Exception {

        return ExtensionLoader.getInstance().get(serviceLoadBalancer);

    }

    public static void init() throws Exception {
        ExtensionLoader.getInstance().loadExtension(LoadBalancer.class);
    }
}
