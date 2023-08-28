package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.EarlyWarningUnitEntity;
import com.imapcloud.nest.v2.service.dto.UnitEntityDTO;

import java.util.List;

/**
 * 天气/区域警告配置与单位关联 服务层
 */
public interface EarlyWarningUnitService extends IService<EarlyWarningUnitEntity> {

    /**
     * 根据预警id查询单位列表
     * @param earlyWarningId 预警对象id
     * @return List<SysUnitEntity>
     */
    List<UnitEntityDTO> getUnitByEarlyWarningId(Integer earlyWarningId);

}
