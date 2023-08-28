package com.imapcloud.nest.v2.manager.ai;

import com.imapcloud.nest.v2.service.AIAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 等待队列检测器
 *
 * @author Vastfy
 * @date 2022/12/8 9:18
 * @since 2.1.5
 */
@Slf4j
@Component
public class WaitingTaskQueueDetector implements ApplicationRunner {

    @Resource
    private AIAnalysisService aiAnalysisService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(1, r -> new Thread(r, "Waiting-Task-Queue-Detector"));
        executorService.execute(() -> {
            log.info("应用启动完成，尝试初始化AI识别任务运行中队列");
            aiAnalysisService.loadProcessingTasks();
            log.info("初始化AI识别任务运行中队列完成");
            log.info("应用启动完成，尝试初始化AI识别任务等待队列");
            aiAnalysisService.loadWaitingTasks();
            log.info("初始化AI识别任务等待队列完成");
            if(!executorService.isShutdown()){
                log.info("关闭线程池【Waiting-Task-Queue-Detector】");
                executorService.shutdown();
            }
        });

    }

}
