package com.imapcloud.nest.v2.common.exception;

import com.geoai.common.core.exception.BizException;
import com.geoai.common.web.exception.IExceptionHandlerMatcher;
import com.geoai.common.web.rest.CommonErrorCode;
import com.geoai.common.web.rest.IErrorCodeDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * UOS服务异常处理器匹配器
 * @author Vastfy
 * @date 2022/5/5 11:13
 * @since 1.0.0
 */
@Component
public class UosExceptionHandlerMatcher implements IExceptionHandlerMatcher {

    @Override
    public <E extends Exception> IErrorCodeDefinition match(E exception) {
        // 定义基类，此种方式实现要求自定义异常继承BizException并且实现getId()方法
        if(exception instanceof BizException){
            // 基类获取内部排序编码
            BizException ex = (BizException) exception;
            // 优先匹配公共错误码
            Optional<CommonErrorCode> optional = Arrays.stream(CommonErrorCode.values())
                    .filter(e -> Objects.equals(e.toBizErrorCode(), ex.getId()))
                    .findFirst();
            if(optional.isPresent()){
                return optional.get();
            }
            // 遍历错误码定义
            return Arrays.stream(UosServiceErrorCode.values())
                    .filter(e -> Objects.equals(e.toBizErrorCode(), ex.getId()))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

}
