package com.rain.rpc.proxy.jdk;

import com.rain.rpc.proxy.api.BaseProxyFactory;
import com.rain.rpc.proxy.api.ProxyFactory;

import java.lang.reflect.Proxy;

public class JdkProxyFactory<T> extends BaseProxyFactory<T> implements ProxyFactory {
    @Override
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                objectProxy
        );
    }
}
