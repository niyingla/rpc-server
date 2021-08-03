package com.example.demo.netty.config;

import com.example.demo.netty.connect.NettyServer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(-2)
@Component
public class ApplicationStartListener implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        NettyServer.start();
    }
}
