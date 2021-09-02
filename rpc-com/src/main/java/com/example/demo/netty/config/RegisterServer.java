package com.example.demo.netty.config;

import com.example.demo.dto.ServerInfo;
import com.example.demo.rpc.context.RpcContext;
import com.example.demo.rpc.context.RpcServerPool;
import com.example.demo.util.CommonUtils;
import com.example.demo.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.UnknownHostException;
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

    private static Logger log = LoggerFactory.getLogger(RegisterServer.class.getName());

    /**
     * 注册当前服务定时任务
     */
    public static void register(RpcContext rpcContext) {
        log.debug("开始注册到服务列表");
        RpcSource rpcSource = rpcContext.getRpcSource();
        //创建服务信息对象
        ServerInfo serverInfo = new ServerInfo(rpcSource.getPort(), CommonUtils.getIp(), rpcSource.getServerName());
        //持续注册 每60s注册一次
        Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "schedule-register");
            thread.setDaemon(true);
            return thread;
        }).scheduleWithFixedDelay(() -> {
            try {
                RpcServerPool.registerServer(rpcSource.getNameSpace(), serverInfo);
            } catch (Exception e) {
                log.error("注册到服务列表失败", e);
            }
        }, 0L, 60L, TimeUnit.SECONDS);

        //发送redis注册消息
        RpcServerPool.sendRegisterMsg(rpcSource.getNameSpace(), serverInfo);
        //消费注册信息
        RpcServerPool.consumerRegisterMessage(rpcSource.getNameSpace());
    }
}
