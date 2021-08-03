package com.example.demo.util;

import com.example.demo.dto.RpcRequestDto;
import com.example.demo.netty.result.FutureResult;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: demo
 * @description:
 * @author: xiaoye
 * @create: 2019-08-12 15:02
 **/
@Slf4j
public class ChannelUtils {

    /**
     * 发送远程请求
     *
     * @param channel
     * @param rpcRequestDto
     * @return
     */
    public static Object sendChannelRpcRequest(ChannelFuture channel, RpcRequestDto rpcRequestDto) {
        channel.channel().writeAndFlush(rpcRequestDto);
//        ReferenceCountUtil.release(rpcRequestDto);
        //等待结果
       return FutureResult.getResult(rpcRequestDto.getRequestId());
    }


}
