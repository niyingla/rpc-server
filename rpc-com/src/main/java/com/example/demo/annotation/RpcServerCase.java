package com.example.demo.annotation;

import com.example.demo.inteface.ClientAspect;

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
    /**
     * 引入服务
     * @return
     */
    String serverName();

    /**
     * 代理类
     * @return
     */
    Class<? extends ClientAspect>[] proxyClass() default {};


    /**
     * 重试次数
     * @return
     */
    int retryCount() default 3;


}
