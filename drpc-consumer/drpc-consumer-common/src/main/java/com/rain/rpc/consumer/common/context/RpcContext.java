package com.rain.rpc.consumer.common.context;

import com.rain.rpc.consumer.common.future.RpcFuture;

public class RpcContext {
    public RpcContext() {
    }

    private static final RpcContext AGENT_CONTEXT = new RpcContext();

    private static final  InheritableThreadLocal<RpcFuture> RPC_FUTURE_INHERITABLE_THREAD_LOCAL = new InheritableThreadLocal<>();

    public static RpcContext getContext() {
        return AGENT_CONTEXT;
    }

    public void setRpcFuture(RpcFuture rpcFuture) {
        RPC_FUTURE_INHERITABLE_THREAD_LOCAL.set(rpcFuture);
    }

    public RpcFuture getRpcFuture() {
        return RPC_FUTURE_INHERITABLE_THREAD_LOCAL.get();
    }

    public void clear() {
        RPC_FUTURE_INHERITABLE_THREAD_LOCAL.remove();
    }
}
