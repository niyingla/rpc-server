package com.example.demo.util;

import com.example.demo.dto.RpcRequestDto;
import com.example.demo.netty.connect.NettyClient;
import com.example.demo.netty.result.FutureResultNew;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @program: demo
 * @description:
 * @author: xiaoye
 * @create: 2019-08-12 15:02
 **/
public class ChannelUtils {

  static Logger log = LoggerFactory.getLogger(NettyClient.class.getName());

  /**
   * 发送远程请求方法
   *
   * @param channel
   * @param rpcRequestDto
   * @return
   */
  public static Object sendChannelRpcRequest(ChannelFuture channel, RpcRequestDto rpcRequestDto, int retryCount) {
    try {
      //写入结果结合
      CompletableFuture result = FutureResultNew.getResult(rpcRequestDto.getRequestId());
      //发送通道数据
      channel.channel().writeAndFlush(rpcRequestDto);
      //等待结果 (5s)
      return result.get(5, TimeUnit.SECONDS);
    } catch (Exception e) {
      log.error("获取结果报错：", e);
      //还有剩余重试次数
      if (retryCount > 0) {
        //进行重试
        return sendChannelRpcRequest(channel, rpcRequestDto, --retryCount);
      } else {
        //没有次数直接报错
        throw new RuntimeException("获取结果报错");
      }
    }
  }


}
