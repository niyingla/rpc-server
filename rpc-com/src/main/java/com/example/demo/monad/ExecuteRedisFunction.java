package com.example.demo.monad;

import redis.clients.jedis.Jedis;

@FunctionalInterface
public interface ExecuteRedisFunction {

    void apply(Jedis resource);

}
