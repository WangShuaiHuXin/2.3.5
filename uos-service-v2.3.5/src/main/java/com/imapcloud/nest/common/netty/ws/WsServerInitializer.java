package com.imapcloud.nest.common.netty.ws;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;

/**
 * Created by wmin on 2020/9/14 10:16
 *
 * @author wmin
 */
public class WsServerInitializer extends ChannelInitializer<Channel> {

    private final ChannelGroup group;
    private final AttributeKey<String> attr;

    public WsServerInitializer(ChannelGroup group, AttributeKey<String> attr) {
        this.group = group;
        this.attr = attr;
    }

    /**
     * 将所需要的ChannelHandler添加到ChannelPipeline
     *
     * @param channel
     */
    @Override
    protected void initChannel(Channel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        //http服务器的编解码器，遵从HTTP协议，先看如下类
        pipeline.addLast(new HttpServerCodec())
                //
                .addLast(new IdleStateHandler(10,10,10))
//                .addLast(new HeartBeatHandler())
                .addLast(new ChunkedWriteHandler())
                .addLast(new HttpObjectAggregator(64 * 1024))
                .addLast(new HttpRequestHandler(attr))
                .addLast(new WebSocketServerProtocolHandler("/ws"))
                .addLast(new TextWebSocketFrameHandler(group, attr));
    }
}
