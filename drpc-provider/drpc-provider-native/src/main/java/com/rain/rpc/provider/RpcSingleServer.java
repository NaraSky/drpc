package com.rain.rpc.provider;

import com.rain.rpc.common.scanner.server.RpcServiceScanner;
import com.rain.rpc.provider.common.server.base.BaseServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcSingleServer extends BaseServer {

    private final Logger logger = LoggerFactory.getLogger(RpcSingleServer.class);

        /**
     * Constructs an RpcSingleServer with the specified server address and package to scan.
     * Initializes the server and scans for RPC service implementations in the specified package.
     *
     * @param serverAddress the server address in "host:port" format, or null/empty for default
     * @param scanPackage the package to scan for RPC service implementations
     */
    public RpcSingleServer(String serverAddress, String scanPackage, String reflectType) {
        super(serverAddress, reflectType);
        logger.info("Initializing RpcSingleServer with serverAddress: {}, scanPackage: {}, reflectType: {}", 
            serverAddress, scanPackage, reflectType);
        try {
            this.handlerMap = RpcServiceScanner.doScannerWithRpcServiceAnnotationFilterAndRegistryService(scanPackage);
            logger.info("Successfully scanned and registered {} service implementations", handlerMap.size());
        } catch (Exception e) {
            logger.error("Failed to initialize RPC Server by scanning services in package: {}", scanPackage, e);
        }
    }
}