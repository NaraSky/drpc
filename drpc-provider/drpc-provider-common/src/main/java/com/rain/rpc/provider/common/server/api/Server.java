package com.rain.rpc.provider.common.server.api;

/**
 * Server interface that defines the basic contract for RPC server implementations.
 * Provides a standardized way to start a Netty-based RPC server.
 */
public interface Server {

    /**
     * Starts the Netty server to handle RPC requests.
     */
    void startNettyServer();

}
