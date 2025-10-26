package com.rain.rpc.test.provider.single;

import com.rain.rpc.provider.RpcSingleServer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RPC Single Server Test
 * Tests the startup and operation of a single RPC server instance
 */
public class RpcSingleServerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcSingleServerTest.class);

    @Test
    public void startRpcSingleServer(){
        LOGGER.info("Starting RPC Single Server test");
        
        // Create RPC server instance with specified host, port and service package
        String serverAddress = "127.0.0.1:27880";
        String servicePackage = "com.rain.rpc.test";
        LOGGER.info("Creating RPC server with address: {} and scanning package: {}", serverAddress, servicePackage);
        
        RpcSingleServer singleServer = new RpcSingleServer(serverAddress, servicePackage);
        
        LOGGER.info("RPC server instance created successfully, now starting Netty server");
        
        // Start the Netty server
        singleServer.startNettyServer();
        
        LOGGER.info("RPC server started successfully");
    }
}