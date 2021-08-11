package com.example.demo.rpc.context;

import com.example.demo.netty.config.RpcSource;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

public class RpcContext {

    /**
     * 客户端链接信息
     */
    private RpcServerPool rpcServerPool;

    /**
     * 配置信息
     */
    private RpcSource rpcSource;

    /**
     *
     */
    private ConfigurableApplicationContext applicationContext;

    /**
     * bean工厂上下文
     */
    private ConfigurableListableBeanFactory beanFactory;

    public RpcServerPool getRpcServerPool() {
        return rpcServerPool;
    }

    public void setRpcServerPool(RpcServerPool rpcServerPool) {
        this.rpcServerPool = rpcServerPool;
    }

    public RpcSource getRpcSource() {
        return rpcSource;
    }

    public void setRpcSource(RpcSource rpcSource) {
        this.rpcSource = rpcSource;
    }

    public ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ConfigurableListableBeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setDefaultListableBeanFactory(ConfigurableListableBeanFactory defaultListableBeanFactory) {
        this.beanFactory = defaultListableBeanFactory;
    }
}
