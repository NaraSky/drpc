package com.rain.rpc.test.scanner;

import com.rain.rpc.common.scanner.ClassScanner;
import com.rain.rpc.common.scanner.reference.RpcReferenceScanner;
import com.rain.rpc.provider.common.scanner.RpcServiceScanner;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ScannerTest {

    /**
     * Scan all classes in com.rain.rpc.test.scanner package
     */
    @Test
    public void testScannerClassNameList() throws Exception {
        List<String> classNameList = ClassScanner.getClassNameList("com.rain.rpc.test.scanner");
        classNameList.forEach(System.out::println);
    }

    /**
     * Scan classes annotated with @RpcService in com.rain.rpc.test.scanner package
     */
    @Test
    public void testScannerClassNameListByRpcService() throws Exception {
        RpcServiceScanner.doScannerWithRpcServiceAnnotationFilterAndRegistryService("117.72.33.162", 8080, "com.rain.rpc.test.scanner", null);
    }

    /**
     * Scan classes annotated with @RpcReference in com.rain.rpc.test.scanner package
     */
    @Test
    public void testScannerClassNameListByRpcReference() throws Exception {
        RpcReferenceScanner.doScannerWithRpcReferenceAnnotationFilter("com.rain.rpc.test.scanner");
    }
}
