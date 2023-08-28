package com.imapcloud.nest.v2.manager.listener.rocketmq;

import cn.hutool.json.JSONUtil;
import com.geoai.rocketmq.spring.annotation.RocketMQMessageListener;
import com.geoai.rocketmq.spring.core.MessageMetadata;
import com.geoai.rocketmq.spring.core.RocketMQListener;
import com.imapcloud.nest.enums.RoleIdenValueEnum;
import com.imapcloud.nest.utils.redis.RedisKeyEnum;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.dao.entity.PowerMeterFlightDataEntity;
import com.imapcloud.nest.v2.manager.dataobj.in.RocketmqInDO;
import com.imapcloud.nest.v2.manager.sql.PowerMeterDataManager;
import com.imapcloud.nest.v2.service.PowerDefectService;
import com.imapcloud.nest.v2.service.PowerInfraredService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 电力分析 处理uda处理后的结果
 *
 * @author boluo
 * @date 2023-03-06
 */
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "geoai-uos-service", topic = "${geoai.uos.rocketmq.powerTaskResultTopic}", awaitMills = 1000)
public class PowerAiTaskResultListener implements RocketMQListener<String> {

    @Resource
    private PowerMeterDataManager powerMeterDataManager;

    @Resource
    private PowerInfraredService powerInfraredService;

    @Resource
    private PowerDefectService powerDefectService;

    @Resource
    private ExecutorService taskInfraredResultExecutor;

    @Resource
    private ExecutorService taskDefectResultExecutor;

    @Resource
    private RedisService redisService;

    @Override
    public void onMessage(String s, MessageMetadata messageMetadata) {

        log.info("#PowerAiTaskResultListener.onMessage# result={}#", s);
        MqInfo mqInfo;
        RocketmqInDO.BusinessParamInfo businessParamInfo;
        try {
            mqInfo = JSONUtil.toBean(s, MqInfo.class);
            businessParamInfo = JSONUtil.toBean(mqInfo.businessParam, RocketmqInDO.BusinessParamInfo.class);
        } catch (Exception e) {
            log.error("#PowerAiTaskResultListener.onMessage# result={}#", s, e);
            return;
        }
        try {
            PowerMeterFlightDataEntity meterData = powerMeterDataManager.getMeterData(businessParamInfo.getDataId());
            if (meterData == null) {
                return;
            }
            String lockKey = RedisKeyEnum.powerLockKey(businessParamInfo.getDataId());
            if (RoleIdenValueEnum.ABNORMAL_FIND_DL_HWCW_NEW.getIdenValue() == meterData.getIdenValue()) {
                taskInfraredResultExecutor.submit(() -> {
                    try {
                        redisService.lock(lockKey, "1", 60);
                        powerInfraredService.result(mqInfo, businessParamInfo);
                    } finally {
                        redisService.releaseLock(lockKey, "1");
                    }
                });
            } else if (RoleIdenValueEnum.ABNORMAL_FIND_DL_QXSB_NEW.getIdenValue() == meterData.getIdenValue()) {
                taskDefectResultExecutor.submit(() -> {
                    try {
                        redisService.lock(lockKey, "1", 60);
                        powerDefectService.result(mqInfo, businessParamInfo);
                    } finally {
                        redisService.releaseLock(lockKey, "1");
                    }
                });
            }
        } catch (Exception e) {
            log.error("#PowerAiTaskResultListener.onMessage# result={}#", s, e);
        }
    }

    @Data
    public static class MqInfo {

        private String orgCode;

        private String taskId;

        private String pictureUrl;

        /**
         * 业务参数
         */
        private String businessParam;

        private TaskResultInfo taskResultInfoInDTO;
    }

    @Data
    public static class TaskResultInfo {

        /**
         * 任务状态 1成功  2失败  3未授权
         */
        private int taskState;

        private List<MarkInfo> infoInDTOList;
    }

    @Data
    public static class MarkInfo {

        private String functionId;

        /**
         * 问题类型id
         */
        private String problemTypeId;

        /**
         * 问题类型名称
         */
        private String problemTypeName;

        /**
         * 矩形  左上角 x1,y1  右下角 x2,y2
         */
        private BigDecimal x1;
        private BigDecimal y1;
        private BigDecimal x2;
        private BigDecimal y2;
    }

    @Getter
    @AllArgsConstructor
    public enum TaskStateEnum implements Serializable {
        /**
         * 任务识别状态
         */
        SUCCESS(1),
        FAILED(2),
        UNAUTH(3),
        ;
        private int code;
    }
}
