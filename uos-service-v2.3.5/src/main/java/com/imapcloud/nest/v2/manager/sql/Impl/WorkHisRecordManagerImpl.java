package com.imapcloud.nest.v2.manager.sql.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.imapcloud.nest.v2.dao.entity.WorkHisRecordEntity;
import com.imapcloud.nest.v2.dao.mapper.WorkHisRecordMapper;
import com.imapcloud.nest.v2.dao.po.in.OrderHisRecordInPO;
import com.imapcloud.nest.v2.dao.po.out.NhQueryHisRecordOutPO;
import com.imapcloud.nest.v2.manager.sql.WorkHisRecordManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WorkHisRecordManagerImpl implements WorkHisRecordManager {

    @Resource
    private WorkHisRecordMapper workHisRecordMapper;

    @Override
    public List<NhQueryHisRecordOutPO> queryExecutingRecords(String orderId) {
        LambdaQueryWrapper wrapper = Wrappers.<WorkHisRecordEntity>lambdaQuery()
                .eq(WorkHisRecordEntity::getOrderId, orderId)
                .eq(WorkHisRecordEntity::getFlag, true)
                .orderByAsc(WorkHisRecordEntity::getProcessCode);
        List<WorkHisRecordEntity> list = workHisRecordMapper.selectList(wrapper);
        List<NhQueryHisRecordOutPO> collect = list.stream().map(item -> {
            NhQueryHisRecordOutPO outPO = new NhQueryHisRecordOutPO();
            outPO.setOrderId(item.getOrderId());
            outPO.setRecordId(item.getRecordId());
            outPO.setFlag(item.getProcessDir());
            outPO.setDesc(item.getDescription());
            outPO.setMark(item.getMark());
            outPO.setOrderStatus(item.getOrderStatus());
            outPO.setCreatorId(item.getCreatorId());
            outPO.setCreatedTime(item.getCreatedTime());
            return outPO;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public boolean checkHisExist(OrderHisRecordInPO inPO) {
        //存在历史节点状态为一致的，那当前操作即为撤回等操作
        LambdaQueryWrapper wrapper = Wrappers.<WorkHisRecordEntity>lambdaQuery()
                .eq(WorkHisRecordEntity::getOrderId, inPO.getOrderId())
                .eq(WorkHisRecordEntity::getOrderStatus, inPO.getOrderStatus())
                .eq(WorkHisRecordEntity::getDeleted, false);
        Integer integer = workHisRecordMapper.selectCount(wrapper);
        return integer > 0 ? false : true;
    }

    @Override
    public void updateDisuseRecord(OrderHisRecordInPO inPO) {
        LambdaUpdateWrapper wrapper = Wrappers.<WorkHisRecordEntity>lambdaUpdate()
                .eq(WorkHisRecordEntity::getOrderId, inPO.getOrderId())
                .eq(WorkHisRecordEntity::getProcessCode, inPO.getProcessCode())
                .eq(WorkHisRecordEntity::getFlag, true)
                .set(WorkHisRecordEntity::getFlag, false);
        workHisRecordMapper.update(null, wrapper);
    }

    @Override
    public void updateProcess(OrderHisRecordInPO inPO,Integer process) {
        LambdaUpdateWrapper wrapper = Wrappers.<WorkHisRecordEntity>lambdaUpdate()
                .eq(WorkHisRecordEntity::getOrderId, inPO.getOrderId())
                .gt(WorkHisRecordEntity::getProcessCode,process)
                .set(WorkHisRecordEntity::getFlag, false);
        workHisRecordMapper.update(null, wrapper);
    }

    @Override
    public void addRecord(OrderHisRecordInPO inPO) {
        WorkHisRecordEntity entity = new WorkHisRecordEntity();
        entity.setOrderId(inPO.getOrderId());
        entity.setRecordId(inPO.getRecordId());
        entity.setFlag(inPO.isFlag());
        entity.setDescription(inPO.getDescription());
        entity.setMark(inPO.getMark());
        entity.setOrderStatus(inPO.getOrderStatus());
        entity.setDeleted(inPO.isDeleted());
        entity.setProcessCode(inPO.getProcessCode());
        entity.setCreatorId(inPO.getCreatorId());
        entity.setModifierId(inPO.getCreatorId());
        entity.setCreatedTime(LocalDateTime.now());
        entity.setModifiedTime(LocalDateTime.now());
        entity.setProcessDir(inPO.isProcessDir());
        workHisRecordMapper.insert(entity);
    }

    @Override
    public List<NhQueryHisRecordOutPO> queryHistRecords(String orderId) {
        LambdaQueryWrapper wrapper = Wrappers.<WorkHisRecordEntity>lambdaQuery()
                .eq(WorkHisRecordEntity::getOrderId, orderId)
                .eq(WorkHisRecordEntity::getDeleted, false)
                .orderByDesc(WorkHisRecordEntity::getCreatedTime);
        List<WorkHisRecordEntity> list = workHisRecordMapper.selectList(wrapper);
        List<NhQueryHisRecordOutPO> collect = list.stream().map(item -> {
            NhQueryHisRecordOutPO outPO = new NhQueryHisRecordOutPO();
            outPO.setOrderId(item.getOrderId());
            outPO.setRecordId(item.getRecordId());
            outPO.setFlag(item.getProcessDir());
            outPO.setDesc(item.getDescription());
            outPO.setMark(item.getMark());
            outPO.setOrderStatus(item.getOrderStatus());
            outPO.setCreatorId(item.getCreatorId());
            outPO.setCreatedTime(item.getCreatedTime());
            return outPO;
        }).collect(Collectors.toList());
        return collect;
    }
}
