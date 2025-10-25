package com.rain.rpc.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {
    String version() default "1.0.0";

    String registryType() default "zookeeper";

    String registryAddr() default "127.0.0.1:2181";

    String loadBalanceType() default "random";

    String serializationType() default "protostuff";

    long timeout() default 5000;

    boolean async() default false;

    boolean oneway() default false;

    String proxy() default "jdk";

    String group() default "default";
}
