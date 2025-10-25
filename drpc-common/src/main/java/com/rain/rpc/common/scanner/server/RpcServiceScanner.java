package com.rain.rpc.common.scanner.server;

import com.rain.rpc.annotation.RpcService;
import com.rain.rpc.common.scanner.ClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RpcService annotation scanner
 */
public class RpcServiceScanner extends ClassLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServiceScanner.class);

    /**
     * Scan classes in the specified package and filter classes annotated with @RpcService
     *
     * @param scanPackage the package to scan
     * @return a map containing the handler mappings
     * @throws Exception if any error occurs during scanning
     */
    public static Map<String, Object> doScannerWithRpcServiceAnnotationFilterAndRegistryService(String scanPackage) throws Exception {
        Map<String, Object> handlerMap = new HashMap<>();
        List<String> classNameList = ClassScanner.getClassNameList(scanPackage);
        if (classNameList == null || classNameList.isEmpty()) {
            return handlerMap;
        }
        classNameList.stream().forEach((className) -> {
            try {
                Class<?> clazz = Class.forName(className);
                RpcService rpcService = clazz.getAnnotation(RpcService.class);
                if (rpcService != null) {
                    // Prioritize using interfaceClass, if interfaceClass name is empty, then use interfaceClassName
                    // TODO Subsequently, register service metadata with the registry center and record instances of classes annotated with @RpcService in handlerMap
                    LOGGER.info("Currently annotated @RpcService class instance name: {}", clazz.getName());
                    LOGGER.info("Properties annotated on @RpcService:");
                    LOGGER.info("interfaceClass: {}", rpcService.interfaceClass().getName());
                    LOGGER.info("interfaceClassName: {}", rpcService.interfaceClassName());
                    LOGGER.info("version: {}", rpcService.version());
                    LOGGER.info("group: {}", rpcService.group());
                }
            } catch (Exception e) {
                LOGGER.error("Failed to scan classes: {}", e.getMessage(), e);
            }
        });
        return handlerMap;
    }
}