package com.example.demo.netty.result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: demo
 * @description:
 * @author: xiaoye
 * @create: 2019-08-12 16:19
 **/
public class FutureResult {
    /**
     * 结果获取map 实际可以根据实例
     */
    private static ConcurrentHashMap<String, CompletableFuture> concurrentHashMap = new ConcurrentHashMap(512);

    /**
     * 获取结果
     *
     * @param requestId
     * @return
     */
    public static CompletableFuture getResult(String requestId) throws Exception {
        CompletableFuture future = new CompletableFuture();
        //创建结果包装类
        concurrentHashMap.put(requestId, future);
        //获取结果
        return future;
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

    /**
     * 完成请求后设置结果
     * @param requestId
     * @param result
     */
    public static void removeAndComplete(String requestId, Object result) {
        CompletableFuture future = concurrentHashMap.remove(requestId);
        future.complete(result);
    }
}
