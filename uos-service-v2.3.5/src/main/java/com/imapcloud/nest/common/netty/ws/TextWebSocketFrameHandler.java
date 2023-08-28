package com.imapcloud.nest.common.netty.ws;

import com.imapcloud.nest.utils.spring.SpringContextUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

/**
 * Created by wmin on 2020/9/14 10:26
 *
 * @author wmin
 */
@Slf4j
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final ChannelGroup group;
    private final AttributeKey<String> attr;

    private AppWsMsgHandleService appWsMsgHandleService = SpringContextUtils.getBean(AppWsMsgHandleService.class);

    public TextWebSocketFrameHandler(ChannelGroup group, AttributeKey<String> attr) {
        this.group = group;
        this.attr = attr;
    }

    /**
     * 重写userEventTriggered()方法已处理自定义事件
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            /**
             * 如果该事件表示握手成功，则从改Channelipeline中移除HttpRequestHandler，因为将不会接受到任何HTTP消息
             */
            ctx.pipeline().remove(HttpRequestHandler.class);
            putChannel(ctx.channel());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        AppWsMsgHandleService appWsMsgHandleService = SpringContextUtils.getBean(AppWsMsgHandleService.class);
        appWsMsgHandleService.handelAppMsg(ctx.channel(), msg);
        if ("ping".equals(msg.text())) {
            ctx.channel().writeAndFlush(new TextWebSocketFrame("pong"));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        removeChannel(ctx.channel());
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        removeChannel(ctx.channel());
        super.channelInactive(ctx);
    }

    private void putChannel(Channel channel) {
        ChannelService.put(channel);
    }

    private void removeChannel(Channel channel) {
        log.debug("TextWebSocketFrameHandler=====================>" + channel.toString());
        ChannelService.remove(channel);
    }
}
