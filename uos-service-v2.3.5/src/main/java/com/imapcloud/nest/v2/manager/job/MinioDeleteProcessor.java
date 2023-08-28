package com.imapcloud.nest.v2.manager.job;

import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.service.DJITaskFileService;
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
 * minio删除处理器
 *
 * @author boluo
 * @date 2022-11-01
 */
@Component
public class MinioDeleteProcessor implements BasicProcessor {

    @Resource
    private RedisService redisService;

    @Resource
    private MinioService minioService;

    @Resource
    private DJITaskFileService djiTaskFileService;

    @Override
    public ProcessResult process(TaskContext context) throws Exception {

        boolean lock = redisService.tryLock(MinioServiceImpl.TASK_LOCK_KEY, "1", 1, TimeUnit.HOURS);
        if (!lock) {
            return new ProcessResult(false, "任务正在执行，请不要重复执行");
        }
        try {

            OmsLogger omsLogger = context.getOmsLogger();
//            omsLogger.info("MinioTagProcessor start to process, current JobParams is {}.", context.getJobParams());
//
//            // 解析参数
//            String jobParams = context.getJobParams();
//            if (!NumberUtil.isNumber(jobParams)) {
//                return new ProcessResult(false, "tag版本是数字，请检查参数。");
//            }
//            int tagVersion = Integer.parseInt(jobParams);
//            if (tagVersion <= 0) {
//                return new ProcessResult(false, "tag版本是大于0的数字。");
//            }
            int tagVersion = 0;
            omsLogger.info("开始删除mission_photo");
            // mission_photo物理删除
            int missionPhoto = minioService.physicsDeleteMissionPhoto(tagVersion);
            while (missionPhoto != 0) {
                missionPhoto = minioService.physicsDeleteMissionPhoto(tagVersion);
            }

            omsLogger.info("开始删除mission_video");
            // mission_video物理删除
            int missionVideo = minioService.physicsDeleteMissionVideo(tagVersion);
            while (missionVideo != 0) {
                missionVideo = minioService.physicsDeleteMissionVideo(tagVersion);
            }

            omsLogger.info("开始删除mission_video_photo");
            // mission_video_photo物理删除
            int deleteMissionVideoPhoto = minioService.physicsDeleteMissionVideoPhoto(tagVersion);
            while (deleteMissionVideoPhoto != 0) {
                deleteMissionVideoPhoto = minioService.physicsDeleteMissionVideoPhoto(tagVersion);
            }

            //删除航线包
            boolean bol = this.djiTaskFileService.physicsDeleteKmz();

            omsLogger.info("end");


            return new ProcessResult(true, "执行成功");
        } finally {
            redisService.releaseLock(MinioServiceImpl.TASK_LOCK_KEY, "1");
        }
    }
}
