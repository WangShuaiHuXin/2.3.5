package com.imapcloud.nest.common.netty.ws;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * Created by wmin on 2020/9/14 10:10
 * url:ws://192.168.1.190:8182/ws/{account}/{type}/{nestId}
 *
 * @author wmin
 */
@Component
public class WsServer {

    /**
     * 创建DefaultChannelGroup，其将保存所有已经连接的WebSocket Channel
     */
    private final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

    private final static AttributeKey<String> NEST_ID = AttributeKey.newInstance("nestId");
    /**
     * 一个NIO线程池处理链接监听ee
     */
    private final EventLoopGroup bossGroup =  new NioEventLoopGroup() ;
    /**
     * 一个线程处理IO操作
     */
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private Channel channel;

    /**
     * 服务开启
     *
     * @param address
     * @return
     */
    public ChannelFuture start(InetSocketAddress address) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(createInitializer(channelGroup, NEST_ID));

        ChannelFuture future = bootstrap.bind(address);
        channel = future.syncUninterruptibly().channel();
        return future;
    }


    private ChannelInitializer<Channel> createInitializer(ChannelGroup group, AttributeKey<String> nestId) {
        return new WsServerInitializer(group, nestId);
    }

    /**
     * 处理服务器关闭，并释放所有资源
     */
    public void destory() {
        if (channel != null) {
            channel.close();
        }

        ChannelService.closeChannelGroup();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }


    public ChannelGroup getChannelGroup() {
        return channelGroup;
    }

    public static AttributeKey<String> getNestId() {
        return NEST_ID;
    }
}
