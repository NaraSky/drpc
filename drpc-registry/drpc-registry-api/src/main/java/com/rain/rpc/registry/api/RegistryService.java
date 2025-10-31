package com.rain.rpc.registry.api;

import com.rain.rpc.protocol.meta.ServiceMeta;
import com.rain.rpc.registry.api.config.RegistryConfig;

import java.rmi.registry.Registry;

public interface RegistryService {

    void register(ServiceMeta serviceMeta) throws Exception;

    void unRegister(ServiceMeta serviceMeta) throws Exception;

    ServiceMeta discovery(String serviceName, int invokerHashCode) throws Exception;

    void destroy() throws Exception;

    default void init(RegistryConfig registryConfig) throws Exception {
    }
}