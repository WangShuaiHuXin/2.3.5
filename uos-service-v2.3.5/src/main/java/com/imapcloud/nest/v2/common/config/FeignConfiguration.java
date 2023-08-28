package com.imapcloud.nest.v2.common.config;

import org.springframework.cloud.commons.httpclient.OkHttpClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * feign配置
 *
 * @author Vastfy
 * @date 2022/9/5 18:19
 * @since 2.0.0
 */
@Configuration
public class FeignConfiguration {

    @Bean
    public okhttp3.OkHttpClient okHttpClient(OkHttpClientFactory okHttpClientFactory) {
        // spring-cloud-commons包中自带的有禁用ssl的 OkHttpClient.Builder，无需手动创建
        return okHttpClientFactory.createBuilder(false).build();
    }

}
