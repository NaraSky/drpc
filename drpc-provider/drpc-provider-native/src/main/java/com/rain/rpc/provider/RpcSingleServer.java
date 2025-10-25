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
    public RpcSingleServer(String serverAddress, String scanPackage) {
        super(serverAddress);
        try {
            this.handlerMap = RpcServiceScanner.doScannerWithRpcServiceAnnotationFilterAndRegistryService(scanPackage);
        } catch (Exception e) {
            logger.error("RPC Server init error", e);
        }
    }
}
