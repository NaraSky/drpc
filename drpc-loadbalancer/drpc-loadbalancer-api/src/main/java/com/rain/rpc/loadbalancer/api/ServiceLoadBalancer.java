package com.rain.rpc.loadbalancer.api;

import java.util.List;

public interface ServiceLoadBalancer<T> {

    T select(List<T> serviceInstances, int hashCode);
}
