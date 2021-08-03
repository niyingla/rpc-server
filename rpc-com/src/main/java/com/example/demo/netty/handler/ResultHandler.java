
package com.example.demo.netty.handler;

import com.example.demo.dto.RpcRequestDto;
import com.example.demo.netty.result.FutureResult;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


public class ResultHandler extends ChannelInboundHandlerAdapter {

    /**
     * 接受远程请求 并响应结果
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof RpcRequestDto){
            RpcRequestDto requestDto = (RpcRequestDto) msg;
            FutureResult.putResult(requestDto.getRequestId(), requestDto.getResult());
        }
    }
}
