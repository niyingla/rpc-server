package com.example.demo.rpc.factory;

import com.example.demo.annotation.RpcServerCase;
import com.example.demo.dto.RpcRequestDto;
import com.example.demo.inteface.Aspect;
import com.example.demo.rpc.util.RpcClient;
import com.example.demo.rpc.util.SpringUtil;
import org.springframework.core.annotation.Order;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: demo
 * @description: 代理工厂实例
 * @author: xiaoye
 * @create: 2019-09-09 11:50
 **/


public class RpcFactory<T> implements InvocationHandler {

    private Class<T> rpcInterface;

    private Class[] proxyClasss;

    private List<Aspect> aspectInsts;

    public RpcFactory(Class<T> rpcInterface) {
        this.rpcInterface = rpcInterface;
        //参数对象转换成能字节  远程调用
        RpcServerCase rpcServerCase = rpcInterface.getAnnotation(RpcServerCase.class);
        proxyClasss = rpcServerCase.proxyClass();
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
        //构建请求信息
        RpcRequestDto rpcRequestDto = new RpcRequestDto(UUID.randomUUID().toString(), rpcInterface.getName(), method.getName(), args);
        //执行切面方法
        List<Aspect> aspectInsts = getAspects(proxyClasss);
        //切面前置请求
        for (Aspect aspect : aspectInsts) {
            aspect.before(rpcRequestDto);
        }
        //发起rpc远程请求
        Object result = RpcClient.sendRpcRequest(rpcInterface, rpcRequestDto);

        //切面后置请求
        for (Aspect aspect : aspectInsts) {
            aspect.after(rpcRequestDto);
        }
        return result;
    }

    /**
     * 获取切面对象
     *
     * @param proxyClasss
     * @return
     */
    private List<Aspect> getAspects(Class[] proxyClasss) {
        if (aspectInsts != null) {
            return aspectInsts;
        }
        this.aspectInsts = Arrays.stream(proxyClasss)
            //循环获取切面类对象
            .map(proxyClass -> {
                Aspect aspect = (Aspect) SpringUtil.getBeanOrNull(proxyClass);
                if (aspect == null) {
                    try {
                        return (Aspect) proxyClass.newInstance();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                return aspect;
            }).filter(Objects::nonNull)
            //注解排序
            .sorted(Comparator.comparing(item -> {
                Order annotation = item.getClass().getAnnotation(Order.class);
                return annotation != null ? annotation.value() : Integer.MAX_VALUE;
            })).collect(Collectors.toList());

        return aspectInsts;
    }

}
