package com.rain.rpc.test.registry;

import com.rain.rpc.protocol.meta.ServiceMeta;
import com.rain.rpc.registry.api.RegistryService;
import com.rain.rpc.registry.api.config.RegistryConfig;
import com.rain.rpc.registry.zookeeper.ZookeeperRegistryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Zookeeper registry service test
 * Tests the functionality of service registration, discovery and deregistration
 */
public class ZookeeperRegistryTest {
    private RegistryService registryService;

    private ServiceMeta serviceMeta;

    @BeforeEach
    public void init() throws Exception{
        RegistryConfig registryConfig = new RegistryConfig("117.72.33.162:2181", "zookeeper");
        this.registryService = new ZookeeperRegistryService();
        this.registryService.init(registryConfig);
        this.serviceMeta = new ServiceMeta(
                ZookeeperRegistryTest.class.getName(), "1.0.0", "117.72.33.162", 2181, "default");
    }

    @Test
    public void testRegister() throws Exception {
        this.registryService.register(serviceMeta);
    }

    @Test
    public void testUnRegister() throws Exception {
        this.registryService.unRegister(serviceMeta);
    }

    @Test
    public void testDiscovery() throws Exception {
        this.registryService.discovery(RegistryService.class.getName(), "rain".hashCode());
    }

    @Test
    public void testDestroy() throws Exception {
        this.registryService.destroy();
    }
}