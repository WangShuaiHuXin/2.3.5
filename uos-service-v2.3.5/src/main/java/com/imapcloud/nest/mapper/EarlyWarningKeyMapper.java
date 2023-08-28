package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.model.EarlyWarningKeyEntity;
import org.apache.ibatis.annotations.Param;

public interface EarlyWarningKeyMapper extends BaseMapper<EarlyWarningKeyEntity> {

    void saveEntity(@Param("entity") EarlyWarningKeyEntity entity);

}
