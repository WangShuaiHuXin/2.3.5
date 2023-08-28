package com.imapcloud.nest.config.threadPool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.*;

/**
 * Created by wmin on 2020/11/24 14:46
 * 定时器线程池配置
 *
 * @author wmin
 */
@Configuration
public class ScheduleConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setScheduler(uosTaskExecutor());
    }


    @Bean(destroyMethod = "shutdown")
    public Executor uosTaskExecutor() {
        return Executors.newScheduledThreadPool(32);
    }

}
