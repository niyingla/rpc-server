package com.example.demo.netty.config;

import com.example.demo.netty.connect.NettyServer;
import com.example.demo.rpc.RpcServerPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

import java.net.Inet4Address;

/**
 * <p> RegirterServerAware </p>
 *
 * @author xiaoye
 * @version 1.0
 * @date 2021/8/3 9:55
 */
public class RegisterServerListener implements ApplicationListener<ApplicationStartedEvent> {

  static Logger log = LoggerFactory.getLogger(RegisterServerListener.class.getName());

  @Override
  public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
    try {
      log.info("开始注册到服务列表");
      // 注册到服务列表
      String serverName = applicationStartedEvent.getApplicationContext().getEnvironment().getProperty("spring.application.name");
      RpcServerPool.registerServer(serverName, Inet4Address.getLocalHost().getHostAddress(), NettyServer.getPort());
    } catch (Exception e) {
      log.error("注册到服务列表失败", e);
    }
  }
}
