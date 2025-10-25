package com.rain.rpc.common.scanner.reference;

import com.rain.rpc.annotation.RpcReference;
import com.rain.rpc.common.scanner.ClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * RpcReference annotation scanner
 */
public class RpcReferenceScanner extends ClassLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcReferenceScanner.class);

    /**
     * Scan classes in the specified package and filter fields annotated with @RpcReference
     *
     * @param scanPackage the package to scan
     * @return a map containing the handler mappings
     * @throws Exception if any error occurs during scanning
     */
    public static Map<String, Object> doScannerWithRpcReferenceAnnotationFilter(String scanPackage) throws Exception {
        Map<String, Object> handlerMap = new HashMap<>();
        List<String> classNameList = ClassScanner.getClassNameList(scanPackage);
        if (classNameList == null || classNameList.isEmpty()) {
            return handlerMap;
        }
        classNameList.stream().forEach((className) -> {
            try {
                Class<?> clazz = Class.forName(className);
                Field[] declaredFields = clazz.getDeclaredFields();
                Stream.of(declaredFields).forEach((field) -> {
                    RpcReference rpcReference = field.getAnnotation(RpcReference.class);
                    if (rpcReference != null) {
                        // TODO Process subsequent logic, create proxy objects for interface references annotated with @RpcReference and put them into global cache
                        LOGGER.info("Currently annotated @RpcReference field name: {}", field.getName());
                        LOGGER.info("Properties annotated on @RpcReference:");
                        LOGGER.info("version: {}", rpcReference.version());
                        LOGGER.info("group: {}", rpcReference.group());
                        LOGGER.info("registryType: {}", rpcReference.registryType());
                        LOGGER.info("registryAddress: {}", rpcReference.registryAddr());
                        LOGGER.info("loadBalanceType: {}", rpcReference.loadBalanceType());
                        LOGGER.info("serializationType: {}", rpcReference.serializationType());
                        LOGGER.info("timeout: {}", rpcReference.timeout());
                        LOGGER.info("async: {}", rpcReference.async());
                        LOGGER.info("oneway: {}", rpcReference.oneway());
                        LOGGER.info("proxy: {}", rpcReference.proxy());
                    }
                });
            } catch (Exception e) {
                LOGGER.error("Failed to scan classes: {}", e.getMessage(), e);
            }
        });
        return handlerMap;
    }
}