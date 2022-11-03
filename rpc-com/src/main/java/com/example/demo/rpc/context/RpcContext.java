package com.example.demo.rpc.context;

import com.example.demo.netty.config.RpcSource;
import com.example.demo.netty.connect.NettyClient;
import com.example.demo.netty.connect.NettyServer;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

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
     * bean工厂上下文
     */
    private ConfigurableListableBeanFactory beanFactory;

    /**
     * 服务端对象
     */
    private NettyServer nettyServer;
    /**
     * 服务端对象
     */
    private NettyClient nettyClient;

    /**
     * 是否开启rpc
     */
    private Boolean enableRpc = false;

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
//
//    public ConfigurableApplicationContext getApplicationContext() {
//        return applicationContext;
//    }

//    public void setApplicationContext(ConfigurableApplicationContext applicationContext) {
//        this.applicationContext = applicationContext;
//    }

    public ConfigurableListableBeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setDefaultListableBeanFactory(ConfigurableListableBeanFactory defaultListableBeanFactory) {
        this.beanFactory = defaultListableBeanFactory;
    }

    public void setBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public NettyServer getNettyServer() {
        return nettyServer;
    }

    public void setNettyServer(NettyServer nettyServer) {
        this.nettyServer = nettyServer;
    }

    public NettyClient getNettyClient() {
        return nettyClient;
    }

    public void setNettyClient(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    public Boolean getEnableRpc() {
        return enableRpc;
    }

    public void setEnableRpc(Boolean enableRpc) {
        this.enableRpc = enableRpc;
    }
}
