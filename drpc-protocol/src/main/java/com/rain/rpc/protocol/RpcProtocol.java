package com.rain.rpc.protocol;

import com.rain.rpc.protocol.header.RpcHeader;

import java.io.Serializable;

public class RpcProtocol<T> implements Serializable {

    private static final long serialVersionUID = 2129749731348057359L;

    private RpcHeader header;

    private T body;

    public RpcHeader getHeader() {
        return header;
    }

    public void setHeader(RpcHeader header) {
        this.header = header;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "RpcProtocol{" +
                "header=" + header +
                ", body=" + body +
                '}';
    }
}
