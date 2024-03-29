package com.example.demo.rpc.context;

import com.example.demo.collection.ArrayListMultimap;
import com.example.demo.dto.RpcServer;
import com.example.demo.dto.ServerInfo;
import com.example.demo.netty.config.RegisterServer;
import com.example.demo.netty.config.RpcSource;
import com.example.demo.netty.connect.NettyClient;
import com.example.demo.rpc.util.RpcClient;
import com.example.demo.rpc.util.SpringUtil;
import com.example.demo.util.SerializiUtil;
import io.netty.channel.ChannelFuture;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
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
    private final Map<String, RpcServer> serverMap = new HashMap<>();

    /**
     * key 服务名 value 链接信息map（key ip+端口 value 连接数组）
     */
    private static Map<String, ArrayListMultimap<String, ChannelFuture>> channelMap = new HashMap<>();


    /**
     * 当前服务实例
     */
    private volatile RpcServerPool instance;

    /**
     * 上下文对象
     */
    private RpcContext rpcContext;

    public RpcServerPool(@NonNull RpcContext rpcContext) {
        if (this.instance == null) {
            synchronized (RpcServerPool.class) {
                if (this.instance == null) {
                    this.rpcContext = rpcContext;
                    this.instance = this;
                }
            }
        }
    }

    /**
     * 获取当前实例
     *
     * @return
     */
    public RpcServerPool getInstance() {
        return instance;
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
            RpcServer rpcServer = serverMap.get(serverName);
            //创建链接
            createConnect(serverName, rpcServer);
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
     * @param rpcServer
     */
    private void createConnect(String serverName, RpcServer rpcServer) {
        //获取配置
        RpcSource rpcSource = rpcContext.getRpcSource();
        //获取客户端链接实例
        NettyClient nettyClient = rpcContext.getNettyClient();
        for (RpcServer.Example example : rpcServer.getExamples()) {
            //获取服务channel列表
            ArrayListMultimap<String, ChannelFuture> futureList = channelMap.computeIfAbsent(serverName, key -> ArrayListMultimap.create());
            //本次连接数 = 默认连接数 - 已存在连接数
            int connectCount = rpcSource.getConnectCount() - futureList.valueSize();
            //创建链接
            nettyClient.createConnect(connectCount, example.getIp(), example.getPort(), futureList);
        }
    }

    /**
     * 重新链接
     *
     * @param serverName
     */
    public void reConnect(String serverName) {
        synchronized (serverName) {
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
    }

    /**
     * 接受到注册消息主动链接服务
     */
    public void initiativeConnectServer(String ip, int port, String serverName) {
        //过滤掉非引用的服务
        if (!serverMap.containsKey(serverName)) {
            return;
        }
        log.debug("新服务注册开始连接服务...");
        //获取客户端链接实例
        NettyClient nettyClient = rpcContext.getNettyClient();
        //获取服务channel列表
        ArrayListMultimap<String, ChannelFuture> futureList = channelMap.computeIfAbsent(serverName, key -> ArrayListMultimap.create());
        //创建链接
        nettyClient.createConnect(rpcContext.getRpcSource().getConnectCount(), ip, port, futureList);
    }

    /**
     * 获取一个连接(默认 没有连接的时候 重新创建)
     *
     * @return
     */
    public ChannelFuture getChannelByServerName(String serverName) {
        //获取链接集合
        ArrayListMultimap<String, ChannelFuture> listMultimap = getChanMap(serverName);
        if (listMultimap == null) return null;
        //随件获取一个链接
        ChannelFuture channelFuture = listMultimap.randomValue();
        //链接存活 直接return
        if (channelFuture.channel().isActive()) {
            return channelFuture;
        } else {
            //清空无效链接
            listMultimap.removeElement(serverName, channelFuture);
        }
        return null;
    }

    /**
     * 获取连接集合
     * @param serverName
     * @return
     */
    private ArrayListMultimap<String, ChannelFuture> getChanMap(String serverName) {
        //获取服务连接池
        ArrayListMultimap<String, ChannelFuture> listMultimap = channelMap.get(serverName);
        //不存在重新链接
        if (listMultimap == null || listMultimap.valueSize() == 0) {
            //可以间隔一定时间才进行下一次链接
            reConnect(serverName);
        }
        return channelMap.get(serverName);
    }

    /**
     * 检查服务链接
     */
    public void checkConnect() {
        for (String serverName : serverMap.keySet()) {
            RpcServer rpcServer = serverMap.get(serverName);
            //清湖已经存在的实例集合
            rpcServer.clearExamples();
            //加载当前服务链接
            addAllServer(rpcContext.getRpcSource().getNameSpace(), serverName);
            //创建连接
            createConnect(serverName, rpcServer);
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
    public RpcServer serverAdd(String serverName, String ip, int port) {
        RpcServer serverDto = serverMap.computeIfAbsent(serverName, key -> new RpcServer(serverName));
        serverDto.addExample(ip, port);
        return serverDto;
    }

    /**
     * 添加服务名字
     *
     * @param serverName
     */
    public void addServerName(String serverName) {
        serverMap.computeIfAbsent(serverName, name -> new RpcServer(name));
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
            Set<String> keys = resource.keys(RegisterServer.SERVER_PRE + nameSpace + serverName + ":*");
            for (String key : keys) {
                //添加一个服务链接地址
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
