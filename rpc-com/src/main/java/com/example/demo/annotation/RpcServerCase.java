package com.example.demo.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author pikaqiu
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface RpcServerCase {
    String serverName();
}
