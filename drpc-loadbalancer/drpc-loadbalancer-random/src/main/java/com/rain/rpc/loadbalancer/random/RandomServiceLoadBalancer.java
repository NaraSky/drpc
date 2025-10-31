package com.rain.rpc.loadbalancer.random;

import com.rain.rpc.loadbalancer.api.ServiceLoadBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

public class RandomServiceLoadBalancer<T> implements ServiceLoadBalancer<T> {

    private final Logger logger = LoggerFactory.getLogger(RandomServiceLoadBalancer.class);

    @Override
    public T select(List<T> serviceInstances, int hashCode) {
        logger.debug("Selecting service instance using random load balancer");
        if (serviceInstances == null || serviceInstances.size() == 0) {
            return null;
        }
        Random random = new Random();
        return serviceInstances.get(random.nextInt(serviceInstances.size()));
    }
}
