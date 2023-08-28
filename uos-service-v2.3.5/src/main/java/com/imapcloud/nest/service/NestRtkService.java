package com.imapcloud.nest.service;

import com.imapcloud.nest.model.NestRtkEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.utils.RestRes;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zheng
 * @since 2021-09-15
 */
public interface NestRtkService extends IService<NestRtkEntity> {

    /**
     * 获取RTK开关状态
     * @param nestId
     * @return
     */
    RestRes getRtkIsEnable(String nestId);

    /**
     * 打开RTK
     * @param nestId
     * @return
     */
    RestRes openRtk(String nestId);

    /**
     * 关闭RTK
     * @param nestId
     * @return
     */
    RestRes closeRtk(String nestId);

    /**
     * 获取RTK类型
     * @param nestId
     * @return
     */
    RestRes getRtkType(String nestId);

    /**
     * 设置RTK类型
     * @param nestId
     * @param type
     * @return
     */
    RestRes setRtkType(String nestId, Integer type);

    /**
     * 获取RTK账号信息
     * @param nestId
     * @return
     */
    RestRes getRtkInfo(String nestId);

    /**
     * 设置RTK账号信息
     * @param nestRtkEntity
     * @return
     */
    RestRes setRtkInfo(NestRtkEntity nestRtkEntity);

    /**
     * 设置RTK过期时间
     * @param nestId
     * @param expireTime
     * @return
     */
    RestRes setExpireTime(String nestId, LocalDate expireTime);

    /**
     * 获取到期的rtk列表
     * @return
     */
    RestRes getExpireRtkList();
    RestRes getNestExpireRtk(String nestId);

    RestRes drtkPowerSwitch(String nestId);
}
