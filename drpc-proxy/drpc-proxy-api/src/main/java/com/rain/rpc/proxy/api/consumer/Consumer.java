package com.rain.rpc.proxy.api.consumer;

import com.rain.rpc.protocol.RpcProtocol;
import com.rain.rpc.protocol.request.RpcRequest;
import com.rain.rpc.proxy.api.future.RpcFuture;

public interface Consumer {

    RpcFuture sendRequest(RpcProtocol<RpcRequest> protocol) throws Exception;
}
