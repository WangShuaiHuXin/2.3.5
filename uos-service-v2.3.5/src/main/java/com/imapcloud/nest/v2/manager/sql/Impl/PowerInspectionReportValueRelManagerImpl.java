package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.imapcloud.nest.v2.dao.entity.PowerInspectionReportValueRelEntity;
import com.imapcloud.nest.v2.dao.mapper.PowerInspectionReportValueRelMapper;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerInspectionReportValueRelInDO;
import com.imapcloud.nest.v2.manager.sql.PowerInspectionReportValueRelManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PowerInspectionReportValueRelManagerImpl implements PowerInspectionReportValueRelManager {

    @Resource
    private PowerInspectionReportValueRelMapper powerInspectionReportValueRelMapper;

    @Override
    public void saveBatch(List<PowerInspectionReportValueRelInDO> reportValueRelEntities) {
        if (CollectionUtil.isEmpty(reportValueRelEntities)) {
            return;
        }
        List<PowerInspectionReportValueRelEntity> powerInspectionReportValueRelEntities = reportValueRelEntities.stream().map(e -> {
            PowerInspectionReportValueRelEntity entity = new PowerInspectionReportValueRelEntity();
            entity.setInspectionReportId(e.getInspectionReportId());
            entity.setValueId(e.getValueId());
            entity.setCreatorId(e.getCreatorId());
            entity.setModifierId(e.getModifierId());
            entity.setCreatedTime(e.getCreatedTime());
            entity.setModifiedTime(e.getModifiedTime());
            entity.setDeleted(e.getDeleted());
            return entity;
        }).collect(Collectors.toList());
        powerInspectionReportValueRelMapper.saveBatch(powerInspectionReportValueRelEntities);
    }

    @Override
    public List<PowerInspectionReportValueRelEntity> selectByReportIds(List<String> reportIds) {
        LambdaQueryWrapper<PowerInspectionReportValueRelEntity> wapper = Wrappers.<PowerInspectionReportValueRelEntity>lambdaQuery()
                .in(PowerInspectionReportValueRelEntity::getInspectionReportId, reportIds);
        List<PowerInspectionReportValueRelEntity> reportValueRelEntities = powerInspectionReportValueRelMapper.selectList(wapper);
        return reportValueRelEntities;
    }

    @Override
    public void deleteBatch(List<String> ids) {
        LambdaUpdateWrapper<PowerInspectionReportValueRelEntity> updateWrapper = Wrappers.<PowerInspectionReportValueRelEntity>lambdaUpdate()
                .in(PowerInspectionReportValueRelEntity::getInspectionReportId, ids)
                .set(PowerInspectionReportValueRelEntity::getDeleted, true);
        powerInspectionReportValueRelMapper.update(null, updateWrapper);
    }
}
