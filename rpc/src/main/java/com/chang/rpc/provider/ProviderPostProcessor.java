package com.chang.rpc.provider;

import com.chang.rpc.annotation.RpcService;
import com.chang.rpc.common.RpcServiceNameBuilder;
import com.chang.rpc.common.ServiceMeta;
import com.chang.rpc.config.RpcProperties;
import com.chang.rpc.filter.ClientLogFilter;
import com.chang.rpc.filter.FilterConfig;
import com.chang.rpc.pool.ThreadPoolFactory;
import com.chang.rpc.protocol.codec.RpcDecoder;
import com.chang.rpc.protocol.codec.RpcEncoder;
import com.chang.rpc.protocol.handler.RpcRequestHandler;
import com.chang.rpc.protocol.handler.ServiceAfterFilterHandler;
import com.chang.rpc.protocol.handler.ServiceBeforeFilterHandler;
import com.chang.rpc.protocol.serialization.SerializationFactory;
import com.chang.rpc.registry.RegistryFactory;
import com.chang.rpc.registry.RegistryService;
import com.chang.rpc.router.LoadBalancerFactory;
import com.chang.rpc.utils.PropertiesUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Chang
 * @version 1.0
 * @title ProviderPostProcessor
 * @description 服务提供方后置处理器
 */
public class ProviderPostProcessor implements InitializingBean, BeanPostProcessor, EnvironmentAware {


    private Logger logger = LoggerFactory.getLogger(ClientLogFilter.class);


    RpcProperties rpcProperties;

    // 此处在linux环境下改为0.0.0.0
    private static String serverAddress = "127.0.0.1";

    private final Map<String, Object> rpcServiceMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        // 开一个线程，启动Netty服务器
        Thread t = new Thread(() -> {
            try {
                startRpcServer();
            } catch (Exception e) {
                logger.error("start rpc server error.", e);
            }
        });
        t.setDaemon(true);
        t.start();
        SerializationFactory.init();
        RegistryFactory.init();
        LoadBalancerFactory.init();
        FilterConfig.initServiceFilter();
        ThreadPoolFactory.setRpcServiceMap(rpcServiceMap);
    }


    /**
     * 服务注册， 将带有 RpcService 注解的类注册到注册中心
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        // 找到bean上带有 RpcService 注解的类
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        if (rpcService != null) {
            // 可能会有多个接口,默认选择第一个接口
            String serviceName = beanClass.getInterfaces()[0].getName();
            if (!rpcService.serviceInterface().equals(void.class)){
                serviceName = rpcService.serviceInterface().getName();
            }
            String serviceVersion = rpcService.serviceVersion();
            try {
                // 服务注册
                int servicePort = rpcProperties.getPort();
                // 获取注册中心 ioc
                RegistryService registryService = RegistryFactory.get(rpcProperties.getRegisterType());
                ServiceMeta serviceMeta = new ServiceMeta();
                // 服务提供方地址
                serviceMeta.setServiceAddr("127.0.0.1");
                serviceMeta.setServicePort(servicePort);
                serviceMeta.setServiceVersion(serviceVersion);
                serviceMeta.setServiceName(serviceName);
                registryService.register(serviceMeta);
                // 缓存
                rpcServiceMap.put(RpcServiceNameBuilder.buildServiceKey(serviceMeta.getServiceName(),serviceMeta.getServiceVersion()), bean);
                logger.info("register server {} version {}",serviceName,serviceVersion);
            } catch (Exception e) {
                logger.error("failed to register service {}",  serviceVersion, e);
            }
        }
        return bean;
    }

    private void startRpcServer() throws InterruptedException {
        int serverPort = rpcProperties.getPort();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new RpcEncoder())
                                    .addLast(new RpcDecoder())
                                    .addLast(new ServiceBeforeFilterHandler())
                                    .addLast(new RpcRequestHandler())
                                    .addLast(new ServiceAfterFilterHandler());
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = bootstrap.bind(serverAddress, serverPort).sync();
            logger.info("server addr {} started on port {}", serverAddress, serverPort);
            channelFuture.channel().closeFuture().sync();
            Runtime.getRuntime().addShutdownHook(new Thread(() ->
            {
                logger.info("ShutdownHook execute start...");
                logger.info("Netty NioEventLoopGroup shutdownGracefully...");
                logger.info("Netty NioEventLoopGroup shutdownGracefully2...");
                boss.shutdownGracefully();
                worker.shutdownGracefully();
                logger.info("ShutdownHook execute end...");
            }, "Allen-thread"));
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }


    /**
     * 读取配置文件
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {
        RpcProperties properties = RpcProperties.getInstance();
        PropertiesUtils.init(properties,environment);
        rpcProperties = properties;
        logger.info("读取配置文件成功");
    }
}
