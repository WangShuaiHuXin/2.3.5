package com.imapcloud.nest.common.netty.service;

import com.alibaba.fastjson.JSONObject;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.AppWebSocketTopicEnum;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.pojo.dto.app.AppStartMissionResDTO;
import com.imapcloud.nest.utils.AppWebSocketRes;
import com.imapcloud.nest.utils.WebSocketRes;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wmin on 2020/12/1 17:12
 *
 * @author wmin
 */
@Component
public class AppService {

    public void startMission(String uuid, Integer missionId, Integer gainDataMode, Integer gainVideo) {
        Map<String, Object> map = new HashMap<>(4);
        map.put("missionId", missionId);
        map.put("gainDataMode", gainDataMode);
        map.put("gainVideo", gainVideo);
        String msg = AppWebSocketRes.instance().token("11111").messageType(AppWebSocketTopicEnum.START_MISSION.getValue()).messageBody(map).toJSONString();
        //TODO 设备id暂时写死，后续前端设计出来后，任务会与设备绑定
        ChannelService.sendMessageByType7Channel("8E5D3877EB0F7947", msg);
    }



}
