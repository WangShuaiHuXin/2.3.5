package com.imapcloud.nest.common.async;

import com.alibaba.fastjson.JSON;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.mapper.NestSensorAudioMapper;
import com.imapcloud.nest.model.NestEntity;
import com.imapcloud.nest.model.NestSensorAudioEntity;
import com.imapcloud.nest.pojo.dto.AudioDto;
import com.imapcloud.nest.service.NestSensorAudioService;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.utils.ToolUtil;
import com.imapcloud.nest.utils.WebSocketRes;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.pojo.BaseResult3;
import com.imapcloud.sdk.pojo.constant.ResultCodeEnum;
import com.imapcloud.sdk.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 异步获取喊话器列表
 *
 * @author: zhengxd
 * @create: 2021/9/18
 **/
@Component
@Slf4j
public class CpsAudioListAsync {

    @Autowired
    private NestService nestService;

    @Resource
    private NestSensorAudioMapper nestSensorAudioMapper;


    public void dealResult(Boolean isSuccess, BaseResult3 result, NestSensorAudioEntity audioEntity, String nestUuid) {
        log.info("{},{},{}",isSuccess, JSON.toJSONString(result),nestUuid);
        String message;
        if (isSuccess && ResultCodeEnum.REQUEST_SUCCESS.equals(result.getCode())) {
            log.info("上传音频文件请求发送成功");
            message = WebSocketRes.ok().topic(WebSocketTopicEnum.UPLOAD_AUDIO_TO_CPS_TIP).toJSONString();
            log.info("成功的message: {}", message);
        } else {
            log.info("上传音频文件失败");
            message = WebSocketRes.err().topic(WebSocketTopicEnum.UPLOAD_AUDIO_TO_CPS_TIP).msg(result.getMsg()).toJSONString();
            log.error("成功的message: {}", message);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ChannelService.sendMessageByType8Channel(nestUuid, message);
    }

}
