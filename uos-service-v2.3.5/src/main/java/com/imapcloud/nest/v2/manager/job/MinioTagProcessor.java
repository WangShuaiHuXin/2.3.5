package com.imapcloud.nest.v2.manager.job;

import cn.hutool.core.util.NumberUtil;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.service.MinioService;
import com.imapcloud.nest.v2.service.impl.MinioServiceImpl;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * minio标签处理器
 *
 * @author boluo
 * @date 2022-11-01
 */
@Component
public class MinioTagProcessor implements BasicProcessor {

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
            omsLogger.info("MinioTagProcessor start to process, current JobParams is {}.", context.getJobParams());

            // 解析参数
            String jobParams = context.getJobParams();
            if (!NumberUtil.isNumber(jobParams)) {
                return new ProcessResult(false, "tag版本是数字，请检查参数。");
            }
            int tagVersion = Integer.parseInt(jobParams);
            if (tagVersion <= 0) {
                return new ProcessResult(false, "tag版本是大于0的数字。");
            }
            omsLogger.info("missionPhoto设置tag开始");
            // 查询mission_photo
            int update = minioService.missionPhoto(tagVersion);
            while (update != 0) {
                update = minioService.missionPhoto(tagVersion);
            }
            omsLogger.info("missionPhoto设置tag成功");

            omsLogger.info("missionVideo设置tag开始");
            // 查询mission_video
            int video = minioService.missionVideo(tagVersion);
            while (video != 0) {
                video = minioService.missionVideo(tagVersion);
            }
            omsLogger.info("missionVideo设置tag成功");

            omsLogger.info("mission_video_photo设置tag开始");
            // 查询mission_video_photo
            int videoPhoto = minioService.missionVideoPhoto(tagVersion);
            while (videoPhoto != 0) {
                videoPhoto = minioService.missionVideoPhoto(tagVersion);
            }
            omsLogger.info("mission_video_photo设置tag成功");
            return new ProcessResult(true, "执行成功");
        } finally {
            redisService.releaseLock(MinioServiceImpl.TASK_LOCK_KEY, "1");
        }
    }
}
