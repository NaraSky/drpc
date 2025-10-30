package com.rain.rpc.proxy.jdk;

import com.rain.rpc.proxy.api.consumer.Consumer;
import com.rain.rpc.proxy.api.object.ObjectProxy;

import java.lang.reflect.Proxy;

public class JdkProxyFactory<T> {
    /**
     * Service version
     */
    private String serviceVersion;
    /**
     * Service group
     */
    private String serviceGroup;
    /**
     * Request timeout in milliseconds, default 15s
     */
    private long timeout = 15000;
    /**
     * Service consumer
     */
    private Consumer consumer;
    /**
     * Serialization type
     */
    private String serializationType;

    /**
     * Whether to call asynchronously
     */
    private boolean async;

    /**
     * Whether to call one-way
     */
    private boolean oneway;


    public JdkProxyFactory(String serviceVersion, String serviceGroup, String serializationType, long timeout, Consumer consumer, boolean async, boolean oneway) {
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.serviceGroup = serviceGroup;
        this.consumer = consumer;
        this.serializationType = serializationType;
        this.async = async;
        this.oneway = oneway;
    }


    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                new ObjectProxy<T>(clazz, serviceVersion, serviceGroup, serializationType, timeout, consumer, async, oneway));
    }
}
