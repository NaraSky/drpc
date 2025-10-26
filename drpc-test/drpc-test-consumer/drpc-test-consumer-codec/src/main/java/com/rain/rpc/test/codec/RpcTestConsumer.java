package com.rain.rpc.test.codec;

import com.rain.rpc.test.codec.init.RpcTestConsumerInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RPC Test Consumer
 * Main class to start the RPC test consumer that connects to the RPC server
 * and sends test requests
 */
public class RpcTestConsumer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcTestConsumer.class);

    public static void main(String[] args) throws InterruptedException {
        LOGGER.info("Starting RPC test consumer application");
        
        // Create Netty bootstrap and event loop group
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
        LOGGER.info("Created Bootstrap and EventLoopGroup with 4 threads");

        try {
            // Configure the bootstrap
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new RpcTestConsumerInitializer());
            LOGGER.info("Bootstrap configured with NioSocketChannel and RpcTestConsumerInitializer");
            
            // Connect to the server
            String host = "127.0.0.1";
            int port = 27880;
            LOGGER.info("Attempting to connect to RPC server at {}:{}", host, port);
            
            bootstrap.connect(host, port).sync();
            LOGGER.info("Successfully connected to RPC server at {}:{}", host, port);
            
            // Keep the connection alive
            LOGGER.info("Consumer is now running and waiting for responses");
            
        } catch (InterruptedException e) {
            LOGGER.error("Failed to connect to RPC server: {}", e.getMessage(), e);
            throw e;
        } finally {
            LOGGER.info("Shutting down consumer in 2 seconds");
            Thread.sleep(2000);
            eventLoopGroup.shutdownGracefully();
            LOGGER.info("Consumer shut down gracefully");
        }
    }
}