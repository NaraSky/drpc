package com.rain.rpc.test.codec.handler;

import com.alibaba.fastjson2.JSONObject;
import com.rain.rpc.protocol.RpcProtocol;
import com.rain.rpc.protocol.header.RpcHeaderFactory;
import com.rain.rpc.protocol.request.RpcRequest;
import com.rain.rpc.protocol.response.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RPC Test Consumer Handler
 * Handles the communication logic for the RPC test consumer
 * Sends RPC requests and processes RPC responses
 */
public class RpcTestConsumerHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {

    private final Logger LOGGER = LoggerFactory.getLogger(RpcTestConsumerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("Connected to RPC server, sending test request");

        // Create and send test RPC request
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<RpcRequest>();
        protocol.setHeader(RpcHeaderFactory.getRequestHeader("jdk"));

        RpcRequest request = new RpcRequest();
        request.setClassName("com.rain.rpc.test.api.DemoService");
        request.setGroup("default");
        request.setMethodName("hello");
        request.setParameters(new Object[]{"rain"});
        request.setParameterTypes(new Class[]{String.class});
        request.setVersion("1.0.0");
        request.setAsync(false);
        request.setOneway(false);

        protocol.setBody(request);
        LOGGER.info("Sending request: {}", JSONObject.toJSONString(protocol));

        // Send the request
        ctx.writeAndFlush(protocol);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcResponse> protocol) throws Exception {
        LOGGER.info("Received response: {}", JSONObject.toJSONString(protocol));

        // Process the response
        RpcResponse response = protocol.getBody();
        if (response != null) {
            if (response.getError() != null) {
                LOGGER.error("RPC call failed: {}", response.getError());
            } else {
                LOGGER.info("RPC call successful, result: {}", response.getResult());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("Exception in consumer handler: {}", cause.getMessage());
        ctx.close();
    }
}