package com.rain.rpc.registry.zookeeper;

import com.rain.rpc.common.helper.RpcServiceHelper;
import com.rain.rpc.loadbalancer.api.ServiceLoadBalancer;
import com.rain.rpc.loadbalancer.random.RandomServiceLoadBalancer;
import com.rain.rpc.protocol.meta.ServiceMeta;
import com.rain.rpc.registry.api.RegistryService;
import com.rain.rpc.registry.api.config.RegistryConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Zookeeper registry service implementation
 * Implements service registration and discovery based on Apache Zookeeper
 */
public class ZookeeperRegistryService implements RegistryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperRegistryService.class);

    public static final int BASE_SLEEP_TIME_MS = 1000;
    public static final int MAX_RETRIES = 3;
    private static final String ZK_REGISTRY_PATH = "/drpc";

    private ServiceDiscovery<ServiceMeta> serviceDiscovery;
    private ServiceLoadBalancer<ServiceInstance<ServiceMeta>> serviceLoadBalancer;

    @Override
    public void init(RegistryConfig registryConfig) throws Exception {
        LOGGER.info("Initializing Zookeeper registry service with address: {}", registryConfig.getRegistryAddress());
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                registryConfig.getRegistryAddress(),
                new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
        client.start();
        JsonInstanceSerializer<ServiceMeta> serializer = new JsonInstanceSerializer<>(ServiceMeta.class);
        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMeta.class)
                .client(client)
                .serializer(serializer)
                .basePath(ZK_REGISTRY_PATH)
                .build();
        this.serviceDiscovery.start();
        this.serviceLoadBalancer = new RandomServiceLoadBalancer<>();
        // TODO 默认创建基于随机算法的负载均衡策略，后续基于SPI扩展
        LOGGER.info("Zookeeper registry service initialized successfully");
    }

    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {
        LOGGER.info("Registering service: {}:{} with group: {} and version: {}", 
                serviceMeta.getServiceName(), serviceMeta.getServiceAddr(), 
                serviceMeta.getServiceGroup(), serviceMeta.getServiceVersion());
        
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance.<ServiceMeta>builder()
                .name(RpcServiceHelper.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion(), serviceMeta.getServiceGroup()))
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.registerService(serviceInstance);
        LOGGER.info("Service registered successfully: {}", serviceMeta.getServiceName());
    }

    @Override
    public void unRegister(ServiceMeta serviceMeta) throws Exception {
        LOGGER.info("Unregistering service: {}", serviceMeta.getServiceName());
        
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance.<ServiceMeta>builder()
                .name(serviceMeta.getServiceName())
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.unregisterService(serviceInstance);
        LOGGER.info("Service unregistered successfully: {}", serviceMeta.getServiceName());
    }

    @Override
    public ServiceMeta discovery(String serviceName, int invokerHashCode) throws Exception {
        LOGGER.debug("Discovering service: {}", serviceName);
        
        Collection<ServiceInstance<ServiceMeta>> serviceInstances = serviceDiscovery.queryForInstances(serviceName);
        ServiceInstance<ServiceMeta> instance = serviceLoadBalancer.select((List<ServiceInstance<ServiceMeta>>) serviceInstances, invokerHashCode);

        if (instance == null) {
            LOGGER.warn("No instance found for service: {}", serviceName);
            return null;
        }
        
        ServiceMeta serviceMeta = instance.getPayload();
        LOGGER.info("Service discovered successfully: {} at {}:{}", serviceName, serviceMeta.getServiceAddr(), serviceMeta.getServicePort());
        return serviceMeta;
    }

    @Override
    public void destroy() throws Exception {
        LOGGER.info("Destroying Zookeeper registry service");
        if (serviceDiscovery != null) {
            serviceDiscovery.close();
        }
        LOGGER.info("Zookeeper registry service destroyed successfully");
    }
}