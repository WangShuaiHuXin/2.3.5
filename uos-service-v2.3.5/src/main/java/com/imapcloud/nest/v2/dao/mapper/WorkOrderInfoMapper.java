package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.WorkOrderInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 南海任务工单
 */
@Mapper
public interface WorkOrderInfoMapper extends BaseMapper<WorkOrderInfoEntity> {


    void editOrder(@Param("bean") WorkOrderInfoEntity poToEntity);
}
