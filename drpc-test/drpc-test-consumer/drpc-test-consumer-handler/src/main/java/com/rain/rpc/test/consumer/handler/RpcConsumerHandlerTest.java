package com.rain.rpc.test.consumer.handler;

import com.rain.rpc.constants.RpcConstants;
import com.rain.rpc.consumer.common.RpcConsumer;
import com.rain.rpc.protocol.RpcProtocol;
import com.rain.rpc.protocol.header.RpcHeaderFactory;
import com.rain.rpc.protocol.request.RpcRequest;

public class RpcConsumerHandlerTest {
    public static void main(String[] args) throws InterruptedException {
        RpcConsumer instance = RpcConsumer.getInstance();
        instance.sendRequest(getRpcRequestProtocol());
        Thread.sleep(1000);
        instance.close();
    }

    private static RpcProtocol<RpcRequest> getRpcRequestProtocol() {
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
        protocol.setHeader(RpcHeaderFactory.getRequestHeader(RpcConstants.SERIALIZATION_JDK));
        RpcRequest request = new RpcRequest();
        request.setClassName("com.rain.rpc.test.api.DemoService");
        request.setMethodName("hello");
        request.setParameters(new Object[]{"rain"});
        request.setParameterTypes(new Class[]{String.class});
        request.setVersion("1.0.0");
        request.setGroup("default");
        request.setOneway(false);
        request.setAsync(false);
        protocol.setBody(request);
        return protocol;
    }
}
