package com.rain.rpc.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {

    /**
     * 接口类
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 完整的接口类名
     */
    String interfaceClassName() default "";

    String version() default "1.0.0";

    String group() default "";
}
