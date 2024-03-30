package com.chang.rpc.demo.filter;

import com.chang.rpc.filter.ClientAfterFilter;
import com.chang.rpc.filter.FilterData;

/**
 * @author Chang
 * @version 1.0
 * @title AfterFilter
 * @description 后置过滤器
 */
public class AfterFilter implements ClientAfterFilter {
    @Override
    public void doFilter(FilterData filterData) {
        System.out.println("客户端后置处理器启动咯");
    }
}
