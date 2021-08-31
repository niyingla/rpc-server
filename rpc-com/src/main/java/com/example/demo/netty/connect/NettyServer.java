package com.example.demo.netty.connect;

import com.example.demo.netty.code.MarshallingCodeCFactory;
import com.example.demo.netty.handler.ServerReqHandler;
import com.example.demo.rpc.context.RpcContext;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: demo
 * @description:
 * @author: xiaoye
 * @create: 2019-08-12 11:29
 **/
public class NettyServer {
    static Logger log = LoggerFactory.getLogger(NettyClient.class.getName());


    private EventLoopGroup pGroup = new NioEventLoopGroup(2);
    private EventLoopGroup cGroup = new NioEventLoopGroup(4);

    /**
     * 初始化服务端
     *
     * @throws Exception
     */
    public void init(int port) throws Exception {
        log.debug("开始服务端。。。");
        ServerBootstrap b = new ServerBootstrap();
        b.group(pGroup, cGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                //表示缓存区动态调配（自适应）
                .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                //缓存区 池化操作
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                //设置日志
                .handler(new LoggingHandler(LogLevel.INFO))
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
//                        sc.pipeline().addLast(new IdleStateHandler(0, 0, 60));
                        sc.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                        sc.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
//                      sc.pipeline().addLast(new JsonObjectDecoder());
                        sc.pipeline().addLast(new ServerReqHandler());
                    }
                });
        ChannelFuture cf = b.bind(port).sync();
        log.debug("初始化服务端完成。。。");
        //阻塞线程
//        cf.channel().closeFuture().sync();
    }


    /**
     * 关闭连接 本处不调用
     * 这是服务基础
     */
    public void shotDown() {
        pGroup.shutdownGracefully();
        cGroup.shutdownGracefully();
    }


    /**
     * @param rpcContext 上下文
     * 开始连接
     */
    public static void start(RpcContext rpcContext) {
        NettyServer nettyServer = null;
        try {
            nettyServer = new NettyServer();
            nettyServer.init(rpcContext.getRpcSource().getPort());
            rpcContext.setNettyServer(nettyServer);
        } catch (Exception e) {
            log.error("启动客户端失败", e);
            nettyServer.shotDown();
            throw new RuntimeException("启动客户端失败");
        }
    }
}
