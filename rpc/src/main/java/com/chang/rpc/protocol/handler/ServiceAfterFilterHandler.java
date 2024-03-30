package com.chang.rpc.protocol.handler;

import com.chang.rpc.common.RpcResponse;
import com.chang.rpc.constants.MsgStatus;
import com.chang.rpc.filter.ClientLogFilter;
import com.chang.rpc.filter.FilterConfig;
import com.chang.rpc.filter.FilterData;
import com.chang.rpc.protocol.MsgHeader;
import com.chang.rpc.protocol.RpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 后置拦截器
 */
public class ServiceAfterFilterHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {

    private Logger logger = LoggerFactory.getLogger(ClientLogFilter.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcResponse> protocol) {
        final FilterData filterData = new FilterData();
        filterData.setData(protocol.getBody());
        RpcResponse response = new RpcResponse();
        MsgHeader header = protocol.getHeader();
        try {
            FilterConfig.getServiceAfterFilterChain().doFilter(filterData);
        } catch (Exception e) {
            header.setStatus((byte) MsgStatus.FAILED.ordinal());
            response.setException(e);
            logger.error("after process request {} error", header.getRequestId(), e);
        }
        ctx.writeAndFlush(protocol);
    }
}