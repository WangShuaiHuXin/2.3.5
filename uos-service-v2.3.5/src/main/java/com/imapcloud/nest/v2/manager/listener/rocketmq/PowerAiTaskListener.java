package com.imapcloud.nest.v2.manager.listener.rocketmq;

import cn.hutool.json.JSONUtil;
import com.geoai.rocketmq.spring.annotation.RocketMQMessageListener;
import com.geoai.rocketmq.spring.core.MessageMetadata;
import com.geoai.rocketmq.spring.core.RocketMQListener;
import com.imapcloud.nest.enums.RoleIdenValueEnum;
import com.imapcloud.nest.utils.redis.RedisKeyEnum;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.manager.dataobj.in.RocketmqInDO;
import com.imapcloud.nest.v2.service.PowerDefectService;
import com.imapcloud.nest.v2.service.PowerInfraredService;
import com.imapcloud.nest.v2.service.RocketmqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 电力分析 发送UDA前的业务操作
 *
 * @author boluo
 * @date 2023-03-01
 */
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "geoai-uos-service", topic = "${geoai.uos.rocketmq.powerTask-topic}", awaitMills = 2000)
public class PowerAiTaskListener implements RocketMQListener<RocketmqInDO.MqInfo> {

    @Resource
    private PowerInfraredService powerInfraredService;

    @Resource
    private PowerDefectService powerDefectService;

    @Resource
    private RedisService redisService;

    @Resource
    private RocketmqService rocketmqService;

    @Override
    public void onMessage(RocketmqInDO.MqInfo mqInfo, MessageMetadata messageMetadata) {

        try {
            log.info("#PowerAiTaskListener.onMessage# mqInfo={}", mqInfo);
            RocketmqInDO.TaskMqInfo taskMqInfo;
            try {
                taskMqInfo = JSONUtil.toBean(JSONUtil.toJsonStr(mqInfo.getData())
                        , RocketmqInDO.TaskMqInfo.class);
            } catch (Exception e) {
                log.error("#PowerAiTaskListener.onMessage# mqInfo={}", mqInfo, e);
                return;
            }
            log.info("#PowerAiTaskListener.onMessage# dataId={}", taskMqInfo.getDataId());

            taskMqInfo.setNum(taskMqInfo.getNum() + 1);
            String lockKey = RedisKeyEnum.powerLockKey(taskMqInfo.getDataId());
            try {
                if (redisService.tryLock(lockKey, "1", 30, TimeUnit.SECONDS)) {
                    if (taskMqInfo.getRoleIdenValueEnum() == RoleIdenValueEnum.ABNORMAL_FIND_DL_HWCW_NEW) {
                        powerInfraredService.infrared(taskMqInfo);
                    } else if (taskMqInfo.getRoleIdenValueEnum() == RoleIdenValueEnum.ABNORMAL_FIND_DL_QXSB_NEW) {
                        powerDefectService.defect(taskMqInfo);
                    }
                } else {
                    rocketmqService.sendTask(taskMqInfo);
                }
            } finally {
                redisService.releaseLock(lockKey, "1");
            }
        } catch (Exception e) {
            log.error("#PowerAiTaskListener.onMessage# mqInfo={}", mqInfo, e);
        }
    }
}
