package com.rain.rpc.common.scanner.server;

import com.rain.rpc.annotation.RpcService;
import com.rain.rpc.common.helper.RpcServiceHelper;
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
public class RpcServiceScanner extends ClassScanner {

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
        LOGGER.info("Starting service scanning process for package: {}", scanPackage);

        Map<String, Object> handlerMap = new HashMap<>();
        List<String> classNameList = ClassScanner.getClassNameList(scanPackage);

        if (classNameList == null || classNameList.isEmpty()) {
            LOGGER.warn("No classes found in package: {}", scanPackage);
            return handlerMap;
        }

        LOGGER.info("Found {} classes in package: {}", classNameList.size(), scanPackage);

        int serviceCount = 0;
        for (String className : classNameList) {
            try {
                Class<?> clazz = Class.forName(className);
                RpcService rpcService = clazz.getAnnotation(RpcService.class);

                if (rpcService != null) {
                    String serviceName = getServiceName(rpcService);
                    String key = RpcServiceHelper.buildServiceKey(serviceName, rpcService.version(), rpcService.group());

                    Object serviceInstance = clazz.newInstance();
                    handlerMap.put(key, serviceInstance);
                    serviceCount++;

                    LOGGER.info("Registered service: {} with key: {}", serviceName, key);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to process class: {}", className, e);
            }
        }

        LOGGER.info("Service scanning completed. Total registered services: {}", serviceCount);
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