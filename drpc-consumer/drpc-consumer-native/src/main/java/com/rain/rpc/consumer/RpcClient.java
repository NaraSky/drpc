package com.rain.rpc.consumer;

import com.rain.rpc.common.exception.RegistryException;
import com.rain.rpc.consumer.common.RpcConsumer;
import com.rain.rpc.proxy.api.ProxyFactory;
import com.rain.rpc.proxy.api.async.IAsyncObjectProxy;
import com.rain.rpc.proxy.api.config.ProxyConfig;
import com.rain.rpc.proxy.api.object.ObjectProxy;
import com.rain.rpc.proxy.jdk.JdkProxyFactory;
import com.rain.rpc.registry.api.RegistryService;
import com.rain.rpc.registry.api.config.RegistryConfig;
import com.rain.rpc.registry.zookeeper.ZookeeperRegistryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcClient {

    private final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    // Registry service for service discovery
    private RegistryService registryService;

    // Service version
    private String serviceVersion;
    // Service group
    private String serviceGroup;
    // Serialization type
    private String serializationType;
    // Timeout in milliseconds
    private long timeout;

    // Enable async invocation
    private boolean async;

    // Enable oneway invocation
    private boolean oneway;

    public RpcClient(String registryAddress, String registryType, String serviceVersion, String serviceGroup, String serializationType, long timeout, boolean async, boolean oneway) {
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.serviceGroup = serviceGroup;
        this.serializationType = serializationType;
        this.async = async;
        this.oneway = oneway;
        this.registryService = this.getRegistryService(registryAddress, registryType);
    }

    private RegistryService getRegistryService(String registryAddress, String registryType) {
        if (StringUtils.isEmpty(registryType)) {
            throw new IllegalArgumentException("registry type is null");
        }
        // TODO: Support SPI extension for multiple registry types
        RegistryService registryService = new ZookeeperRegistryService();
        try {
            registryService.init(new RegistryConfig(registryAddress, registryType));
        } catch (Exception e) {
            logger.error("RpcClient init registry service throws exception:{}", e);
            throw new RegistryException(e.getMessage(), e);
        }
        return registryService;
    }

    public <T> T create(Class<T> interfaceClass) {
        ProxyFactory proxyFactory = new JdkProxyFactory<T>();
        proxyFactory.init(new ProxyConfig(interfaceClass, serviceVersion, serviceGroup, serializationType, timeout, registryService, RpcConsumer.getInstance(), async, oneway));
        return proxyFactory.getProxy(interfaceClass);
    }

    public <T> IAsyncObjectProxy createAsync(Class<T> interfaceClass) {
        return new ObjectProxy<T>(interfaceClass, serviceVersion, serviceGroup, serializationType, timeout, registryService, RpcConsumer.getInstance(), async, oneway);
    }

    public void shutdown() {
        RpcConsumer.getInstance().close();
    }
}
