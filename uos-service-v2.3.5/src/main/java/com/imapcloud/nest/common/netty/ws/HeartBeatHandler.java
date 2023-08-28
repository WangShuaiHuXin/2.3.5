package com.imapcloud.nest.common.netty.ws;

import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.utils.WebSocketRes;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;


import java.util.Map;

import static com.imapcloud.nest.common.netty.ws.ChannelGroupTypeEnum.TYPE7;

/**
 * channel心跳检测
 */
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //判断evt是否是idleStateEvent（用于触发用户事件，包含读空闲/写空闲/读写空闲）
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
//                handleReaderIdle(ctx);
            } else if (idleStateEvent.state() == IdleState.WRITER_IDLE) {

            } else if (idleStateEvent.state() == IdleState.ALL_IDLE) {

            }
        }
    }

    private void handleReaderIdle(ChannelHandlerContext ctx) {
        AttributeKey<String> attr = WsServer.getNestId();
        Channel channel = ctx.channel();
        String param = channel.attr(attr).get();
        String[] split = param.split("/");
        String type = split[1];
        String deviceId = split[2].split("\\?")[0];

        if (TYPE7.getValue().equals(type)) {
            System.out.println("读空闲...");
            ChannelGroup channelGroupByType = ChannelService.getChannelGroupByType(7);
            System.out.println(channelGroupByType.size());
//            Map<String, Integer> appStateMap = ChannelService.getAppStateMap();
//            Integer state = appStateMap.get(deviceId);
            String message = WebSocketRes.ok().topic(WebSocketTopicEnum.MOBILE_APP_OFF_LINE).data(null).uuid(deviceId).toJSONString();
            ChannelService.sendMessageByType6Channel(deviceId, message);
        }
    }
}
