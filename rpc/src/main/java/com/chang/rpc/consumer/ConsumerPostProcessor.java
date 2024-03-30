package com.chang.rpc.consumer;

import com.chang.rpc.config.RpcProperties;
import com.chang.rpc.filter.ClientLogFilter;
import com.chang.rpc.utils.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author Chang
 * @version 1.0
 * @title ConsumerPostProcessor
 * @description 消费方后置处理器
 */
@Configuration
public class ConsumerPostProcessor implements EnvironmentAware {

    private Logger logger = LoggerFactory.getLogger(ClientLogFilter.class);

    RpcProperties rpcProperties;


    /**
     * 从配置文件中读取数据
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {
        RpcProperties properties = RpcProperties.getInstance();
        PropertiesUtils.init(properties,environment);
    }
}
