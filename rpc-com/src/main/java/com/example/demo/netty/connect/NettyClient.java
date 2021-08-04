package com.example.demo.netty.connect;

import com.example.demo.netty.code.MarshallingCodeCFactory;
import com.example.demo.netty.handler.ResultHandler;
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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pikaqiu
 */
public class NettyClient {

    static Logger log = LoggerFactory.getLogger(NettyClient.class.getName());
    private Bootstrap b = new Bootstrap();
    private EventLoopGroup group = new NioEventLoopGroup();
    private List<ChannelFuture> channelFutures = new ArrayList<>();


    public static NettyClient getNewInstance(){
        return new NettyClient();
    }

    /**
     * 初始化客户端
     * @return
     */
    public NettyClient initClient() {
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
    public NettyClient createConnect(int count, String ip, int port, List<ChannelFuture> channelFutures) {
        for (int i = 0; i < count; i++) {
            Runnable runnable = () -> {
                try {
                    ChannelFuture cf = b.connect(ip, port).sync();
                    channelFutures.add(cf);
                    cf.channel().closeFuture().sync();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            new Thread(runnable).start();
        }
        return this;

    }

    /**
     * 随机获取一个连接
     * @return
     */
    public ChannelFuture getChannelFuture() {
        return CollectionUtils.isEmpty(channelFutures) ? null : channelFutures.get((int) (Math.random() * (channelFutures.size())));
    }

    /**
     * 关闭连接
     */
    public void close() {
        group.shutdownGracefully();
    }

}
