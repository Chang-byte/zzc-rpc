package com.chang.rpc.consumer;

import com.chang.rpc.annotation.RpcReference;
import com.chang.rpc.config.RpcProperties;
import com.chang.rpc.filter.ClientLogFilter;
import com.chang.rpc.utils.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * @author Chang
 * @version 1.0
 * @title ConsumerPostProcessor
 * @description 消费方后置处理器
 */
@Configuration
public class ConsumerPostProcessor implements BeanPostProcessor, EnvironmentAware, InitializingBean {

    private Logger logger = LoggerFactory.getLogger(ClientLogFilter.class);

    RpcProperties rpcProperties;


    /**
     * 从配置文件中读取数据
     *
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {
        RpcProperties properties = RpcProperties.getInstance();
        PropertiesUtils.init(properties, environment);
    }


    @Override
    public void afterPropertiesSet() throws Exception {

    }


    /**
     * 代理层注入
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 获取所有的字段
        final Field[] fields = bean.getClass().getDeclaredFields();
        // 遍历所有的字段，找到 @RpcReference 注解的字段
        for (Field field : fields) {
            if (field.isAnnotationPresent(RpcReference.class)) {
                final RpcReference rpcReference = field.getAnnotation(RpcReference.class);
                final Class<?> aClass = field.getType();
                field.setAccessible(true);
                Object object = null;

                // 创建代理对象
                try {
                    object = Proxy.newProxyInstance(
                            aClass.getClassLoader(),
                            new Class<?>[]{aClass},
                            new RpcInvokerProxy(rpcReference.serviceVersion(),
                                    rpcReference.timeout(),
                                    rpcReference.faultTolerant(),
                                    rpcReference.loadBalancer(),
                                    rpcReference.retryCount())
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 将代理对象设置给字段
                try {
                    field.set(bean, object);
                    field.setAccessible(false);
                    logger.info(beanName + " field:" + field.getName() + "注入成功");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    logger.info(beanName + " field:" + field.getName() + "注入失败");
                }

            }

        }
        return bean;
    }
}
