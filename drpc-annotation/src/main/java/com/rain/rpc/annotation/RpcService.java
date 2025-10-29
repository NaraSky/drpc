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
     * Interface class
     */
    Class<?> interfaceClass() default void.class;

    /**
     * Full interface class name
     */
    String interfaceClassName() default "";

    String version() default "1.0.0";

    String group() default "";
}