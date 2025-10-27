package com.rain.rpc.provider.common.server.base;

import com.rain.rpc.codec.RpcDecoder;
import com.rain.rpc.codec.RpcEncoder;
import com.rain.rpc.provider.common.handler.RpcProviderHandler;
import com.rain.rpc.provider.common.server.api.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
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

    /**
     * Constructs a BaseServer with the specified server address.
     * If serverAddress is null or empty, uses default host and port.
     * 
     * @param serverAddress the server address in "host:port" format, or null/empty for default
     */
    public BaseServer(String serverAddress, String reflectType) {
        if (!StringUtils.isEmpty(serverAddress)) {
            String[] serverArray = serverAddress.split(":");
            // Check if the server address has both host and port
            if (serverArray.length == 2) {
                this.host = serverArray[0];
                try {
                    this.port = Integer.parseInt(serverArray[1]);
                } catch (NumberFormatException e) {
                    LOGGER.error("Invalid port number: {}, using default port: {}", serverArray[1], DEFAULT_PORT);
                    this.port = DEFAULT_PORT;
                }
            } else {
                LOGGER.warn("Invalid server address format: {}, using default host: {} and port: {}", serverAddress, DEFAULT_HOST, DEFAULT_PORT);
            }
        }
        this.reflectType = reflectType;
        LOGGER.info("BaseServer initialized with host: {}, port: {}, reflectType: {}", this.host, this.port, this.reflectType);
    }

    /**
     * Start the Netty RPC server with the configured host and port.
     * Sets up the Netty pipeline with RPC decoder, encoder, and handler.
     */
    @Override
    public void startNettyServer() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            // Add RPC codec handlers and provider handler to the pipeline
                            pipeline.addLast(new RpcDecoder())
                                    .addLast(new RpcEncoder())
                                    .addLast(new RpcProviderHandler(reflectType, handlerMap));
                        }
                    }).option(io.netty.channel.ChannelOption.SO_BACKLOG, 128)
                    .childOption(io.netty.channel.ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = bootstrap.bind(host, port).sync();
            LOGGER.info("RPC Provider Server started successfully on {}:{}", host, port);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error("RPC Provider Server interrupted during operation: {}", e.getMessage(), e);
            Thread.currentThread().interrupt(); // Preserve the interrupted status
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            LOGGER.info("RPC Provider Server shutdown completed");
        }
    }
}