package com.example.demo.netty.config;

import com.example.demo.netty.connect.NettyServer;
import com.example.demo.rpc.context.RpcContext;
import com.example.demo.rpc.context.RpcServerPool;
import com.example.demo.rpc.factory.StartFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;

import java.net.Inet4Address;
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
  public static void register( RpcContext rpcContext ) {
      log.debug("开始注册到服务列表");
      //持续注册 每60s注册一次
      Executors.newSingleThreadScheduledExecutor(r -> {
          Thread thread = new Thread(r, "schedule-register");
          thread.setDaemon(true);
          return thread;
      }).scheduleWithFixedDelay(() -> {
          try {
              RpcSource rpcSource = rpcContext.getRpcSource();
              RpcServerPool.registerServer(rpcSource.getServerName(), Inet4Address.getLocalHost().getHostAddress(), rpcSource.getPort());
          } catch (Exception e) {
              log.error("注册到服务列表失败", e);
          }
      }, 0L, 60L, TimeUnit.SECONDS);
  }
}
