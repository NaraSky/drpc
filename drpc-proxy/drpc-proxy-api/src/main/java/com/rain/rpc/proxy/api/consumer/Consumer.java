package com.rain.rpc.proxy.api.consumer;

import com.rain.rpc.protocol.RpcProtocol;
import com.rain.rpc.protocol.request.RpcRequest;
import com.rain.rpc.proxy.api.future.RpcFuture;
import com.rain.rpc.registry.api.RegistryService;

public interface Consumer {

    RpcFuture sendRequest(RpcProtocol<RpcRequest> protocol, RegistryService registryService) throws Exception;
}
