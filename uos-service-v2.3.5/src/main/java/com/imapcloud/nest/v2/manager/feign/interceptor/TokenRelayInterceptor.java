package com.imapcloud.nest.v2.manager.feign.interceptor;

import com.geoai.common.core.bean.ITrustedAccessTracer;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.constant.SecurityConstants;
import com.geoai.common.core.util.JsonUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Token中继处理器拦截器
 * @author Vastfy
 * @date 2022/5/20 10:11
 * @since 2.0.0、
 */
@Slf4j
public class TokenRelayInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 开启了hystrix熔断，会独立子线程，这里获取不到数据，需要升级openfeign版本到3.0.4
        String authorization = Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(it -> ((ServletRequestAttributes) it).getRequest())
                .map(e -> e.getHeader(SecurityConstants.HTTP_HEADER_AUTHORIZATION))
                .orElse(null);
        requestTemplate.header(SecurityConstants.HTTP_HEADER_AUTHORIZATION, authorization);
        String trace = Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(it -> ((ServletRequestAttributes) it).getRequest())
                .map(e -> e.getHeader(SecurityConstants.HTTP_HEADER_ACCESS_TRACE))
                .orElse(null);
        // 防止异步线程，获取不到该值
        if(!StringUtils.hasText(trace)){
            ITrustedAccessTracer accessTracer = TrustedAccessTracerHolder.get();
            // json
            trace = JsonUtils.writeJson(accessTracer);
            // base64
            trace = Base64Utils.encodeToString(trace.getBytes(StandardCharsets.UTF_8));
        }
        requestTemplate.header(SecurityConstants.HTTP_HEADER_ACCESS_TRACE, trace);
    }

}
