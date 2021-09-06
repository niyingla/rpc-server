package com.example.demo.netty.config;

import com.example.demo.dto.ServerInfo;
import com.example.demo.monad.ExecuteRedisFunction;
import com.example.demo.rpc.context.RpcContext;
import com.example.demo.rpc.util.SpringUtil;
import com.example.demo.util.CommonUtils;
import com.example.demo.util.SerializiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 注册
 * <p> RegirterServerAware </p>
 *
 * @author xiaoye
 * @version 1.0
 * @date 2021/8/3 9:55
 */
public class RegisterServer {

    private static RegisterServer registerServer;

    private static Logger log = LoggerFactory.getLogger(RegisterServer.class.getName());
    /**
     * redis 注册地址前缀
     */
    public static final String SERVER_PRE = "server:pre:";

    /**
     * redis 注册地址前缀
     */
    public static final String REGISTER_PRE = "register:pre:";

    /**
     * 注册当前服务定时任务
     */
    public static void registerAndSubscribe(RpcContext rpcContext) {
        if (registerServer != null) {
            return;
        }
        log.debug("开始注册到服务列表");
        RpcSource rpcSource = rpcContext.getRpcSource();
        registerServer = new RegisterServer();
        //创建服务信息对象
        ServerInfo serverInfo = new ServerInfo(rpcSource.getPort(), CommonUtils.getIp(), rpcSource.getServerName());
        //注册定时任务
        registerServer.registerTask(rpcSource, serverInfo);
        //发送redis注册消息
        registerServer.sendRegisterMsg(rpcSource.getNameSpace(), serverInfo);
        //消费注册信息
        registerServer.consumerRegisterMessage(rpcSource.getNameSpace());
    }

    /**
     * 定时注册任务
     * @param rpcSource
     * @param serverInfo
     */
    private void registerTask(RpcSource rpcSource, ServerInfo serverInfo) {
        //持续注册 每60s注册一次
        Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "schedule-register");
            thread.setDaemon(true);
            return thread;
        }).scheduleWithFixedDelay(() -> {
            try {
                //注册服务
                registerServer(rpcSource.getNameSpace(), serverInfo);
            } catch (Exception e) {
                log.error("注册到服务列表失败", e);
            }
        }, 0L, 60L, TimeUnit.SECONDS);
    }
    /**
     * 注册服务
     *
     * @param nameSpace
     * @param serverInfo
     */
    private void registerServer(String nameSpace, ServerInfo serverInfo) {
        String key = SERVER_PRE + nameSpace + serverInfo.getServerName() + ":" + serverInfo.getIp() + ":" + serverInfo.getPort();
        //设置注册信息 90s失效
        execRedisFunction(resource -> resource.setex(key.getBytes(), 90, SerializiUtil.serialize(serverInfo)));
    }


    /**
     * 发送开始注册信息
     *
     * @param nameSpace
     * @param serverInfo
     */
    private void sendRegisterMsg(String nameSpace, ServerInfo serverInfo) {
        String key = REGISTER_PRE + nameSpace;
        execRedisFunction(resource -> resource.publish(key, SerializiUtil.toBase64String(serverInfo)));
    }


    /**
     * 执行redis方法
     */
    public static void execRedisFunction(ExecuteRedisFunction redisFunction) {
        Jedis resource = null;
        try {
            resource = SpringUtil.getBean(JedisPool.class).getResource();
            redisFunction.apply(resource);
        } finally {
            if (resource != null) {
                resource.close();
            }
        }
    }
    /**
     * 消费注册消息
     *
     * @param nameSpace
     */
    private void consumerRegisterMessage(String nameSpace) {
        new Thread(() -> {
            String key = REGISTER_PRE + nameSpace;
            Jedis resource = SpringUtil.getBean(JedisPool.class).getResource();
            resource.subscribe(new RegisterPubMsgSub(), key);
        }).start();
    }
}
