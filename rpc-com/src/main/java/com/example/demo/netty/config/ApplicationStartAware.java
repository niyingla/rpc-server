//package com.example.demo.netty.config;
//
//import com.example.demo.netty.connect.NettyServer;
//import org.springframework.beans.BeansException;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
///**
// * @author huohua
// */
//@Component
//@Order(-2)
//public class ApplicationStartAware implements ApplicationContextAware {
//
//    /**
//     * 拉起服务端
//     * @param applicationContext
//     * @throws BeansException
//     */
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        new Thread(() -> NettyServer.start()).start();
//    }
//}
