package com.chang.rpc.protocol.handler;

import com.chang.rpc.common.RpcFuture;
import com.chang.rpc.common.RpcRequestHolder;
import com.chang.rpc.common.RpcResponse;
import com.chang.rpc.protocol.RpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Chang
 * @version 1.0
 * @title RpcResponseHandler
 * @description 响应处理器
 */
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcResponse> msg) {
        long requestId = msg.getHeader().getRequestId();
        RpcFuture<RpcResponse> future = RpcRequestHolder.REQUEST_MAP.remove(requestId);
        future.getPromise().setSuccess(msg.getBody());
    }

}
