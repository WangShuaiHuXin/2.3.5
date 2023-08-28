package com.imapcloud.nest.v2.manager.sql.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imapcloud.nest.v2.dao.entity.WorkOrderInfoEntity;
import com.imapcloud.nest.v2.dao.mapper.WorkOrderInfoMapper;
import com.imapcloud.nest.v2.dao.po.in.OrderInfoInPO;
import com.imapcloud.nest.v2.dao.po.in.QueryOrderInPO;
import com.imapcloud.nest.v2.dao.po.out.NhQueryOrderOutPO;
import com.imapcloud.nest.v2.manager.sql.WorkOrderInfoManager;
import com.imapcloud.nest.v2.service.dto.in.NhOrderInfoInDTO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WorkOrderInfoManagerImpl implements WorkOrderInfoManager {

    @Resource
    private WorkOrderInfoMapper workOrderInfoMapper;

    @Override
    public NhQueryOrderOutPO queryListByCondition(QueryOrderInPO build) {

        LambdaQueryWrapper<WorkOrderInfoEntity> queryWrapper = Wrappers.<WorkOrderInfoEntity>lambdaQuery()
                .eq(ObjectUtils.isNotNull(build.getStatus()), WorkOrderInfoEntity::getOrderStatus, build.getStatus())
                .eq(ObjectUtils.isNotNull(build.getDegree()), WorkOrderInfoEntity::getPriorityDegree, build.getDegree())
                .like(StringUtils.isNotBlank(build.getTitle()), WorkOrderInfoEntity::getTitle, build.getTitle())
                .ge(StringUtils.isNotBlank(build.getBeginTime()), WorkOrderInfoEntity::getCreatedTime, build.getBeginTime())
                .le(StringUtils.isNotBlank(build.getEndTime()), WorkOrderInfoEntity::getCreatedTime, build.getEndTime())
                .eq(StringUtils.isNotBlank(build.getOrgCode()), WorkOrderInfoEntity::getOrgCode, build.getOrgCode())
                .eq(WorkOrderInfoEntity::getDeleted, false)
                .likeRight(StringUtils.isNotBlank(build.getUserOrgCode()), WorkOrderInfoEntity::getOrgCode, build.getUserOrgCode())
                .orderByDesc(WorkOrderInfoEntity::getCreatedTime);
        Page page = workOrderInfoMapper.selectPage(new Page<>(build.getPageNo(), build.getPageSize()), queryWrapper);
        NhQueryOrderOutPO po = new NhQueryOrderOutPO();
        if (page.getTotal() > 0) {
            po.setTotal(page.getTotal());
            List<WorkOrderInfoEntity> records = page.getRecords();
            List<NhQueryOrderOutPO.OrderInfo> collect = records.stream().map(item -> {
                return entityToOutPo(item);
            }).collect(Collectors.toList());
            po.setInfoList(collect);
            return po;
        }
        return null;
    }

    @Override
    public boolean checkNameConfilct(String title, String orgCode,String orderId) {
        LambdaQueryWrapper<WorkOrderInfoEntity> queryWrapper = Wrappers.<WorkOrderInfoEntity>lambdaQuery()
                .eq(WorkOrderInfoEntity::getTitle, title)
                .eq(WorkOrderInfoEntity::getOrgCode, orgCode)
                .eq(WorkOrderInfoEntity::getDeleted, false)
                .ne(StringUtils.isNotBlank(orderId),WorkOrderInfoEntity::getOrderId,orderId);
        int cout = workOrderInfoMapper.selectCount(queryWrapper);
        return cout > 0 ? true : false;
    }

    @Override
    public void saveOrder(OrderInfoInPO infoInPO) {
        WorkOrderInfoEntity entity = poToEntity(infoInPO);
        int insert = workOrderInfoMapper.insert(entity);

    }

    @Override
    public NhQueryOrderOutPO.OrderInfo queryOneById(String orderId) {
        LambdaQueryWrapper<WorkOrderInfoEntity> queryWrapper = Wrappers.<WorkOrderInfoEntity>lambdaQuery().eq(WorkOrderInfoEntity::getOrderId,orderId)
                .eq(WorkOrderInfoEntity::getDeleted,false);
        WorkOrderInfoEntity entity = workOrderInfoMapper.selectOne(queryWrapper);
        if(ObjectUtils.isEmpty(entity)){
            return null;
        }
        NhQueryOrderOutPO.OrderInfo orderInfo = entityToOutPo(entity);
        return orderInfo;
    }

    @Override
    public void editOrder(OrderInfoInPO infoInPO) {
        WorkOrderInfoEntity entity = poToEntity(infoInPO);
        workOrderInfoMapper.editOrder(entity);
    }

    public  NhQueryOrderOutPO.OrderInfo entityToOutPo(WorkOrderInfoEntity item){
        NhQueryOrderOutPO.OrderInfo orderInfo = new NhQueryOrderOutPO.OrderInfo();
        orderInfo.setTitle(item.getTitle());
        orderInfo.setOrgCode(item.getOrgCode());
        orderInfo.setInspectionBeginTime(item.getInsepectionStartTime());
        orderInfo.setInspectionEndTime(item.getInsepectionEndTime());
        orderInfo.setOrderId(item.getOrderId());
        orderInfo.setCreatedTime(item.getCreatedTime());
        orderInfo.setOrderStatus(item.getOrderStatus());
        orderInfo.setDegree(item.getPriorityDegree());
        orderInfo.setOrderType(item.getType());
        orderInfo.setVersionId(item.getVersionId());
        orderInfo.setId(item.getId());
        orderInfo.setCreatorId(item.getCreatorId());
        orderInfo.setVerificationMethod(item.getVerificationMethod());
        orderInfo.setFrequency(item.getInspectionFrequency());
        orderInfo.setDesc(item.getDescription());
        return orderInfo;
    }

    public WorkOrderInfoEntity poToEntity(OrderInfoInPO infoInPO){
        WorkOrderInfoEntity entity = new WorkOrderInfoEntity();
        entity.setOrderId(infoInPO.getOrderId());
        entity.setVersionId(infoInPO.getVersionId()==0?1:infoInPO.getVersionId());
        entity.setTitle(infoInPO.getTitle());
        entity.setOrgCode(infoInPO.getOrgCode());
        entity.setType(infoInPO.getOrderType());
        entity.setPriorityDegree(infoInPO.getDegree());
        entity.setInsepectionStartTime(infoInPO.getBeginTime());
        entity.setInsepectionEndTime(infoInPO.getEndTime());
        entity.setVerificationMethod(infoInPO.getVerificationMethod());
        entity.setInspectionFrequency(infoInPO.getFrequency());
        entity.setDescription(infoInPO.getDesc());
        entity.setOrderStatus(infoInPO.getOrderStatus());
        entity.setDeleted(false);
        entity.setCreatorId(infoInPO.getUserId());
        entity.setModifierId(infoInPO.getUserId());
        entity.setCreatedTime(LocalDateTime.now());
        entity.setModifiedTime(LocalDateTime.now());
        return entity;
    }

}
