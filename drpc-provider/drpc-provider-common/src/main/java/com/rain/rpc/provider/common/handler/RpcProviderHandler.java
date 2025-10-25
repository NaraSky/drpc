package com.rain.rpc.provider.common.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Handler for processing incoming RPC requests on the provider side.
 * This handler receives messages from clients and processes them according to the registered services.
 */
public class RpcProviderHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProviderHandler.class);

    private final Map<String, Object> handlerMap;

    /**
     * Constructs an RpcProviderHandler with the specified handler map.
     * 
     * @param handlerMap the map containing service instances for processing requests
     */
    public RpcProviderHandler(Map<String, Object> handlerMap){
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        LOGGER.info("Data received by RPC provider ====>>> {}", o.toString());
        LOGGER.info("Data stored in handlerMap is as follows:");
        for (Map.Entry<String, Object> entry : handlerMap.entrySet()) {
            LOGGER.info("RpcProviderHandler: {}, {}", entry.getKey(), entry.getValue());
        }
        channelHandlerContext.writeAndFlush(o);
    }
}
