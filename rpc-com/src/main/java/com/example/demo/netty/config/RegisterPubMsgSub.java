package com.example.demo.netty.config;

import com.example.demo.dto.ServerInfo;
import com.example.demo.rpc.context.RpcServerPool;
import com.example.demo.rpc.factory.StartFactory;
import com.example.demo.util.SerializiUtil;
import redis.clients.jedis.JedisPubSub;

/**
 * @author huohua
 */
public class RegisterPubMsgSub extends JedisPubSub {
    /** JedisPubSub类是一个没有抽象方法的抽象类,里面方法都是一些空实现
     * 所以可以选择需要的方法覆盖,这儿使用的是SUBSCRIBE指令，所以覆盖了onMessage
     * 如果使用PSUBSCRIBE指令，则覆盖onPMessage方法
     * 当然也可以选择BinaryJedisPubSub,同样是抽象类，但方法参数为byte[]
     **/
    @Override
    public void onMessage(String channel, String message) {
        //接收到exit消息后退出
        Object msg = SerializiUtil.Base64ToObj(message);
        if (msg instanceof ServerInfo) {
            ServerInfo serverInfo = (ServerInfo) msg;
            //获取连接池
            RpcServerPool rpcServerPool = StartFactory.getRpcContext().getRpcServerPool();
            //连接服务
            rpcServerPool.initiativeConnectServer(serverInfo.getIp(), serverInfo.getPort(), serverInfo.getServerName());
        }
    }
}
