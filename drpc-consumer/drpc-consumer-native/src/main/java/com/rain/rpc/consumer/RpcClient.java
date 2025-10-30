package com.rain.rpc.consumer;

import com.rain.rpc.consumer.common.RpcConsumer;
import com.rain.rpc.proxy.jdk.JdkProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcClient {

    private final Logger logger = LoggerFactory.getLogger(RpcClient.class);
    /**
     * Service version
     */
    private String serviceVersion;
    /**
     * Service group
     */
    private String serviceGroup;
    /**
     * Serialization type
     */
    private String serializationType;
    /**
     * Request timeout
     */
    private long timeout;

    /**
     * Whether to call asynchronously
     */
    private boolean async;

    /**
     * Whether to call one-way
     */
    private boolean oneway;

    public RpcClient(String serviceVersion, String serviceGroup, String serializationType, long timeout, boolean async, boolean oneway) {
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.serviceGroup = serviceGroup;
        this.serializationType = serializationType;
        this.async = async;
        this.oneway = oneway;
    }

    public <T> T create(Class<T> interfaceClass) {
        JdkProxyFactory<T> jdkProxyFactory = new JdkProxyFactory<T>(serviceVersion, serviceGroup, serializationType, timeout, RpcConsumer.getInstance(), async, oneway);
        return jdkProxyFactory.getProxy(interfaceClass);
    }

    public void shutdown() {
        RpcConsumer.getInstance().close();
    }
}
