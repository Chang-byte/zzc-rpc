package com.chang.rpc.provider;

import com.chang.rpc.filter.ClientLogFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * @author Chang
 * @version 1.0
 * @title ProviderPostProcessor
 * @description 服务提供方后置处理器
 */
public class ProviderPostProcessor implements InitializingBean, BeanPostProcessor, EnvironmentAware {


    private Logger logger = LoggerFactory.getLogger(ClientLogFilter.class);

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void setEnvironment(Environment environment) {

    }
}
