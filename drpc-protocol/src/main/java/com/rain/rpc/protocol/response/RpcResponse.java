package com.rain.rpc.protocol.response;

import com.rain.rpc.protocol.base.RpcMessage;

public class RpcResponse extends RpcMessage {

    private static final long serialVersionUID = -4463995592120023755L;

    private String error;
    private Object result;

    public boolean isError() {
        return error != null;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "error='" + error + '\'' +
                ", result=" + result +
                '}';
    }
}
