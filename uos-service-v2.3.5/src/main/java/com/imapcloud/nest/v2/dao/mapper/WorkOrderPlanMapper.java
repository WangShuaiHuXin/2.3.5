package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.WorkOrderPlanEntity;
import com.imapcloud.nest.v2.dao.po.out.NhOrderPlanOptionOutPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkOrderPlanMapper extends BaseMapper<WorkOrderPlanEntity> {
    List<NhOrderPlanOptionOutPO> listOrderPlanOption(@Param("orderId") String orderId);
}
