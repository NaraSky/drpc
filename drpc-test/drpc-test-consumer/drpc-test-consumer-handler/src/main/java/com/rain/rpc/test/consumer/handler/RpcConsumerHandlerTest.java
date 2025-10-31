package com.rain.rpc.test.consumer.handler;

import com.rain.rpc.common.exception.RegistryException;
import com.rain.rpc.consumer.common.RpcConsumer;
import com.rain.rpc.consumer.common.context.RpcContext;
import com.rain.rpc.protocol.RpcProtocol;
import com.rain.rpc.protocol.header.RpcHeaderFactory;
import com.rain.rpc.protocol.request.RpcRequest;
import com.rain.rpc.proxy.api.callback.AsyncRpcCallback;
import com.rain.rpc.proxy.api.future.RpcFuture;
import com.rain.rpc.registry.api.RegistryService;
import com.rain.rpc.registry.api.config.RegistryConfig;
import com.rain.rpc.registry.zookeeper.ZookeeperRegistryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcConsumerHandlerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcConsumerHandlerTest.class);


    public static void main(String[] args) throws Exception {
        RpcConsumer consumer = RpcConsumer.getInstance();
        RpcFuture rpcFuture = consumer.sendRequest(getRpcRequestProtocol(), getRegistryService("117.72.33.162:2181", "zookeeper"));
        rpcFuture.addCallback(new AsyncRpcCallback() {
            @Override
            public void onSuccess(Object result) {
                LOGGER.info("Received data from consumer: {}", result);
            }

            @Override
            public void onException(Exception e) {
                LOGGER.info("Exception occurred: {}", e.getMessage());
            }
        });
        Thread.sleep(200);
        consumer.close();
    }

    public static void mainAsync(String[] args) throws Exception {
        RpcConsumer consumer = RpcConsumer.getInstance();
        consumer.sendRequest(getRpcRequestProtocol(), getRegistryService("117.72.33.162:2181", "zookeeper"));
        RpcFuture future = RpcContext.getContext().getRpcFuture();
        LOGGER.info("Received data from consumer: {}", future.get());
        consumer.close();
    }

    private static RegistryService getRegistryService(String registryAddress, String registryType) {
        if (StringUtils.isEmpty(registryType)) {
            throw new IllegalArgumentException("registry type is null");
        }
        // TODO: Support SPI extension for multiple registry types
        RegistryService registryService = new ZookeeperRegistryService();
        try {
            registryService.init(new RegistryConfig(registryAddress, registryType));
        } catch (Exception e) {
            LOGGER.error("RpcClient init registry service throws exception:{}", e);
            throw new RegistryException(e.getMessage(), e);
        }
        return registryService;
    }

    public static void mainSync(String[] args) throws Exception {
        RpcConsumer consumer = RpcConsumer.getInstance();
        RpcFuture future = consumer.sendRequest(getRpcRequestProtocol(), getRegistryService("117.72.33.162:2181", "zookeeper"));
        LOGGER.info("Received data from consumer: {}", future.get());
        consumer.close();
    }

    private static RpcProtocol<RpcRequest> getRpcRequestProtocol() {
        // Simulate sending data
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<RpcRequest>();
        protocol.setHeader(RpcHeaderFactory.getRequestHeader("jdk"));
        RpcRequest request = new RpcRequest();
        request.setClassName("io.binghe.rpc.test.api.DemoService");
        request.setGroup("binghe");
        request.setMethodName("hello");
        request.setParameters(new Object[]{"binghe"});
        request.setParameterTypes(new Class[]{String.class});
        request.setVersion("1.0.0");
        request.setAsync(false);
        request.setOneway(false);
        protocol.setBody(request);
        return protocol;
    }

    private static RpcProtocol<RpcRequest> getRpcRequestProtocolAsync() {
        // Simulate sending data
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<RpcRequest>();
        protocol.setHeader(RpcHeaderFactory.getRequestHeader("jdk"));
        RpcRequest request = new RpcRequest();
        request.setClassName("io.binghe.rpc.test.api.DemoService");
        request.setGroup("binghe");
        request.setMethodName("hello");
        request.setParameters(new Object[]{"binghe"});
        request.setParameterTypes(new Class[]{String.class});
        request.setVersion("1.0.0");
        request.setAsync(true);
        request.setOneway(false);
        protocol.setBody(request);
        return protocol;
    }

    private static RpcProtocol<RpcRequest> getRpcRequestProtocolSync() {
        // Simulate sending data
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<RpcRequest>();
        protocol.setHeader(RpcHeaderFactory.getRequestHeader("jdk"));
        RpcRequest request = new RpcRequest();
        request.setClassName("io.binghe.rpc.test.api.DemoService");
        request.setGroup("binghe");
        request.setMethodName("hello");
        request.setParameters(new Object[]{"binghe"});
        request.setParameterTypes(new Class[]{String.class});
        request.setVersion("1.0.0");
        request.setAsync(false);
        request.setOneway(false);
        protocol.setBody(request);
        return protocol;
    }
}
