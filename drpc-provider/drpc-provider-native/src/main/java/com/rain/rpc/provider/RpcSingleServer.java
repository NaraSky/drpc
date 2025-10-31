package com.rain.rpc.provider;

import com.rain.rpc.provider.common.scanner.RpcServiceScanner;
import com.rain.rpc.provider.common.server.base.BaseServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcSingleServer extends BaseServer {

    private final Logger logger = LoggerFactory.getLogger(RpcSingleServer.class);

    /**
     * Initialize RPC server with service scanning
     * @param serverAddress server address (host:port)
     * @param registryAddress registry center address
     * @param registryType registry type (zookeeper/nacos/etcd)
     * @param scanPackage package to scan for @RpcService
     * @param reflectType reflection type (jdk/cglib)
     */
    public RpcSingleServer(String serverAddress, String registryAddress, String registryType, String scanPackage, String reflectType) {
        super(serverAddress, registryAddress, registryType, reflectType);
        logger.info("RpcSingleServer init - address: {}, registry: {}:{}, reflect: {}, scan: {}", 
            serverAddress, registryType, registryAddress, reflectType, scanPackage);

        try {
            this.handlerMap = RpcServiceScanner.doScannerWithRpcServiceAnnotationFilterAndRegistryService(
                this.host, this.port, scanPackage, registryService);
            
            if (!handlerMap.isEmpty()) {
                logger.info("Service registration completed - {} services registered", handlerMap.size());
                logger.debug("Registered services: {}", handlerMap.keySet());
            } else {
                logger.warn("No @RpcService found in package: {}", scanPackage);
            }
        } catch (Exception e) {
            logger.error("Service scanning failed for package: {}", scanPackage, e);
        }
    }
}