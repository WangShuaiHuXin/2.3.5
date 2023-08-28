package com.imapcloud.nest.v2.common.utils;

import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName AuditUtils.java
 * @Description AuditUtils
 * @createTime 2022年07月21日 10:01:00
 */
@Slf4j
public class AuditUtils {

    /**
     * 获取审计
     * @return
     */
    public static String getAudit(){
        //TODO 开发环境没有实际登录
//        return Optional.ofNullable(TrustedAccessTracerHolder.get())
//                .map(x->x.getAccountId())
//                .orElseThrow(()->new BusinessException("查询不到对应登录用户信息，请联系管理员"));
        return Optional.ofNullable(TrustedAccessTracerHolder.get())
                .map(x->x.getAccountId())
                .orElseGet(()->"1");
    }

    /**
     *
     * @param id
     * @return
     */
    public static Long getSnowId(Long id){
        if(id!=null){
            return id;
        }
        Long snowflakeId = com.geoai.common.core.util.BizIdUtils.snowflakeId();
        return snowflakeId;
    }
}
