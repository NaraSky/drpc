package com.rain.rpc.consumer.common.initializer;

import com.rain.rpc.codec.RpcDecoder;
import com.rain.rpc.codec.RpcEncoder;
import com.rain.rpc.consumer.common.handler.RpcConsumerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcConsumerInitializer extends ChannelInitializer<SocketChannel> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcConsumerInitializer.class);

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        LOGGER.debug("Initializing channel pipeline for consumer connection");
        ChannelPipeline cp = channel.pipeline();
        // Add RPC encoder to serialize outgoing messages
        cp.addLast(new RpcEncoder());
        // Add RPC decoder to deserialize incoming messages
        cp.addLast(new RpcDecoder());
        // Add consumer handler to process business logic
        cp.addLast(new RpcConsumerHandler());
        LOGGER.debug("Channel pipeline initialized successfully with RpcEncoder, RpcDecoder and RpcConsumerHandler");
    }
}