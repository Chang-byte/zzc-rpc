package com.chang.rpc.filter;

import com.chang.rpc.config.RpcProperties;

import java.util.Map;

/**
 * @author Chang
 * @version 1.0
 * @title ServiceTokenFilter
 * @description token 拦截器
 */
public class ServiceTokenFilter implements ServiceBeforeFilter {

    /**
     *
     * @param filterData
     */
    @Override
    public void doFilter(FilterData filterData) {
        // TODO 从数据库中查询
        final Map<String, Object> attachments = filterData.getClientAttachments();
        final Map<String, Object> serviceAttachments = RpcProperties.getInstance().getServiceAttachments();
        if (!attachments.getOrDefault("token","").equals(serviceAttachments.getOrDefault("token",""))){
            throw new IllegalArgumentException("token不正确");
        }
    }

}