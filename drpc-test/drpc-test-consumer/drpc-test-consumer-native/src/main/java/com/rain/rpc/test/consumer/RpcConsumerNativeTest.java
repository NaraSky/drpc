package com.rain.rpc.test.consumer;

import com.rain.rpc.consumer.RpcClient;
import com.rain.rpc.proxy.api.async.IAsyncObjectProxy;
import com.rain.rpc.proxy.api.future.RpcFuture;
import com.rain.rpc.test.api.DemoService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcConsumerNativeTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcConsumerNativeTest.class);

    public static void main(String[] args) {
        RpcClient rpcClient = new RpcClient("1.0.0", "default", "cglib", 3000, false, false);
        DemoService demoService = rpcClient.create(DemoService.class);
        String result = demoService.hello("rain");
        LOGGER.info("Result: {}", result);
        rpcClient.shutdown();
    }

    @Test
    public void testAsyncInterfaceRpc() throws Exception {
        RpcClient rpcClient = new RpcClient("1.0.0", "default", "cglib", 3000, false, false);
        IAsyncObjectProxy demoService = rpcClient.createAsync(DemoService.class);
        RpcFuture future = demoService.call("hello", "rain");
        LOGGER.info("Result: {}", future.get());
        rpcClient.shutdown();
    }
}
