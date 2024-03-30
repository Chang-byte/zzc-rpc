package com.chang.rpc.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chang
 * @version 1.0
 * @title ClientLogFilter
 * @description 日志过滤器
 */
public class ClientLogFilter implements ClientBeforeFilter {

    private Logger logger = LoggerFactory.getLogger(ClientLogFilter.class);


    @Override
    public void doFilter(FilterData filterData) {
        logger.info(filterData.toString());
    }
}
