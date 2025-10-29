package com.rain.rpc.consumer.common.callback;

public interface AsyncRpcCallback {

    /**
     * Called when the RPC call completes successfully.
     * 
     * @param result the result returned by the RPC call
     */
    void onSuccess(Object result);

    /**
     * Called when the RPC call encounters an error.
     * 
     * @param e the exception that occurred during the RPC call
     */
    void onException(Exception e);
}