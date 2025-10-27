package com.rain.rpc.test.provider.service;

import com.rain.rpc.annotation.RpcService;
import com.rain.rpc.test.api.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RpcService(interfaceClass = DemoService.class, interfaceClassName = "com.rain.rpc.test.api.DemoService", version = "1.0.0", group = "default")
public class ProviderDemoServiceImpl implements DemoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderDemoServiceImpl.class);

    @Override
    public String hello(String name) {
        LOGGER.info("Received request: {}", name);
        return "Hello " + name;
    }
}
