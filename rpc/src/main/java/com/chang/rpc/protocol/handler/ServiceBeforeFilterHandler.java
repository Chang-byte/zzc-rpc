package com.chang.rpc.protocol.handler;

import com.chang.rpc.common.RpcRequest;
import com.chang.rpc.common.RpcResponse;
import com.chang.rpc.constants.MsgStatus;
import com.chang.rpc.filter.FilterConfig;
import com.chang.rpc.filter.FilterData;
import com.chang.rpc.protocol.MsgHeader;
import com.chang.rpc.protocol.RpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 前置拦截器
 */
public class ServiceBeforeFilterHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

    private Logger logger = LoggerFactory.getLogger(ServiceBeforeFilterHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) {
        final RpcRequest request = protocol.getBody();
        final FilterData filterData = new FilterData(request);
        RpcResponse response = new RpcResponse();
        MsgHeader header = protocol.getHeader();

        try {
            FilterConfig.getServiceBeforeFilterChain().doFilter(filterData);

        } catch (Exception e) {
            RpcProtocol<RpcResponse> resProtocol = new RpcProtocol<>();
            header.setStatus((byte) MsgStatus.FAILED.ordinal());
            response.setException(e);
            logger.error("before process request {} error", header.getRequestId(), e);
            resProtocol.setHeader(header);
            resProtocol.setBody(response);
            ctx.writeAndFlush(resProtocol);
            return;
        }
        ctx.fireChannelRead(protocol);
    }
}