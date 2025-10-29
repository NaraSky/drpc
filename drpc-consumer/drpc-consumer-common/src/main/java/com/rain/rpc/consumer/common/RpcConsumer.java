package com.rain.rpc.consumer.common;

import com.rain.rpc.consumer.common.future.RpcFuture;
import com.rain.rpc.consumer.common.handler.RpcConsumerHandler;
import com.rain.rpc.consumer.common.initializer.RpcConsumerInitializer;
import com.rain.rpc.protocol.RpcProtocol;
import com.rain.rpc.protocol.request.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages RPC consumer connections and sends requests to RPC providers.
 */
public class RpcConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcConsumer.class);
    private static volatile RpcConsumer instance;
    private static Map<String, RpcConsumerHandler> handlerMap = new ConcurrentHashMap<>();
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    // Constructs an RpcConsumer with a configured Netty bootstrap and event loop group
    private RpcConsumer() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new RpcConsumerInitializer());
        LOGGER.info("RPC Consumer initialized with 4 event loop threads");
    }

    public static RpcConsumer getInstance() {
        if (instance == null) {
            synchronized (RpcConsumer.class) {
                if (instance == null) {
                    LOGGER.debug("Creating singleton instance of RpcConsumer");
                    instance = new RpcConsumer();
                }
            }
        }
        return instance;
    }

    /**
     * Sends an RPC request to the service provider
     * Manages connection creation/reuse and delegates the actual sending to RpcConsumerHandler
     *
     * @param protocol the RPC protocol containing the request data
     * @return the result of the RPC call
     * @throws InterruptedException if the connection process is interrupted
     */
    public RpcFuture sendRequest(RpcProtocol<RpcRequest> protocol) throws InterruptedException {
        // TODO Hardcoded for now, will be fetched from registry center in the future
        String serviceAddress = "127.0.0.1";
        int port = 27880;
        String key = serviceAddress.concat("_").concat(String.valueOf(port));

        LOGGER.debug("Target service address: {}:{}", serviceAddress, port);
        RpcConsumerHandler handler = handlerMap.get(key);

        if (handler == null) {
            LOGGER.debug("No existing connection, creating new connection to service provider");
            handler = getRpcConsumerHandler(serviceAddress, port);
            handlerMap.put(key, handler);
        } else if (!handler.getChannel().isActive()) {
            LOGGER.warn("Inactive connection to {}:{}, creating new connection", serviceAddress, port);
            handler.close();
            handler = getRpcConsumerHandler(serviceAddress, port);
            handlerMap.put(key, handler);
        } else {
            LOGGER.debug("Reusing existing active connection to service provider");
        }

        return handler.sendRequest(protocol);
    }

    /**
     * Gets or creates an RPC consumer handler for the specified service address and port
     *
     * @param serviceAddress the service provider address
     * @param port           the service provider port
     * @return the RPC consumer handler
     * @throws InterruptedException if the connection process is interrupted
     */
    public RpcConsumerHandler getRpcConsumerHandler(String serviceAddress, int port) throws InterruptedException {
        LOGGER.info("Establishing connection to service provider at {}:{}", serviceAddress, port);
        ChannelFuture channelFuture = bootstrap.connect(serviceAddress, port).sync();
        channelFuture.addListener((ChannelFutureListener) listener -> {
            if (listener.isSuccess()) {
                LOGGER.info("Successfully connected to service provider at {}:{}", serviceAddress, port);
            } else {
                LOGGER.error("Failed to connect to service provider at {}:{}", serviceAddress, port, listener.cause());
                eventLoopGroup.shutdownGracefully();
            }
        });
       return channelFuture.channel().pipeline().get(RpcConsumerHandler.class);
    }

    /**
     * Closes the RPC consumer and releases all resources
     */
    public void close() {
        LOGGER.info("Shutting down RPC consumer and event loop group");
        eventLoopGroup.shutdownGracefully();
    }
}