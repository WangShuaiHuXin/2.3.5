package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.EarlyWarningKeyEntity;
import com.imapcloud.nest.pojo.dto.EarlyWarningKeyDto;

public interface EarlyWarningKeyService extends IService<EarlyWarningKeyEntity> {
    boolean saveAll(EarlyWarningKeyDto entity);

    boolean saveByUnitId(EarlyWarningKeyDto entity);

    boolean saveByNestId(EarlyWarningKeyEntity entity);
}
