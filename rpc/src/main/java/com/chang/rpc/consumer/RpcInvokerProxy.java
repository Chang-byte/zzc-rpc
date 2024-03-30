package com.chang.rpc.consumer;

import com.chang.rpc.common.*;
import com.chang.rpc.config.RpcProperties;
import com.chang.rpc.constants.MsgType;
import com.chang.rpc.constants.ProtocolConstants;
import com.chang.rpc.protocol.MsgHeader;
import com.chang.rpc.protocol.RpcProtocol;
import com.chang.rpc.router.LoadBalancer;
import com.chang.rpc.router.LoadBalancerFactory;
import com.chang.rpc.router.ServiceMetaRes;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @author Chang
 * @version 1.0
 * @title RpcInvokerProxy
 * @description 代理类
 */
public class RpcInvokerProxy implements InvocationHandler {


    // 服务版本号
    private String serviceVersion;

    // 超时重试时间
    private long timeout;

    // 负载均衡策略
    private String loadBalancerType;

    // 默认熔断机制
    private String faultTolerantType;

    // 重试次数
    private long retryCount;

    private static final Logger logger = LoggerFactory.getLogger(RpcInvokerProxy.class);

    public RpcInvokerProxy(String serviceVersion, long timeout, String loadBalancerType, String faultTolerantType, long retryCount) {
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.loadBalancerType = loadBalancerType;
        this.faultTolerantType = faultTolerantType;
        this.retryCount = retryCount;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 1.构建消息协议(消息头和请求体)
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();

        MsgHeader header = new MsgHeader();
        long requestId = RpcRequestHolder.REQUEST_ID_GEN.incrementAndGet();
        header.setMagic(ProtocolConstants.MAGIC);
        header.setVersion(ProtocolConstants.VERSION);
        header.setRequestId(requestId);
        final byte[] serialization = RpcProperties.getInstance().getSerialization().getBytes();
        header.setSerializationLen(serialization.length);
        header.setSerializations(serialization);
        header.setMsgType((byte) MsgType.REQUEST.ordinal());
        header.setStatus((byte) 0x1);
        protocol.setHeader(header);

        RpcRequest request = new RpcRequest();
        request.setServiceVersion(this.serviceVersion);
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setData(ObjectUtils.isEmpty(args) ? new Object[0] : args);
        request.setDataClass(ObjectUtils.isEmpty(args) ? null : args[0].getClass());
        request.setServiceAttachments(RpcProperties.getInstance().getServiceAttachments());
        request.setClientAttachments(RpcProperties.getInstance().getClientAttachments());

        // TODO 拦截器的实现

        protocol.setBody(request);

        // 2.创建消费者
        RpcConsumer rpcConsumer = new RpcConsumer();
        // 获取服务中心的名称 通过请求的类名和服务的版本号来确定服务的名称。
        String serviceName = RpcServiceNameBuilder.buildServiceKey(request.getClassName(), request.getServiceVersion());
        // 获取请求数据
        Object[] params = {request.getData()};
        // 负载均衡策略
        final LoadBalancer loadBalancer = LoadBalancerFactory.get(loadBalancerType);

        // 根据负载均衡策略获取对应的服务
        final ServiceMetaRes serviceMetaRes = loadBalancer.select(params, serviceName);

        ServiceMeta curServiceMeta = serviceMetaRes.getCurServiceMeta();
        final Collection<ServiceMeta> otherServiceMeta = serviceMetaRes.getOtherServiceMeta();
        // 重试机制
        long count = 1;
        long retryCount = this.retryCount;
        RpcResponse rpcResponse = null;
        while (count <= retryCount) {
            // 处理返回数据
            RpcFuture<RpcResponse> future = new RpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()), timeout);
            // XXXHolder 本地缓存起来
            RpcRequestHolder.REQUEST_MAP.put(requestId, future);

            try {
                // 发送消息
                rpcConsumer.sendRequest(protocol, curServiceMeta);
                // 等待响应数据的返回
                rpcResponse = future.getPromise().get(future.getTimeout(), TimeUnit.MILLISECONDS);
                // 如果有异常并且没有其他服务
                if (rpcResponse.getException() != null && otherServiceMeta.size() == 0) {
                    throw rpcResponse.getException();
                }
                if (rpcResponse.getException() != null) {
                    throw rpcResponse.getException();
                }
                logger.info("rpc 调用成功, serviceName: {}", serviceName);
                // 返回调用结果
                return rpcResponse.getData();
            } catch (Throwable e) {
                // 重试处理，要根据策略来进行调整

            }

        }
        throw new RuntimeException("rpc 调用失败");
    }
}
