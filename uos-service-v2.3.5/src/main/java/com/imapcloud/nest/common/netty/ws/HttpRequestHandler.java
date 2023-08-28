package com.imapcloud.nest.common.netty.ws;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.AttributeKey;

/**
 * Created by wmin on 2020/9/14 10:24
 *
 * @author wmin
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final AttributeKey<String> attr;

    public HttpRequestHandler(AttributeKey<String> attr) {
        this.attr = attr;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        String uri = request.uri();
        //TODO token校验
        String param = uri.substring(4);
//        if (!validateToken(param)) {
//            ctx.close();
//            return;
//        }
        param = param.contains("?language=") ? param : param + "?language=zh-CN";
        String substring = uri.substring(0, 3);
        ctx.channel().attr(attr).set(param);
        request.setUri(substring);
        ctx.fireChannelRead(request.retain());
    }


    private void removeChannel(Channel channel) {
        ChannelService.remove(channel);
    }

}
