package com.imapcloud.nest.v2.common;

import com.geoai.common.core.bean.ITrustedAccessTracer;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.mp.DataOperatorContext;
import com.geoai.common.mp.DataOperatorContextAware;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 默认的操作人接口
 * @author Vastfy
 * @date 2022/5/18 17:33
 * @since 1.0.0
 */
@Component
public class DefaultDataOperatorContextAware implements DataOperatorContextAware {

    @Override
    public DataOperatorContext getDataOperatorContext() {
        ITrustedAccessTracer trustedAccessTracer = TrustedAccessTracerHolder.get();
        // 登录、注册接口由于未被拦截，故不存在该值，需要设置为空
        if(Objects.nonNull(trustedAccessTracer)){
            return trustedAccessTracer::getAccountId;
        }
        return null;
    }
}
