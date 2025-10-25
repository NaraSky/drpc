package com.rain.rpc.test.scanner;

import com.rain.rpc.common.scanner.ClassScanner;
import com.rain.rpc.common.scanner.reference.RpcReferenceScanner;
import com.rain.rpc.common.scanner.server.RpcServiceScanner;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ScannerTest {

    /**
     * 扫描com.rain.rpc.test.scanner包下所有的类
     */
    @Test
    public void testScannerClassNameList() throws Exception {
        List<String> classNameList = ClassScanner.getClassNameList("com.rain.rpc.test.scanner");
        classNameList.forEach(System.out::println);
    }

    /**
     * 扫描com.rain.rpc.test.scanner包下所有标注了@RpcService注解的类
     */
    @Test
    public void testScannerClassNameListByRpcService() throws Exception {
        RpcServiceScanner.doScannerWithRpcServiceAnnotationFilterAndRegistryService("com.rain.rpc.test.scanner");
    }

    /**
     * 扫描com.rain.rpc.test.scanner包下所有标注了@RpcReference注解的类
     */
    @Test
    public void testScannerClassNameListByRpcReference() throws Exception {
        RpcReferenceScanner.doScannerWithRpcReferenceAnnotationFilter("com.rain.rpc.test.scanner");
    }
}
