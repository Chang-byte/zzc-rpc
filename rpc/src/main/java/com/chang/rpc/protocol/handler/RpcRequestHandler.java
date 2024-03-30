package com.chang.rpc.protocol.handler;

import com.chang.rpc.common.RpcRequest;
import com.chang.rpc.pool.ThreadPoolFactory;
import com.chang.rpc.protocol.RpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Chang
 * @version 1.0
 * @title RpcRequestHandler
 * @description 处理消费方发送数据并且调用方法
 */
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {


    public RpcRequestHandler() {}

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) {
        ThreadPoolFactory.submitRequest(ctx,protocol);
    }

}
