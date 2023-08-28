package com.imapcloud.nest.v2.manager.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;

/**
 * 抽象Feign降级处理器
 * @author Vastfy
 * @date 2021/11/11 11:11
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractFeignFallbackFactory<T> implements org.springframework.cloud.openfeign.FallbackFactory<T> {

    private String getErrorMessage(Throwable original) {
        log.error("异常", original);
        Throwable throwable = NestedExceptionUtils.getMostSpecificCause(original);
        String errorMsg = throwable.getMessage();
        /*if (throwable instanceof HystrixTimeoutException) {
            errorMsg = "Request timeout";
        }*/
        return errorMsg;
    }

    @Override
    public T create(Throwable cause) {
        String errorMessage = getErrorMessage(cause);
        log.error("服务调用失败，原因：{}", errorMessage);
        return doFallbackHandle(errorMessage);
    }

    /**
     * 降级处理
     * @param errMessage 错误消息
     * @return  降级处理类
     */
    protected abstract T doFallbackHandle(String errMessage);

}
