package com.imapcloud.nest.service.listener;

import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.pojo.dto.MqttTakeOffRecordEventDTO;
import com.imapcloud.nest.sdk.listener.DataTranCompleteListenerFactory;
import com.imapcloud.nest.service.event.MqttSwitchZoomCameraEvent;
import com.imapcloud.nest.service.event.MqttTakeOffRecordEvent;
import com.imapcloud.nest.service.event.NestUnloadBatteryEvent;
import com.imapcloud.nest.utils.WebSocketRes;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.camera.enums.CameraLensVideoSourceEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MqttCommonEventListenerService {

    @Autowired
    private RedisService redisService;

    @Async("pubExecutor")
    @EventListener
    public void mqttSwitchZoomCameraListen(MqttSwitchZoomCameraEvent mqttSwitchZoomCameraEvent) {
        log.info("事件触发切换变焦镜头");
        String nestUuid = mqttSwitchZoomCameraEvent.getSource();
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.SWITCH_ZOOM_CAMERA, nestUuid);
            MqttResult<NullParam> res = cm.getCameraManagerCf().setCameraLensVideoSource(CameraLensVideoSourceEnum.ZOOM);
            String msg;
            if (res.isSuccess()) {
                redisService.set(redisKey, 0);
                msg = WebSocketRes.ok().msg("geoai_uos_sendMessageByTypeChannel_24").topic(WebSocketTopicEnum.TASK_START_PROGRESS_RESULT).uuid(nestUuid).toJSONString();
            } else {
                redisService.incr(redisKey);
                msg = WebSocketRes.err().msg("geoai_uos_sendMessageByTypeChannel_23_WITHPARAM" + "&" + res.getMsg()).topic(WebSocketTopicEnum.TASK_START_PROGRESS_RESULT).uuid(nestUuid).toJSONString();
            }
            redisService.expire(redisKey, 40, TimeUnit.MINUTES);
            ChannelService.sendMessageByType3Channel(nestUuid, msg);
        }
    }

    @Async("pubExecutor")
    @EventListener
    public void mqttTakeOffRecordListen(MqttTakeOffRecordEvent mqttTakeOffRecordEvent) {
        log.info("事件触发起飞录制");
        MqttTakeOffRecordEventDTO source = mqttTakeOffRecordEvent.getSource();
        String nestUuid = source.getNestUuid();
        Integer missionId = source.getMissionId();
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.NEST_TAKE_OFF_RECORDS, nestUuid, missionId);
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            MqttResult<NullParam> result = cm.getCameraManagerCf().startRecord();
            String msg;
            if (result.isSuccess()) {
                redisService.set(redisKey, 0);
                msg = WebSocketRes.ok().msg("起飞录制指令下发成功").topic(WebSocketTopicEnum.TASK_START_PROGRESS_RESULT).uuid(nestUuid).toJSONString();
            } else {
                redisService.incr(redisKey);
                msg = WebSocketRes.err().msg("起飞录制指令下发失败，" + result.getMsg()).topic(WebSocketTopicEnum.TASK_START_PROGRESS_RESULT).uuid(nestUuid).toJSONString();
            }
            redisService.expire(redisKey, 40, TimeUnit.MINUTES);
            ChannelService.sendMessageByType3Channel(nestUuid, msg);
        }
    }

    @Async("pubExecutor")
    @EventListener
    public void nestUnloadBatteryListen(NestUnloadBatteryEvent nestUnloadBatteryEvent) {
        log.info("触发卸载电池事件");
        log.info("开始nestUnloadBatteryListen -> {}",nestUnloadBatteryEvent.toString());
        String nestUuid = nestUnloadBatteryEvent.getSource();
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            Timer timer = new Timer(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    MqttResult<NullParam> mqttRes = cm.getMotorManagerCf().unLoadBattery();
                    if (mqttRes.isSuccess()) {
                        log.info(nestUuid + "卸载电池成功");
                    } else {
                        log.info(nestUuid + "卸载电池失败，" + mqttRes.getMsg());
                    }
                }
            }, 3000);


            //延迟发送关闭遥控器
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    MqttResult<NullParam> result = cm.getRcManagerCf().controllerRcMachineOnOff();
                    if (result.isSuccess()) {
                        log.info("nestUuid:{}关闭遥控器", nestUuid);
                    }
                }
            }, 8000);

            DataTranCompleteListenerFactory.destroy(nestUuid);
        }
        log.info("结束nestUnloadBatteryListen");
    }
}
