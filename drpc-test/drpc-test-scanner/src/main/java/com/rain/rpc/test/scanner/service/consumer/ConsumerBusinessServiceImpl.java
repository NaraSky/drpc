package com.rain.rpc.test.scanner.service.consumer;

import com.rain.rpc.annotation.RpcReference;
import com.rain.rpc.test.scanner.service.provider.DemoService;

public class ConsumerBusinessServiceImpl implements ConsumerBusinessService {

    @RpcReference(registryType = "zookeeper", registryAddr = "117.72.33.162:2181", version = "1.0.0", group = "default")
    private DemoService demoService;

}