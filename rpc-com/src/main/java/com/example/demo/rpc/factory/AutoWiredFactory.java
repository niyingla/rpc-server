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
public class AutoWiredFactory implements ApplicationListener<ApplicationStartedEvent> {

    private RpcServerPool rpcServerPool;

    /**
     * 需要加載实例列表
     */
    private Map<Class, RpcServerCase> rpcInterFace = ScannerUtils.getAnnotations(RpcServerCase.class, "com.example.demo");

    @Autowired
    private DefaultListableBeanFactory defaultListableBeanFactory;

    public void setBean(Class interfaceServer) {
        ProxyFactory proxyFactory = new ProxyFactory();
        Object interfaceInfo = proxyFactory.getInterfaceInfo(interfaceServer);
        defaultListableBeanFactory.registerSingleton(StringUtils.lowerFirst(interfaceServer.getSimpleName()), interfaceInfo);
    }


    /**
     * 通过扫描获取所有rpc代理类
     */
    public void autoWiredRpcProxy() {
        //初始连接池
        rpcServerPool = new RpcServerPool();

        for (Map.Entry<Class, RpcServerCase> entry : rpcInterFace.entrySet()) {
            //设置代理对象
            setBean(entry.getKey());
            //写入连接服务列表
            rpcServerPool.addServerName(entry.getValue().serverName());
        }
        //开始链接
        this.rpcServerPool.initAllConnect();
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationEvent) {
        autoWiredRpcProxy();
    }

    public RpcServerPool getRpcServerPool() {
        return rpcServerPool;
    }
}
