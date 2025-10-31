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
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);

        try {
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new RpcTestConsumerInitializer());

            String host = "117.73.33.162";
            int port = 27880;

            bootstrap.connect(host, port).sync();

            Thread.sleep(5000);

        } catch (InterruptedException e) {
            LOGGER.error("Failed to connect to RPC server: {}", e.getMessage());
            throw e;
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}