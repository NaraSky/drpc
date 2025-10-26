package com.rain.rpc.provider.common.handler;

import com.alibaba.fastjson2.JSON;
import com.rain.rpc.protocol.RpcProtocol;
import com.rain.rpc.protocol.enumeration.RpcType;
import com.rain.rpc.protocol.header.RpcHeader;
import com.rain.rpc.protocol.request.RpcRequest;
import com.rain.rpc.protocol.response.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Handler for processing incoming RPC requests on the provider side.
 * This handler receives messages from clients and processes them according to the registered services.
 */
public class RpcProviderHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProviderHandler.class);

    private final Map<String, Object> handlerMap;

    /**
     * Constructs an RpcProviderHandler with the specified handler map.
     *
     * @param handlerMap the map containing service instances for processing requests
     */
    public RpcProviderHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcRequest> rpcProtocol) throws Exception {
        LOGGER.info("Received RPC request: {}", JSON.toJSONString(rpcProtocol));
        LOGGER.info("Data stored in handlerMap is as follows:");
        for (Map.Entry<String, Object> entry : handlerMap.entrySet()) {
            LOGGER.info("RpcProviderHandler: {}, {}", entry.getKey(), entry.getValue());
        }
        RpcProtocol<RpcResponse> responseRpcProtocol = getRpcResponseRpcProtocol(rpcProtocol);
        channelHandlerContext.writeAndFlush(responseRpcProtocol);
    }

    private static RpcProtocol<RpcResponse> getRpcResponseRpcProtocol(RpcProtocol<RpcRequest> rpcProtocol) {
        RpcHeader header = rpcProtocol.getHeader();
        RpcRequest request = rpcProtocol.getBody();
        header.setMessageType((byte) RpcType.RESPONSE.getType());
        RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();
        RpcResponse response = new RpcResponse();
        response.setResult("数据交互成功");
        response.setAsync(request.isAsync());
        response.setOneway(request.isOneway());
        responseRpcProtocol.setHeader(header);
        responseRpcProtocol.setBody(response);
        return responseRpcProtocol;
    }
}
