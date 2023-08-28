package com.imapcloud.nest.v2.manager.feign.config;

import com.imapcloud.nest.v2.manager.feign.interceptor.TokenRelayInterceptor;
import org.springframework.context.annotation.Bean;

/**
 * token中继feign配置
 *
 * @author Vastfy
 * @date 2022/5/23 16:33
 * @since 2.0.0
 */
public class TokenRelayConfiguration {

    @Bean
    public TokenRelayInterceptor tokenRelayInterceptor(){
        return new TokenRelayInterceptor();
    }

}
