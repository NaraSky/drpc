package com.rain.rpc.consumer.common;

import com.rain.rpc.common.helper.RpcServiceHelper;
import com.rain.rpc.common.threadpool.ClientThreadPool;
import com.rain.rpc.consumer.common.handler.RpcConsumerHandler;
import com.rain.rpc.consumer.common.helper.RpcConsumerHandlerHelper;
import com.rain.rpc.consumer.common.initializer.RpcConsumerInitializer;
import com.rain.rpc.protocol.RpcProtocol;
import com.rain.rpc.protocol.meta.ServiceMeta;
import com.rain.rpc.protocol.request.RpcRequest;
import com.rain.rpc.proxy.api.consumer.Consumer;
import com.rain.rpc.proxy.api.future.RpcFuture;
import com.rain.rpc.registry.api.RegistryService;
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
 * Manages RPC consumer connections and sends requests to providers.
 */
public class RpcConsumer implements Consumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcConsumer.class);
    private static volatile RpcConsumer instance;
    private static Map<String, RpcConsumerHandler> handlerMap = new ConcurrentHashMap<>();
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    // Constructor with Netty bootstrap and event loop group
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

    @Override
    public RpcFuture sendRequest(RpcProtocol<RpcRequest> protocol, RegistryService registryService) throws Exception {
        RpcRequest request = protocol.getBody();
        String serviceKey = RpcServiceHelper.buildServiceKey(
            request.getClassName(), request.getVersion(), request.getGroup());
        
        Object[] params = request.getParameters();
        int invokerHashCode = (params == null || params.length == 0) 
            ? serviceKey.hashCode() : params[0].hashCode();
        
        ServiceMeta serviceMeta = registryService.discovery(serviceKey, invokerHashCode);
        if (serviceMeta == null) {
            LOGGER.error("Service not found: {}", serviceKey);
            return null;
        }
        
        RpcConsumerHandler handler = RpcConsumerHandlerHelper.get(serviceMeta);
        
        if (handler == null) {
            handler = getRpcConsumerHandler(serviceMeta.getServiceAddr(), serviceMeta.getServicePort());
            RpcConsumerHandlerHelper.put(serviceMeta, handler);
        } else if (!handler.getChannel().isActive()) {
            LOGGER.warn("Connection inactive, reconnecting to {}:{}", 
                serviceMeta.getServiceAddr(), serviceMeta.getServicePort());
            handler.close();
            handler = getRpcConsumerHandler(serviceMeta.getServiceAddr(), serviceMeta.getServicePort());
            RpcConsumerHandlerHelper.put(serviceMeta, handler);
        }
        
        return handler.sendRequest(protocol, request.isAsync(), request.isOneway());
    }

    public RpcConsumerHandler getRpcConsumerHandler(String address, int port) throws InterruptedException {
        LOGGER.info("Connecting to {}:{}", address, port);
        ChannelFuture channelFuture = bootstrap.connect(address, port).sync();
        channelFuture.addListener((ChannelFutureListener) listener -> {
            if (listener.isSuccess()) {
                LOGGER.info("Connected to {}:{}", address, port);
            } else {
                LOGGER.error("Connection failed to {}:{}", address, port, listener.cause());
                eventLoopGroup.shutdownGracefully();
            }
        });
        return channelFuture.channel().pipeline().get(RpcConsumerHandler.class);
    }

    public void close() {
        LOGGER.info("Shutting down RPC consumer");
        RpcConsumerHandlerHelper.closeRpcClientHandler();
        eventLoopGroup.shutdownGracefully();
        ClientThreadPool.shutdown();
    }
}