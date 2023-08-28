package com.imapcloud.nest.service;

import com.imapcloud.nest.model.TransmissionTowerEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.utils.RestRes;

/**
 * <p>
 * 输电线路杆塔表 服务类
 * </p>
 *
 * @author wmin
 * @since 2021-08-17
 */
public interface TransmissionTowerService extends IService<TransmissionTowerEntity> {

    /**
     * 通过基站UUID和杆塔UUID创建任务
     *
     * @param towerUuid
     * @param nestUuid
     * @return
     */
    RestRes createTaskByTowerAndNestUuid(String towerUuid, String nestUuid);
}
