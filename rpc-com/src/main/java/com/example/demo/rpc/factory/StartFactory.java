package com.example.demo.rpc.factory;

import com.example.demo.annotation.RpcServerCase;
import com.example.demo.factory.ProxyFactory;
import com.example.demo.rpc.RpcServerPool;
import com.example.demo.util.ScannerUtils;
import com.example.demo.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author pikaqiu
 */
@Order(-1)
@Component
public class StartFactory implements ApplicationListener<ApplicationStartedEvent> {

    /**
     * 需要加載实例列表和服务列表
     */
    private Map<Class, RpcServerCase> rpcInterFace = ScannerUtils.getAnnotations(RpcServerCase.class, "com.example.demo");

    @Autowired
    private DefaultListableBeanFactory defaultListableBeanFactory;

    /**
     * 根据接口生成代理对象
     * @param interfaceServer
     */
    public <T> void setBean(Class<T> interfaceServer) {
        //生成代理对象
        T proxyObject = ProxyFactory.getInterfaceInfo(interfaceServer);
        //注册对象到spring
        defaultListableBeanFactory.registerSingleton(StringUtils.lowerFirst(interfaceServer.getSimpleName()), proxyObject);
    }


    /**
     * 通过扫描获取所有rpc代理类
     */
    public void autoWiredRpcProxy() {
        //初始连接池
        RpcServerPool rpcServerPool = RpcServerPool.getInstance();
        //循环注入代理对象到spring 并将调用服务写到列表
        for (Map.Entry<Class, RpcServerCase> entry : rpcInterFace.entrySet()) {
            //设置代理对象
            setBean(entry.getKey());
            //写入连接服务列表
            rpcServerPool.addServerName(entry.getValue().serverName());
        }
        //开始链接
        rpcServerPool.initAllConnect();
    }

    /**
     * 启动客户端服务
     * @param applicationEvent
     */
    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationEvent) {
        autoWiredRpcProxy();
    }
}
