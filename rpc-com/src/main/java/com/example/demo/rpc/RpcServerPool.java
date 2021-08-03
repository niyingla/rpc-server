package com.example.demo.rpc;

import com.example.demo.dto.RpcServerDto;
import com.example.demo.netty.connect.NettyClient;
import com.example.demo.rpc.util.RpcClient;
import com.example.demo.rpc.util.SpringUtil;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pikaqiu
 */
public class RpcServerPool  {

    static Logger log = LoggerFactory.getLogger(RpcClient.class.getName());

    private final Map<String, RpcServerDto> serverDtoMap = new HashMap<>();

    private static Map<String, List<NettyClient>> channelMap = new HashMap<>();


    private static String serverPre = "server:pre:";

    public static void registerServer(String serverName, String ip, int port) {
        Jedis resource = SpringUtil.getBean(JedisPool.class).getResource();
        resource.hset(serverPre + serverName, ip, port + "");
        resource.expire(serverPre + serverName, 5 * 60 * 60);
    }



    /**
     * 初始化所有连接
     */
    public void initAllConnect() {
        //根据注册列表 获取redis中存的 ip和端口
        loadServer();

        for (String serverName : serverDtoMap.keySet()) {
            RpcServerDto rpcServerDto = serverDtoMap.get(serverName);
            for (RpcServerDto.Example example : rpcServerDto.getExamples()) {
                //循环创建连接
                log.info("创建连接 服务: {}：ip: {} ,port: {}", serverName, example.getIp(), example.getPort());
                NettyClient nettyClient = new NettyClient();
                nettyClient.initClient().createConnect(2, example.getIp(), example.getPort());

                List<NettyClient> nettyClients = channelMap.get(serverName);

                if (nettyClients == null) {
                    nettyClients = new ArrayList<>();
                    channelMap.put(serverName, nettyClients);
                }
                nettyClients.add(nettyClient);
            }
        }
    }

    /**
     * 获取一个连接
     *
     * @return
     */
    public static ChannelFuture getChannelByServerName(String serverName) {
        //随机获取一个连接
        List<NettyClient> nettyClients = channelMap.get(serverName);
        ChannelFuture channelFuture = null;
        for (; nettyClients.size() > 0; ) {
            channelFuture = nettyClients.get((int) (Math.random() * (nettyClients.size()))).getChannelFuture();

            if (channelFuture != null && channelFuture.channel().isActive()) {
                break;
            } else {
                nettyClients.remove(channelFuture);
            }
        }
        return channelFuture;
    }


    /**
     * 添加服务名和参数
     *
     * @param serverName
     * @param ip
     * @param port
     * @return
     */
    public void serverAdd(String serverName, String ip, int port) {
        RpcServerDto serverDto = serverDtoMap.get(serverName);
        if (serverDto == null) {
            serverDto = new RpcServerDto(serverName);
        }
        serverDto.addExample(ip, port);
        serverDtoMap.put(serverName, serverDto);
    }

    /**
     * 添加服务名字
     *
     * @param serverName
     */
    public void addServerName(String serverName) {
        RpcServerDto serverDto = serverDtoMap.get(serverName);
        if (serverDto == null) {
            serverDto = new RpcServerDto(serverName);
        }
        serverDtoMap.put(serverName, serverDto);
    }

    /**
     * 加载所有服务
     */
    public void loadServer() {
        for (String serverName : serverDtoMap.keySet()) {
            Jedis resource = SpringUtil.getBean(JedisPool.class).getResource();
            Map<String, String> serverMap = resource.hgetAll(serverPre + serverName);
            for (Map.Entry<String, String> entry : serverMap.entrySet()) {
                serverAdd(serverName, entry.getKey(), Integer.valueOf(entry.getValue()));
            }
        }
    }

}
