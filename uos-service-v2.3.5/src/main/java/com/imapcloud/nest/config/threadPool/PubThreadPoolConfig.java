package com.imapcloud.nest.config.threadPool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author wmin
 */
@Configuration
@Slf4j
public class PubThreadPoolConfig {

    private final int corePoolSize = 4;
    private final int maxPoolSize = 8;
    private final int queueCapacity = 64;
    private final int keepAliveSeconds = 30;
    private final int awaitTerminationSeconds = 30;
    private final String threadNamePrefix = "pubExecutor";


    /**
     * 日志异步保存线程池配置
     *
     * @return
     */
    @Bean("pubExecutor")
    public ThreadPoolTaskExecutor logExecutor() {
        log.info("---------- 线程池开始加载 ----------");
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        // 核心线程池大小
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        // 最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        // 队列容量
        threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
        // 活跃时间
        threadPoolTaskExecutor.setKeepAliveSeconds(keepAliveSeconds);
        // 主线程等待子线程执行时间
        threadPoolTaskExecutor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        // 线程名字前缀
        threadPoolTaskExecutor.setThreadNamePrefix(threadNamePrefix);
        // RejectedExecutionHandler:当pool已经达到max-size的时候，如何处理新任务
        // CallerRunsPolicy:不在新线程中执行任务，而是由调用者所在的线程来执行
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

}
