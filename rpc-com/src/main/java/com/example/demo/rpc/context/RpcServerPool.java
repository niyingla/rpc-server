package com.example.demo.rpc.context;

import com.example.demo.dto.RpcServerDto;
import com.example.demo.netty.config.RpcSource;
import com.example.demo.netty.connect.NettyClient;
import com.example.demo.rpc.factory.StartFactory;
import com.example.demo.rpc.util.RpcClient;
import com.example.demo.rpc.util.SpringUtil;
import com.google.common.collect.ArrayListMultimap;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private RpcServerPool() {
    }

    public static RpcServerPool getInstance() {
        if (instance == null) {
            synchronized (RpcServerPool.class) {
                if (instance == null) {
                    instance = new RpcServerPool();
                }
            }
        }
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
            resource.set(key, ip + ":" + port);
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
    public RpcServerPool initAllConnect(RpcContext rpcContext) {
        //根据注册列表 获取redis中存的 ip和端口
        log.info("开始获取服务列表...");
        loadServer();
        log.info("开始连接服务列表...");
        for (String serverName : serverDtoMap.keySet()) {
            RpcServerDto rpcServerDto = serverDtoMap.get(serverName);
            //创建链接
            createConnect(serverName, rpcServerDto, rpcContext);
        }
        log.info("连接服务完成...");
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
    private void createConnect(String serverName, RpcServerDto rpcServerDto, RpcContext rpcContext) {
        //获取配置
        RpcSource rpcSource = rpcContext.getRpcSource();
        for (RpcServerDto.Example example : rpcServerDto.getExamples()) {
            //获取客户端链接实例
            NettyClient nettyClient = NettyClient.geInstance();
            //获取服务channel列表
            ArrayListMultimap<String, ChannelFuture> futureList = channelMap.getOrDefault(serverName, ArrayListMultimap.create());
            //初始化链接
            nettyClient.initClient().createConnect(rpcSource.getConnectCount(), example.getIp(), example.getPort(), futureList);
            if (!channelMap.containsKey(serverName)) {
                channelMap.put(serverName, futureList);
            }
        }
    }

    /**
     * 重新链接
     *
     * @param serverName
     */
    public void reConnect(String serverName) {
        channelMap.remove(serverName);
        //获取注册列表
        addAllServer(serverName);
        //创建连接
        createConnect(serverName, serverDtoMap.get(serverName), StartFactory.getRpcContext());
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
        if (listMultimap == null || listMultimap.size() == 0) {
            //todo 可以间隔一定时间才进行下一次链接
            reConnect(serverName);
        }
        for (; listMultimap.size() > 0; ) {
            //随件获取一个链接
            Collection<ChannelFuture> channelFutures = listMultimap.values();
            int index = (int) (Math.random() * (channelFutures.size()));
            channelFuture = channelFutures.toArray(new ChannelFuture[0])[index];
            //链接存活 直接return
            if (channelFuture != null && channelFuture.channel().isActive()) {
                break;
            } else {
                synchronized (RpcServerPool.class) {
                    //清空无效链接
                    channelFutures.remove(channelFuture);
                    if (CollectionUtils.isEmpty(channelFutures)) {
                        channelMap.remove(serverName);
                    }
                }
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
            rpcServerDto.clearExamples();
            //加载当前服务链接
            addAllServer(serverName);
            //创建连接
            createConnect(serverName, rpcServerDto, StartFactory.getRpcContext());
        }
    }


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
        if (!channelMap.containsKey(serverName)) {
            RpcServerDto serverDto = new RpcServerDto(serverName);
            serverDtoMap.put(serverName, serverDto);
        }
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
            Set<String> keys = resource.keys(serverPre + serverName+":*");
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
    private void addServer(String serverName, Jedis resource, String key) {
        String ipAndPort = resource.get(key);
        if (ipAndPort != null && ipAndPort.contains(":")) {
            String[] split = ipAndPort.split(":");
            serverAdd(serverName, split[0], Integer.valueOf(split[1]));
        }
    }
}