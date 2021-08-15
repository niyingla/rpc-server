package com.example.demo.netty.connect;

import com.example.demo.netty.code.MarshallingCodeCFactory;
import com.example.demo.netty.handler.ResultHandler;
import com.google.common.collect.ArrayListMultimap;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author pikaqiu
 */
public class NettyClient {

    static Logger log = LoggerFactory.getLogger(NettyClient.class.getName());
    private Bootstrap b = new Bootstrap();
    private EventLoopGroup group = new NioEventLoopGroup();
    private boolean hasInit = false;

    private static volatile NettyClient instance;

    /**
     * 获取客户端实例
     * @return
     */
    public static NettyClient geInstance() {
        if (instance == null) {
            synchronized (NettyClient.class) {
                if (instance == null) {
                    instance = new NettyClient();
                    //自动 初始化客户端
                    try {
                        instance.initClient();
                    } catch (Exception e) {
                        instance.close();
                        log.error("初始化客户端实例失败", e);
                        throw new RuntimeException("初始化客户端实例失败");
                    }
                }
            }
        }
        return instance;
    }

    /**
     * 初始化客户端
     *
     * @return
     */
    private synchronized NettyClient initClient() {
        if (hasInit) {
            return this;
        }
        //2 辅助类(注意Client 和 Server 不一样)
        b.group(group)
                .channel(NioSocketChannel.class)
                //表示缓存区动态调配（自适应）
                .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                //缓存区 池化操作
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        sc.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                        sc.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                        sc.pipeline().addLast(new ResultHandler());
                    }
                });
        hasInit = true;
        log.info("初始化客户端完成。。。");
        return this;
    }


    /**
     * 创建连接池内连接
     *
     * @param count
     * @param ip
     * @param port
     * @return
     */
    public NettyClient createConnect(int count, String ip, int port, ArrayListMultimap<String, ChannelFuture> channelFuturesMultimap) {
        //获取当前server地址连接列表
        List<ChannelFuture> channelFutures = channelFuturesMultimap.get(ip + ":" + port);
        //已经存在就不连接了
        if (!CollectionUtils.isEmpty(channelFutures)) {
            return this;
        }
        log.info("创建连接 ip: {} ,port: {}", ip, port);
        //循环创建连接
        for (int i = 0; i < count; i++) {
            try {
                //链接服务端
                ChannelFuture channelFuture = b.connect(ip, port).sync();
                synchronized (NettyClient.class) {
                    channelFuturesMultimap.put(ip + ":" + port, channelFuture);
                }
            } catch (Exception e) {
                log.error("链接错误...", e);
            }
        }
        return this;
    }


    /**
     * 关闭连接
     */
    public void close() {
        group.shutdownGracefully();
    }

}
