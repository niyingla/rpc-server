package com.example.demo.monad;

import redis.clients.jedis.Jedis;

/**
 * 执行方法函数接口
 */
@FunctionalInterface
public interface ExecuteRedisFunction {

    /**
     * 执行方法
     * @param resource
     */
    void apply(Jedis resource);

}
