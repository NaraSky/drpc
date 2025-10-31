package com.rain.rpc.test.consumer;

import com.rain.rpc.consumer.RpcClient;
import com.rain.rpc.proxy.api.async.IAsyncObjectProxy;
import com.rain.rpc.proxy.api.future.RpcFuture;
import com.rain.rpc.test.api.DemoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcConsumerNativeTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcConsumerNativeTest.class);

    public static void main(String[] args) {
        RpcClient rpcClient = new RpcClient("117.72.33.162:2181", "zookeeper", "1.0.0", "default", "jdk", 3000, false, false);
        DemoService demoService = rpcClient.create(DemoService.class);
        String result = demoService.hello("World");
        LOGGER.info("RPC call result: {}", result);
        rpcClient.shutdown();
    }

    private RpcClient rpcClient;

    @BeforeEach
    public void initRpcClient() {
        rpcClient = new RpcClient("117.72.33.162:2181", "zookeeper", "1.0.0", "default", "jdk", 3000, false, false);
    }

    @Test
    public void testInterfaceRpc() {
        DemoService demoService = rpcClient.create(DemoService.class);
        String result = demoService.hello("World");
        LOGGER.info("RPC call result: {}", result);
        rpcClient.shutdown();
    }

    @Test
    public void testAsyncInterfaceRpc() throws Exception {
        IAsyncObjectProxy demoService = rpcClient.createAsync(DemoService.class);
        RpcFuture future = demoService.call("hello", "World");
        LOGGER.info("Async RPC call result: {}", future.get());
        rpcClient.shutdown();
    }
}
