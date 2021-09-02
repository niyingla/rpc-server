package com.example.demo.rpc.context;

import com.example.demo.collection.ArrayListMultimap;
import com.example.demo.dto.RpcServerDto;
import com.example.demo.dto.ServerInfo;
import com.example.demo.monad.ExecuteRedisFunction;
import com.example.demo.netty.config.RegisterPubMsgSub;
import com.example.demo.netty.config.RpcSource;
import com.example.demo.netty.connect.NettyClient;
import com.example.demo.rpc.util.RpcClient;
import com.example.demo.rpc.util.SpringUtil;
import com.example.demo.util.SerializiUtil;
import com.sun.xml.internal.messaging.saaj.util.Base64;
import io.netty.channel.ChannelFuture;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private final Map<String, RpcServerDto> serverMap = new HashMap<>();

    /**
     * key 服务名 value 链接信息map（key ip+端口 value 连接数组）
     */
    private static Map<String, ArrayListMultimap<String, ChannelFuture>> channelMap = new HashMap<>();

    /**
     * redis 注册地址前缀
     */
    private static String serverPre = "server:pre:";

    /**
     * redis 注册地址前缀
     */
    private static String registerPre = "register:pre:";

    /**
     * 当前服务实例
     */
    private static volatile RpcServerPool instance;

    /**
     * 上下文对象
     */
    private RpcContext rpcContext;

    private RpcServerPool(RpcContext rpcContext) {
        this.rpcContext = rpcContext;
    }

    /**
     * 获取当前实例
     *
     * @return
     */
    public static RpcServerPool getInstance() {
        return instance;
    }

    /**
     * 获取一个新rpc客户端连接池实例
     *
     * @param rpcContext
     * @return
     */
    public static RpcServerPool getNewInstance(@NonNull RpcContext rpcContext) {
        instance = new RpcServerPool(rpcContext);
        return instance;
    }

    /**
     * 注册服务
     *
     * @param nameSpace
     * @param serverInfo
     */
    public static void registerServer(String nameSpace, ServerInfo serverInfo) {
        String key = serverPre + nameSpace + serverInfo.getServerName() + ":" + serverInfo.getIp() + ":" + serverInfo.getPort();
        //设置注册信息 90s失效
        execRedisFunction(resource -> resource.setex(key.getBytes(), 90, SerializiUtil.serialize(serverInfo)));
    }


    /**
     * 发送开始注册信息
     *
     * @param nameSpace
     * @param serverInfo
     */
    public static void sendRegisterMsg(String nameSpace, ServerInfo serverInfo) {
        String key = registerPre + nameSpace;
        execRedisFunction(resource -> resource.publish(key, SerializiUtil.toBase64String(serverInfo)));
    }

    /**
     * 消费注册消息
     *
     * @param nameSpace
     */
    public static void consumerRegisterMessage(String nameSpace) {
        String key = registerPre + nameSpace;
        Jedis resource = SpringUtil.getBean(JedisPool.class).getResource();
        resource.subscribe(new RegisterPubMsgSub(), key);

    }

    /**
     * 执行redis方法
     */
    public static void execRedisFunction(ExecuteRedisFunction redisFunction) {
        Jedis resource = null;
        try {
            resource = SpringUtil.getBean(JedisPool.class).getResource();
            redisFunction.apply(resource);
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
        for (String serverName : serverMap.keySet()) {
            RpcServerDto rpcServerDto = serverMap.get(serverName);
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
            ArrayListMultimap<String, ChannelFuture> futureList = channelMap.computeIfAbsent(serverName, key -> ArrayListMultimap.create());
            //创建链接
            nettyClient.createConnect(rpcSource.getConnectCount(), example.getIp(), example.getPort(), futureList);
        }
    }

    /**
     * 重新链接
     *
     * @param serverName
     */
    public synchronized void reConnect(String serverName) {
        ArrayListMultimap<String, ChannelFuture> listMultimap = channelMap.get(serverName);
        //不存在重新链接
        if (listMultimap == null || listMultimap.valueSize() == 0) {
            channelMap.remove(serverName);
            //获取注册列表
            addAllServer(rpcContext.getRpcSource().getNameSpace(), serverName);
            //创建连接
            createConnect(serverName, serverMap.get(serverName));
        }
    }

    /**
     * 接受到注册消息主动链接服务
     */
    public void initiativeConnectServer(String ip, int port, String serverName) {
        if (!serverMap.containsKey(serverName)) {
            return;
        }
        //获取客户端链接实例
        NettyClient nettyClient = NettyClient.geInstance();
        //获取服务channel列表
        ArrayListMultimap<String, ChannelFuture> futureList = channelMap.computeIfAbsent(serverName, key -> ArrayListMultimap.create());
        //创建链接
        nettyClient.createConnect(rpcContext.getRpcSource().getConnectCount(), ip, port, futureList);
    }

    /**
     * 获取一个连接
     *
     * @return
     */
    public ChannelFuture getChannelByServerName(String serverName) {
        //获取服务连接池
        ArrayListMultimap<String, ChannelFuture> listMultimap = channelMap.get(serverName);
        ChannelFuture channelFuture;
        //不存在重新链接
        if (listMultimap == null || listMultimap.valueSize() == 0) {
            //可以间隔一定时间才进行下一次链接
            reConnect(serverName);
            //重新取链接
            listMultimap = channelMap.get(serverName);
        }
        //随件获取一个链接
        List<ChannelFuture> channelFutures = listMultimap.values();
        //随机祛暑下标
        int index = (int) (Math.random() * (channelFutures.size()));
        //转数组
        channelFuture = channelFutures.get(index);
        //链接存活 直接return
        if (channelFuture.channel().isActive()) {
            return channelFuture;
        } else {
            //清空无效链接
            listMultimap.removeElement(serverName, channelFuture);
            //重新获取一次
            return getChannelByServerName(serverName);
        }
    }

    /**
     * 检查服务链接
     */
    public void checkConnect() {
        for (String serverName : serverMap.keySet()) {
            RpcServerDto rpcServerDto = serverMap.get(serverName);
            //清湖已经存在的实例集合
            rpcServerDto.clearExamples();
            //加载当前服务链接
            addAllServer(rpcContext.getRpcSource().getNameSpace(), serverName);
            //创建连接
            createConnect(serverName, rpcServerDto);
        }
    }

    /**
     * 检查定时链接任务
     */
    public void checkTask() {
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
        }, 100L, 1000L, TimeUnit.SECONDS);
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
        RpcServerDto serverDto = serverMap.computeIfAbsent(serverName, key -> new RpcServerDto(serverName));
        serverDto.addExample(ip, port);
    }

    /**
     * 添加服务名字
     *
     * @param serverName
     */
    public void addServerName(String serverName) {
        serverMap.putIfAbsent(serverName, new RpcServerDto(serverName));
    }

    /**
     * 加载所有服务
     */
    public void loadServer() {
        for (String serverName : serverMap.keySet()) {
            addAllServer(rpcContext.getRpcSource().getNameSpace(), serverName);
        }
    }

    /**
     * 当前服务名链接所有服务
     *
     * @param nameSpace  名称空间
     * @param serverName 服务名
     */
    private void addAllServer(String nameSpace, String serverName) {
        Jedis resource = null;
        try {
            resource = SpringUtil.getBean(JedisPool.class).getResource();
            Set<String> keys = resource.keys(serverPre + nameSpace + serverName + ":*");
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
        ServerInfo serverInfo = SerializiUtil.unserizlize(resultBytes);
        if (serverInfo != null && StringUtils.equals(serverName, serverInfo.getServerName())) {
            serverAdd(serverName, serverInfo.getIp(), serverInfo.getPort());
        }
    }
}
