package com.rain.rpc.common.scanner.server;

import com.rain.rpc.annotation.RpcService;
import com.rain.rpc.common.scanner.ClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Scanner for classes annotated with @RpcService.
 * This class scans packages for classes annotated with @RpcService and registers them as services.
 */
public class RpcServiceScanner extends ClassLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServiceScanner.class);

    /**
     * Scans classes in the specified package and filters classes annotated with @RpcService.
     * For each class annotated with @RpcService, creates an instance and registers it in the handler map.
     *
     * @param scanPackage the package to scan for @RpcService annotated classes
     * @return a map containing the service instances with their keys
     * @throws Exception if any error occurs during class scanning or instantiation
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

                    String serviceName = getServiceName(rpcService);
                    // com.rain.rpc.test.provider.service.DemoService1.0.0default
                    String key = serviceName.concat(rpcService.version()).concat(rpcService.group());
                    handlerMap.put(key, clazz.newInstance());
                }
            } catch (Exception e) {
                LOGGER.error("Failed to scan classes: {}", e.getMessage(), e);
            }
        });
        return handlerMap;
    }

    /**
     * Gets the service name from the @RpcService annotation.
     * Prioritizes interfaceClass if available, otherwise uses interfaceClassName.
     *
     * @param rpcService the @RpcService annotation instance
     * @return the service name
     */
    private static String getServiceName(RpcService rpcService) {
        Class<?> clazz = rpcService.interfaceClass();
        if (clazz != void.class) {
            return clazz.getName();
        }
        String serviceName = clazz.getName();
        if (serviceName == null || serviceName.trim().isEmpty()) {
            serviceName = rpcService.interfaceClassName();
        }
        return serviceName;
    }
}