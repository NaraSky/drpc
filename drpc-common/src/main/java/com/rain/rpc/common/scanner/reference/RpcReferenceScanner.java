package com.rain.rpc.common.scanner.reference;

import com.rain.rpc.annotation.RpcReference;
import com.rain.rpc.common.scanner.ClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * RpcReference annotation scanner
 */
public class RpcReferenceScanner extends ClassScanner {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcReferenceScanner.class);

    /**
     * Scan classes in the specified package and filter fields annotated with @RpcReference
     *
     * @param scanPackage the package to scan
     * @return a map containing the handler mappings
     * @throws Exception if any error occurs during scanning
     */
    public static Map<String, Object> doScannerWithRpcReferenceAnnotationFilter(String scanPackage) throws Exception {
        LOGGER.info("Starting reference scanning process for package: {}", scanPackage);
        
        Map<String, Object> handlerMap = new HashMap<>();
        List<String> classNameList = ClassScanner.getClassNameList(scanPackage);
        if (classNameList == null || classNameList.isEmpty()) {
            LOGGER.warn("No classes found in package: {}", scanPackage);
            return handlerMap;
        }
        
        LOGGER.info("Found {} classes in package: {}", classNameList.size(), scanPackage);
        
        AtomicInteger referenceCount = new AtomicInteger(0);
        classNameList.stream().forEach((className) -> {
            try {
                Class<?> clazz = Class.forName(className);
                Field[] declaredFields = clazz.getDeclaredFields();
                Stream.of(declaredFields).forEach((field) -> {
                    RpcReference rpcReference = field.getAnnotation(RpcReference.class);
                    if (rpcReference != null) {
                        // TODO Process subsequent logic, create proxy objects for interface references annotated with @RpcReference and put them into global cache
                        LOGGER.info("Found @RpcReference annotation on field: {}.{}", clazz.getSimpleName(), field.getName());
                        referenceCount.incrementAndGet();
                    }
                });
            } catch (Exception e) {
                LOGGER.error("Failed to scan class: {}", className, e);
            }
        });
        
        LOGGER.info("Reference scanning completed. Total references found: {}", referenceCount.get());
        return handlerMap;
    }
}