package com.rain.rpc.test.codec.init;

import com.rain.rpc.codec.RpcDecoder;
import com.rain.rpc.codec.RpcEncoder;
import com.rain.rpc.test.codec.handler.RpcTestConsumerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RPC Test Consumer Initializer
 * Initializes the channel pipeline with necessary handlers for codec and message processing
 */
public class RpcTestConsumerInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcTestConsumerInitializer.class);

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        LOGGER.info("Initializing channel pipeline for consumer connection");
        ChannelPipeline pipeline = channel.pipeline();

        // Add RPC decoder to deserialize incoming messages
        pipeline.addLast(new RpcDecoder())
                // Add RPC encoder to serialize outgoing messages
                .addLast(new RpcEncoder())
                // Add consumer handler to process business logic
                .addLast(new RpcTestConsumerHandler());

        LOGGER.info("Channel pipeline initialized successfully with RpcDecoder, RpcEncoder and RpcTestConsumerHandler");
    }
}