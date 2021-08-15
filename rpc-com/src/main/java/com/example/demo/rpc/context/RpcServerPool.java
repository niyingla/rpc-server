package com.example.demo.rpc.context;

import com.example.demo.collection.ArrayListMultimap;
import com.example.demo.dto.RpcServerDto;
import com.example.demo.dto.ServerDto;
import com.example.demo.netty.config.RpcSource;
import com.example.demo.netty.connect.NettyClient;
import com.example.demo.rpc.factory.StartFactory;
import com.example.demo.rpc.util.RpcClient;
import com.example.demo.rpc.util.SpringUtil;
import com.example.demo.util.RedisUtil;
import io.netty.channel.ChannelFuture;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author pikaqiu
 */
public class RpcServerPool {

    static Logger log = LoggerFactory.getLogger(RpcClient.class.getName());

    /**
     * key 服务名 value 初始化链接参数
     */
    private final Map<String, RpcServerDto> serverDtoMap = new HashMap<>();

    /**
     * key 服务名 value 链接信息
     */
    private static Map<String, ArrayListMultimap<String,ChannelFuture>> channelMap = new HashMap<>();

    private static String serverPre = "server:pre:";

    private static volatile RpcServerPool instance;

    private RpcContext rpcContext;

    private RpcServerPool(RpcContext rpcContext) {
        this.rpcContext = rpcContext;
    }

    /**
     * 获取当前实例
     * @return
     */
    public static RpcServerPool getInstance() {
        return instance;
    }

    /**
     * 获取一个新rpc客户端连接池实例
     * @param rpcContext
     * @return
     */
    public static RpcServerPool getNewInstance(@NonNull RpcContext rpcContext) {
        instance = new RpcServerPool(rpcContext);
        return instance;
    }
    /**
     * 注册服务
     * @param serverName
     * @param ip
     * @param port
     */
    public static void registerServer(String serverName, String ip, int port) {
        Jedis resource = null;
        try {
            resource = SpringUtil.getBean(JedisPool.class).getResource();
            String key = serverPre + serverName + ":" + ip + ":" + port;
            //设置注册信息
            resource.set(key.getBytes(), RedisUtil.serialize(new ServerDto(port, ip, serverName)));
            //90s 过期
            resource.expire(key, 90);
        } finally {
            if (resource != null) {
                resource.close();
            }
        }
    }


    /**
     * 初始化所有连接
     */
    public RpcServerPool initAllConnect() {
        //根据注册列表 获取redis中存的 ip和端口
        log.debug("开始获取服务列表...");
        loadServer();
        log.debug("开始连接服务列表...");
        for (String serverName : serverDtoMap.keySet()) {
            RpcServerDto rpcServerDto = serverDtoMap.get(serverName);
            //创建链接
            createConnect(serverName, rpcServerDto);
        }
        log.debug("连接服务完成...");
        //定时检查链接
        checkTask();
        return this;
    }

    /**
     * 创建链接
     *
     * @param serverName
     * @param rpcServerDto
     */
    private void createConnect(String serverName, RpcServerDto rpcServerDto) {
        //获取配置
        RpcSource rpcSource = rpcContext.getRpcSource();
        for (RpcServerDto.Example example : rpcServerDto.getExamples()) {
            //获取客户端链接实例
            NettyClient nettyClient = NettyClient.geInstance();
            //获取服务channel列表
            ArrayListMultimap<String, ChannelFuture> futureList = channelMap.getOrDefault(serverName, ArrayListMultimap.create());
            //创建链接
            nettyClient.createConnect(rpcSource.getConnectCount(), example.getIp(), example.getPort(), futureList);
            //放入集合
            channelMap.putIfAbsent(serverName, futureList);
        }
    }

    /**
     * 重新链接
     *
     * @param serverName
     */
    public synchronized void reConnect(String serverName) {
        channelMap.remove(serverName);
        //获取注册列表
        addAllServer(serverName);
        //创建连接
        createConnect(serverName, serverDtoMap.get(serverName));
    }

    /**
     * 获取一个连接
     *
     * @return
     */
    public ChannelFuture getChannelByServerName(String serverName) {
        //获取服务连接池
        ArrayListMultimap<String, ChannelFuture> listMultimap = channelMap.get(serverName);
        ChannelFuture channelFuture = null;
        //不存在重新链接
        if (listMultimap == null || listMultimap.valueSize() == 0) {
            //可以间隔一定时间才进行下一次链接
            reConnect(serverName);
            //重新取链接
            listMultimap = channelMap.get(serverName);
        }
        for (; listMultimap.valueSize() > 0; ) {
            //随件获取一个链接
            List<ChannelFuture> channelFutures = listMultimap.values();
            //随机祛暑下标
            int index = (int) (Math.random() * (channelFutures.size()));
            //转数组
            channelFuture = channelFutures.get(index);
            //链接存活 直接return
            if (channelFuture.channel().isActive()) {
                break;
            } else {
                //清空无效链接
                listMultimap.removeElement(serverName, channelFuture);
                //重新获取一次
                return getChannelByServerName(serverName);
            }
        }
        return channelFuture;
    }

    /**
     * 检查服务链接
     */
    public void checkConnect() {
        for (String serverName : serverDtoMap.keySet()) {
            RpcServerDto rpcServerDto = serverDtoMap.get(serverName);
            //清湖已经存在的实例集合
            rpcServerDto.clearExamples();
            //加载当前服务链接
            addAllServer(serverName);
            //创建连接
            createConnect(serverName, rpcServerDto);
        }
    }

    /**
     * 检查定时链接任务
     */
    public void checkTask(){
        //持续注册 每60s注册一次
        Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "schedule-register");
            thread.setDaemon(true);
            return thread;
        }).scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    checkConnect();
                } catch (Exception e) {
                    log.error("注册到服务列表失败", e);
                }
            }
        }, 90L, 60L, TimeUnit.SECONDS);
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
        RpcServerDto serverDto = serverDtoMap.getOrDefault(serverName, new RpcServerDto(serverName));
        serverDto.addExample(ip, port);
        serverDtoMap.put(serverName, serverDto);
    }

    /**
     * 添加服务名字
     *
     * @param serverName
     */
    public void addServerName(String serverName) {
        serverDtoMap.putIfAbsent(serverName, new RpcServerDto(serverName));
    }

    /**
     * 加载所有服务
     */
    public void loadServer() {
        for (String serverName : serverDtoMap.keySet()) {
            addAllServer(serverName);
        }
    }

    /**
     * 当前服务名链接所有服务
     *
     * @param serverName
     */
    private void addAllServer(String serverName) {
        Jedis resource = null;
        try {
            resource = SpringUtil.getBean(JedisPool.class).getResource();
            Set<String> keys = resource.keys(serverPre + serverName + ":*");
            for (String key : keys) {
                //添加一个服务练剑
                addServer(serverName, resource, key);
            }
        } finally {
            if (resource != null) {
                resource.close();
            }
        }
    }

    /**
     * 添加一个服务练剑
     *
     * @param serverName
     * @param resource
     * @param key
     */
    private synchronized void addServer(String serverName, Jedis resource, String key) {
        byte[] resultBytes = resource.get(key.getBytes());
        ServerDto serverDto = RedisUtil.unserizlize(resultBytes);
        if (serverDto != null && StringUtils.equals(serverName, serverDto.getServerName())) {
            serverAdd(serverName, serverDto.getIp(), serverDto.getPort());
        }
    }
}
