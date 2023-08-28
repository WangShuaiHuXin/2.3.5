package com.imapcloud.nest.config.threadPool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

/**
 * 线程池
 *
 * @author daolin
 */
@Configuration
public class TaskExecutor {

    @Bean("executorService")
    public static ExecutorService getExecutor() {
        return new ThreadPoolExecutor(4, 16,
                0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(64), new ThreadPoolExecutor.DiscardPolicy());
    }

    @Bean("wsExecutorService")
    public static ExecutorService wsExecutor() {
        return new ThreadPoolExecutor(4, 50, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(500), new ThreadPoolExecutor.DiscardPolicy());
    }

    @Bean("logExecutorService")
    public static ExecutorService mqttLogExecutor() {
        return new ThreadPoolExecutor(4, 30, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100), new ThreadPoolExecutor.DiscardPolicy());
    }

    @Bean("mqttNestExecutor")
    public ExecutorService mqttNestExecutor() {
        return new ThreadPoolExecutor(16, 30, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100), new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    /**
     * AI任务结果 红外处理
     *
     * @return {@link ExecutorService}
     */
    @Bean("taskInfraredResultExecutor")
    public ExecutorService taskInfraredResultExecutor() {
        // 本地红外测温比较慢
        return new ThreadPoolExecutor(5, 10, 60, TimeUnit.SECONDS
                , new ArrayBlockingQueue<>(40), new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    /**
     * AI任务结果 缺陷处理
     *
     * @return {@link ExecutorService}
     */
    @Bean("taskDefectResultExecutor")
    public ExecutorService taskDefectResultExecutor() {
        // 缺陷识别比较快
        return new ThreadPoolExecutor(2, 2, 60, TimeUnit.SECONDS
                , new ArrayBlockingQueue<>(100), new ThreadPoolExecutor.CallerRunsPolicy());
    }
}