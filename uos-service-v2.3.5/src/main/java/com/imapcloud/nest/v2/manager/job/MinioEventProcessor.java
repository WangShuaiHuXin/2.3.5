package com.imapcloud.nest.v2.manager.job;

import com.imapcloud.nest.enums.MinioEventTypeEnum;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.service.MinioService;
import com.imapcloud.nest.v2.service.impl.MinioServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * minio解析tag
 *
 * @author boluo
 * @date 2022-10-27
 */
@Slf4j
@Component
public class MinioEventProcessor implements BasicProcessor {

    @Resource
    private MinioService minioService;

    @Resource
    private RedisService redisService;

    @Override
    public ProcessResult process(TaskContext context) throws Exception {

        boolean lock = redisService.tryLock(MinioServiceImpl.TASK_LOCK_KEY, "1", 1, TimeUnit.HOURS);
        if (!lock) {
            return new ProcessResult(false, "任务正在执行，请不要重复执行");
        }
        try {

            OmsLogger omsLogger = context.getOmsLogger();

            omsLogger.info("开始解析eventData");
            int update = minioService.analysisFileEvent();
            while (update != 0) {
                update = minioService.analysisFileEvent();
            }
            omsLogger.info("解析eventData完成");

            omsLogger.info("开始删除eventData");
            try {
                // 删除无用的消息
                minioService.deleteFileEvent();
            } catch (Exception e) {
                log.error("#MinioEventProcessor.process# delete failed", e);
            }
            omsLogger.info("完成删除eventData");

            omsLogger.info("开始解析tag事件");
            int analysisTag = minioService.analysisEvent(MinioEventTypeEnum.PUT_TAGGING);
            while (analysisTag != 0) {
                analysisTag = minioService.analysisEvent(MinioEventTypeEnum.PUT_TAGGING);
            }
            omsLogger.info("解析tag事件完成");

            omsLogger.info("开始解析delete事件");
            int deleteTag = minioService.analysisEvent(MinioEventTypeEnum.DELETE);
            while (deleteTag != 0) {
                deleteTag = minioService.analysisEvent(MinioEventTypeEnum.DELETE);
            }
            omsLogger.info("解析delete事件完成");
            return new ProcessResult(true, "执行成功");
        } finally {
            redisService.releaseLock(MinioServiceImpl.TASK_LOCK_KEY, "1");
        }
    }
}
