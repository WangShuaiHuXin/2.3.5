package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.util.BizIdUtils;
import com.imapcloud.nest.v2.dao.entity.WorkOrderReportEntity;
import com.imapcloud.nest.v2.dao.mapper.WorkOrderReportMapper;
import com.imapcloud.nest.v2.dao.po.in.OrderReportInPO;
import com.imapcloud.nest.v2.dao.po.out.OrderReportOutPO;
import com.imapcloud.nest.v2.manager.sql.WorkOrderReportManager;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class WorkOrderReportManagerImpl implements WorkOrderReportManager {
    @Resource
    private WorkOrderReportMapper workOrderReportMapper;

    @Override
    public void saveOne(OrderReportInPO build) {
        WorkOrderReportEntity workOrderReportEntity = new WorkOrderReportEntity();
        workOrderReportEntity.setOrderId(build.getOrderId());
        workOrderReportEntity.setReportId(BizIdUtils.snowflakeIdStr());
        workOrderReportEntity.setReportName(build.getName());
        workOrderReportEntity.setPath(build.getPath());
        workOrderReportEntity.setDeleted(false);
        workOrderReportEntity.setCreatedTime(LocalDateTime.now());
        workOrderReportEntity.setCreatorId(build.getUserId());
        workOrderReportEntity.setModifierId(build.getUserId());
        workOrderReportEntity.setModifiedTime(LocalDateTime.now());
        workOrderReportMapper.insert(workOrderReportEntity);
    }

    @Override
    public boolean updateToDel(String reportId) {
        LambdaUpdateWrapper<WorkOrderReportEntity> wrapper = Wrappers.<WorkOrderReportEntity>lambdaUpdate()
                .eq(WorkOrderReportEntity::getReportId, reportId)
                .set(WorkOrderReportEntity::getDeleted, true);
        int update = workOrderReportMapper.update(null, wrapper);
        return update == 1 ? true : false;
    }

    @Override
    public List<OrderReportOutPO> selectByCondition(OrderReportInPO build) {
        LambdaQueryWrapper<WorkOrderReportEntity> wrapper = Wrappers.<WorkOrderReportEntity>lambdaQuery()
                .eq(StringUtils.isNotBlank(build.getReportId()), WorkOrderReportEntity::getReportId, build.getReportId())
                .eq(StringUtils.isNotBlank(build.getOrderId()), WorkOrderReportEntity::getOrderId, build.getOrderId())
                .eq(WorkOrderReportEntity::getDeleted, false)
                .orderByDesc(WorkOrderReportEntity::getCreatedTime);
        List<WorkOrderReportEntity> workOrderReportEntities = workOrderReportMapper.selectList(wrapper);
        Optional<List<WorkOrderReportEntity>> optional = Optional.ofNullable(workOrderReportEntities);
        if (optional.isPresent()) {
            List<OrderReportOutPO> collect = workOrderReportEntities.stream().map(item -> toOutPo(item)).collect(Collectors.toList());
            return collect;
        }
        return Collections.EMPTY_LIST;
    }

    public OrderReportOutPO toOutPo(WorkOrderReportEntity entity) {
        OrderReportOutPO outPO = new OrderReportOutPO();
        outPO.setOrderId(entity.getOrderId());
        outPO.setReportId(entity.getReportId());
        outPO.setReportName(entity.getReportName());
        outPO.setPath(entity.getPath());
        outPO.setDeleted(entity.getDeleted());
        outPO.setCreatedTime(entity.getCreatedTime());
        outPO.setCreatorId(entity.getCreatorId());
        outPO.setModifierId(entity.getModifierId());
        outPO.setModifiedTime(entity.getModifiedTime());
        return outPO;
    }
}
