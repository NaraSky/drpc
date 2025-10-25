package com.rain.rpc.test.provider.service;

import com.rain.rpc.annotation.RpcService;

@RpcService(interfaceClass = DemoService.class, interfaceClassName = "com.rain.rpc.test.provider.service.DemoService", version = "1.0.0", group = "default")
public class ProviderDemoServiceImpl implements DemoService {
}
