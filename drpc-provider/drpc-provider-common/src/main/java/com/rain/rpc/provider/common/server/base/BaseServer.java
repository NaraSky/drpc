package com.rain.rpc.provider.common.server.base;

import com.rain.rpc.codec.RpcDecoder;
import com.rain.rpc.codec.RpcEncoder;
import com.rain.rpc.provider.common.handler.RpcProviderHandler;
import com.rain.rpc.provider.common.server.api.Server;
import com.rain.rpc.registry.api.RegistryService;
import com.rain.rpc.registry.api.config.RegistryConfig;
import com.rain.rpc.registry.zookeeper.ZookeeperRegistryService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Base implementation of the Server interface using the Netty framework.
 * Provides a foundation for RPC server implementations with the configurable host and port.
 * This class handles the setup and configuration of the Netty server.
 */
public class BaseServer implements Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseServer.class);
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 27110;

    protected String host = DEFAULT_HOST;
    protected int port = DEFAULT_PORT;
    protected Map<String, Object> handlerMap = new HashMap<>();
    private String reflectType;

    protected RegistryService registryService;

    /**
     * Initialize base server with address parsing and registry setup
     * @param serverAddress server address (host:port), uses default if empty
     * @param registryAddress registry center address
     * @param registryType registry type (zookeeper/nacos/etcd)
     * @param reflectType reflection type (jdk/cglib)
     */
    public BaseServer(String serverAddress, String registryAddress, String registryType, String reflectType) {
        if (!StringUtils.isEmpty(serverAddress)) {
            String[] serverArray = serverAddress.split(":");
            if (serverArray.length == 2) {
                this.host = serverArray[0];
                try {
                    this.port = Integer.parseInt(serverArray[1]);
                } catch (NumberFormatException e) {
                    LOGGER.error("Invalid port: {}, using default: {}", serverArray[1], DEFAULT_PORT);
                    this.port = DEFAULT_PORT;
                }
            } else {
                LOGGER.warn("Invalid address format: {}, using default {}:{}", serverAddress, DEFAULT_HOST, DEFAULT_PORT);
            }
        }
        
        this.reflectType = reflectType;
        this.registryService = getRegistryService(registryAddress, registryType);
        LOGGER.info("BaseServer init - {}:{}, reflect: {}, registry: {}:{}", 
            this.host, this.port, this.reflectType, registryType, registryAddress);
    }

    private RegistryService getRegistryService(String registryAddress, String registryType) {
        try {
            RegistryService service = new ZookeeperRegistryService();
            service.init(new RegistryConfig(registryAddress, registryType));
            return service;
        } catch (Exception e) {
            LOGGER.error("Registry service init failed: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void startNettyServer() {
        LOGGER.info("Starting Netty server on {}:{}", host, port);

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new RpcDecoder());
                            pipeline.addLast(new RpcEncoder());
                            pipeline.addLast(new RpcProviderHandler(reflectType, handlerMap));
                        }
                    })
                    .option(io.netty.channel.ChannelOption.SO_BACKLOG, 128)
                    .childOption(io.netty.channel.ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = bootstrap.bind(host, port).sync();
            LOGGER.info("Server started - {}:{}, {} services registered", host, port, handlerMap.size());

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error("Server interrupted: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            LOGGER.info("Server shutdown completed");
        }
    }
}