package com.chang.rpc.filter;

import com.chang.rpc.spi.ExtensionLoader;
import lombok.SneakyThrows;

import java.io.IOException;

/**
 * @author Chang
 * @version 1.0
 * @title FilterConfig
 * @description 拦截器的配置类， 用于统一管理拦截器
 */
public class FilterConfig {

    private static FilterChain serviceBeforeFilterChain = new FilterChain();
    private static FilterChain serviceAfterFilterChain = new FilterChain();
    private static FilterChain clientBeforeFilterChain = new FilterChain();
    private static FilterChain clientAfterFilterChain = new FilterChain();

    @SneakyThrows
    public static void initServiceFilter(){
        final ExtensionLoader extensionLoader = ExtensionLoader.getInstance();
        extensionLoader.loadExtension(ServiceAfterFilter.class);
        extensionLoader.loadExtension(ServiceBeforeFilter.class);
        serviceBeforeFilterChain.addFilter(extensionLoader.gets(ServiceBeforeFilter.class));
        serviceAfterFilterChain.addFilter(extensionLoader.gets(ServiceAfterFilter.class));
    }
    public static void initClientFilter() throws IOException, ClassNotFoundException {
        final ExtensionLoader extensionLoader = ExtensionLoader.getInstance();
        extensionLoader.loadExtension(ClientAfterFilter.class);
        extensionLoader.loadExtension(ClientBeforeFilter.class);
        clientBeforeFilterChain.addFilter(extensionLoader.gets(ClientBeforeFilter.class));
        clientAfterFilterChain.addFilter(extensionLoader.gets(ClientAfterFilter.class));
    }

    public static FilterChain getServiceBeforeFilterChain(){
        return serviceBeforeFilterChain;
    }
    public static FilterChain getServiceAfterFilterChain(){
        return serviceAfterFilterChain;
    }
    public static FilterChain getClientBeforeFilterChain(){
        return clientBeforeFilterChain;
    }
    public static FilterChain getClientAfterFilterChain(){
        return clientAfterFilterChain;
    }
}
