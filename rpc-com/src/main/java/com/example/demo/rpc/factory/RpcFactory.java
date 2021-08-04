package com.example.demo.rpc.factory;

import com.example.demo.rpc.util.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @program: demo
 * @description: 代理工厂实例
 * @author: xiaoye
 * @create: 2019-09-09 11:50
 **/


public class RpcFactory<T> implements InvocationHandler {

    private Class<T> rpcInterface;

    public RpcFactory(Class<T>  rpcInterface) {
        this.rpcInterface = rpcInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //判断是否是接口自定义方法
        Method[] declaredMethods = rpcInterface.getDeclaredMethods();
        if (Arrays.asList(declaredMethods).indexOf(method) < 0) {
            try {
                return method.invoke(this, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //发起rpc远程请求
        return RpcClient.sendRpcRequest(rpcInterface, method.getName(), args);
    }
}
