package com.rain.rpc.protocol.base;

import java.io.Serializable;

public class RpcMessage implements Serializable {

    private boolean oneway;

    private boolean async;

    public boolean isOneway() {
        return oneway;
    }

    public void setOneway(boolean oneway) {
        this.oneway = oneway;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }
}
