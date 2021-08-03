package com.example.demo.netty.result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @program: demo
 * @description:
 * @author: xiaoye
 * @create: 2019-08-12 16:19
 **/
public class FutureResultNew {

    public static ConcurrentHashMap<String, CompletableFuture> concurrentHashMap = new ConcurrentHashMap();

    /**
     * 获取结果
     *
     * @param requestId
     * @return
     */
    public static Object getResult(String requestId, CompletableFuture future) throws Exception {
        //创建结果包装类
        concurrentHashMap.put(requestId, future);
        //获取结果
        return future.get(5, TimeUnit.SECONDS);
    }

    /**
     * 放入结果
     *
     * @param requestId
     * @param result
     * @return
     */
    public static void putResult(String requestId, Object result) {
        CompletableFuture future = concurrentHashMap.get(requestId);
        future.complete(result);
    }
}
