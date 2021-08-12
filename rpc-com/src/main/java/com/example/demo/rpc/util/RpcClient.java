package com.example.demo.rpc.util;

import com.alibaba.fastjson.JSON;
import com.example.demo.annotation.RpcServerCase;
import com.example.demo.dto.RpcRequestDto;
import com.example.demo.inteface.Aspect;
import com.example.demo.monad.Try;
import com.example.demo.rpc.context.RpcServerPool;
import com.example.demo.util.ChannelUtils;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 远程调用服务客户端类
 *
 * @author pikaqiu
 */
@Component
public class RpcClient {

    static Logger log = LoggerFactory.getLogger(RpcClient.class.getName());


    /**
     * 根据注解类信息
     * 发送目标服务 rpc请求
     *
     * @param interfaceClass
     * @param rpcRequestDto
     * @return
     */
    public static Object sendRpcRequest(Class interfaceClass, RpcRequestDto rpcRequestDto) {
        //参数对象转换成能字节  远程调用
        RpcServerCase rpcServerCase = (RpcServerCase) interfaceClass.getAnnotation(RpcServerCase.class);
        //获取请求channel
        ChannelFuture channel = RpcServerPool.getInstance().getChannelByServerName(rpcServerCase.serverName());
        if (channel == null) {
            throw new RuntimeException("服务不存在");
        }
        log.info("发起远程请求 请求目标服务：{} 目标方法：{}.{} 参数:{}", rpcServerCase.serverName(),
                interfaceClass.getName(), rpcRequestDto.getMethodName(), JSON.toJSONString(rpcRequestDto.getArgs()));
        //发送请求
        return ChannelUtils.sendChannelRpcRequest(channel, rpcRequestDto);
    }


}

