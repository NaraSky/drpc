package com.rain.rpc.test.scanner.service.provider;

import com.rain.rpc.annotation.RpcService;

@RpcService(interfaceClass = DemoService.class, interfaceClassName = "com.rain.rpc.test.scanner.service.provider.DemoService", version = "1.0.0", group = "default")
public class ProviderDemoServiceImpl implements DemoService {

}