package com.imapcloud.nest.v2.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义线程池配置类
 * @author Vastfy
 * @date 2022/1/03 16:11
 * @since 2.1.4
 */
@Configuration
public class ExecutorConfiguration {

    /**
     * 业务异步线程池
     * @return 业务异步线程池
     */
    @Bean("bizAsyncExecutor")
    public ThreadPoolTaskExecutor bizAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(12);
        executor.setKeepAliveSeconds(60);
        executor.setQueueCapacity(128);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix("biz-event-");
        // 告诉线程池，在销毁之前执行shutdown方法
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // shutdown/shutdownNow 之后等待3秒
        executor.setAwaitTerminationSeconds(3);
        return executor;
    }

}
