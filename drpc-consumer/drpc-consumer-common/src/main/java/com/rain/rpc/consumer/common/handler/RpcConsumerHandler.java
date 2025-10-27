package com.rain.rpc.consumer.common.handler;

import com.alibaba.fastjson2.JSON;
import com.rain.rpc.protocol.RpcProtocol;
import com.rain.rpc.protocol.request.RpcRequest;
import com.rain.rpc.protocol.response.RpcResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

/**
 * RPC Consumer Handler
 * Handles the communication logic for the RPC consumer
 * Sends RPC requests and processes RPC responses
 */
public class RpcConsumerHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcConsumerHandler.class);
    private volatile Channel channel;
    private SocketAddress remotePeer;

    public Channel getChannel() {
        return channel;
    }

    public SocketAddress getRemotePeer() {
        return remotePeer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.remotePeer = this.channel.remoteAddress();
        LOGGER.info("Consumer channel is now active, remote peer: {}", remotePeer);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
        LOGGER.debug("Channel registered with handler, channel id: {}", channel.id());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcResponse> protocol) throws Exception {
        LOGGER.info("Received response from service provider, request id: {}, response: {}", 
            protocol.getHeader().getRequestId(), JSON.toJSONString(protocol));
    }

    /**
     * Sends an RPC request to the service provider
     *
     * @param protocol the RPC protocol containing the request data
     */
    public void sendRequest(RpcProtocol<RpcRequest> protocol) {
        LOGGER.info("Sending request to service provider, request id: {}, request: {}", 
            protocol.getHeader().getRequestId(), JSON.toJSONString(protocol));
        this.channel.writeAndFlush(protocol);
    }

    public void close() {
        LOGGER.info("Closing consumer channel connection");
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
}