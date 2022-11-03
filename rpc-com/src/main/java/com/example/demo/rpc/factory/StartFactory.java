package com.example.demo.rpc.factory;

import com.example.demo.annotation.RpcServerCase;
import com.example.demo.factory.ProxyFactory;
import com.example.demo.netty.config.RegisterServer;
import com.example.demo.netty.config.RpcSource;
import com.example.demo.netty.connect.NettyClient;
import com.example.demo.netty.connect.NettyServer;
import com.example.demo.rpc.context.RpcContext;
import com.example.demo.rpc.context.RpcServerPool;
import com.example.demo.util.LocalStringUtils;
import com.example.demo.util.ScannerUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;

import java.util.Map;

/**
 * @author pikaqiu
 */
@Order(-1)
public class StartFactory implements ApplicationListener<ApplicationStartedEvent> {


    /**
     * 当前上下文
     */
    private final static RpcContext rpcContext = new RpcContext();

    /**
     * 根据接口生成代理对象
     *
     * @param interfaceServer
     */
    public <T> void setBean(RpcContext rpcContext, Class<T> interfaceServer) {
        //生成代理对象
        T proxyObject = ProxyFactory.getInterfaceProxy(interfaceServer);
        //注册对象到spring
        rpcContext.getBeanFactory().registerSingleton(LocalStringUtils.lowerFirst(interfaceServer.getSimpleName()), proxyObject);
    }


    /**
     * 通过扫描获取所有rpc代理类
     */
    public synchronized void registerRpcServer(RpcContext rpcContext, String classPtah) {
        RpcServerPool rpcServerPool = rpcContext.getRpcServerPool();
        // 需要加載实例列表和服务列表
        Map<Class, RpcServerCase> rpcInterFace = ScannerUtils.getAnnotations(RpcServerCase.class, classPtah);
        //循环注入代理对象到spring 并将调用服务写到列表
        for (Map.Entry<Class, RpcServerCase> entry : rpcInterFace.entrySet()) {
            //设置代理对象
            setBean(rpcContext, entry.getKey());
            //写入连接服务列表
            rpcServerPool.addServerName(entry.getValue().serverName());
        }
    }

    /**
     * 启动客户端服务
     *
     * @param applicationEvent
     */
    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationEvent) {
        if(!rpcContext.getEnableRpc()){
            return;
        }
        //1 设置上下文
        setContext(applicationEvent);
        //2 启动客户端
        NettyServer.start(rpcContext);
        //3 发起服务注册
        RegisterServer.registerAndSubscribe(rpcContext);
    }

    /**
     * 设置spring上下文
     * @param applicationEvent
     */
    private void setContext(ApplicationStartedEvent applicationEvent) {
        ConfigurableApplicationContext applicationContext = applicationEvent.getApplicationContext();
        RpcSource rpcSource = applicationContext.getBean(RpcSource.class);
        //1.1 设置服务名
        rpcSource.setServerName(StringUtils.isBlank(rpcSource.getServerName()) ?
            applicationContext.getEnvironment().getProperty("spring.application.name") : rpcSource.getServerName());
        //2 注入配置
        rpcContext.setRpcSource(rpcSource);
        rpcContext.setApplicationContext(applicationEvent.getApplicationContext());
    }

    /**
     * 设置对象到上下文
     *
     * @param registry
     */
    public RpcContext setContextBean(ConfigurableListableBeanFactory registry) {
        //开启rpc
        rpcContext.setEnableRpc(Boolean.TRUE);
        //设置bean工厂上下文
        rpcContext.setDefaultListableBeanFactory(registry);
        //初始连接池
        rpcContext.setRpcServerPool(new RpcServerPool(rpcContext));
        //初始化粗护垫对象
        rpcContext.setNettyClient(NettyClient.geInstance());
        return rpcContext;
    }

    public static RpcContext getRpcContext() {
        return rpcContext;
    }
}
