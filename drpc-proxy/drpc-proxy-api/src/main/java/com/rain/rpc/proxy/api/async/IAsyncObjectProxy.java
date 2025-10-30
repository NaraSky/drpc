package com.rain.rpc.proxy.api.async;

import com.rain.rpc.proxy.api.future.RpcFuture;

/**
 * Interface for asynchronous object proxy
 */
public interface IAsyncObjectProxy {

    /**
     * Asynchronously call a method
     *
     * @param methodName the name of the method to call
     * @param args the arguments to pass to the method
     * @return RpcFuture representing the result of the asynchronous call
     */
    RpcFuture call(String methodName, Object... args);
}
