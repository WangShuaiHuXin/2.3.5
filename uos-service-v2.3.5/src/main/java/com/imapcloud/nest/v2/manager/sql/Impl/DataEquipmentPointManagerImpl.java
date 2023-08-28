package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.util.BizIdUtils;
import com.imapcloud.nest.v2.dao.entity.DataEquipmentPointEntity;
import com.imapcloud.nest.v2.dao.entity.DataEquipmentPointRelEntity;
import com.imapcloud.nest.v2.dao.entity.PowerEquipmentLegerInfoEntity;
import com.imapcloud.nest.v2.dao.mapper.DataEquipmentPointMapper;
import com.imapcloud.nest.v2.dao.mapper.DataEquipmentPointRelMapper;
import com.imapcloud.nest.v2.manager.dataobj.in.DataEquipmentPintRelQueryInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.DataEquipmentPointInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.DataEquipmentPointQueryInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.DataEquipmentPintRelQueryOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.DataEquipmentPointQueryOutDO;
import com.imapcloud.nest.v2.manager.sql.DataEquipmentPointManager;
import com.imapcloud.nest.v2.manager.sql.PowerEquipmentLegerInfoManager;
import com.imapcloud.nest.v2.service.dto.out.DataEquipmentPointQueryOutDTO;
import com.imapcloud.sdk.utils.StringUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Transactional
public class DataEquipmentPointManagerImpl implements DataEquipmentPointManager {

    @Resource
    private DataEquipmentPointMapper dataEquipmentPointMapper;

    @Resource
    private DataEquipmentPointRelMapper dataEquipmentPointRelMapper;

    @Resource
    private PowerEquipmentLegerInfoManager powerEquipmentLegerInfoManager;

    @Override
    public boolean save(DataEquipmentPointInDO inDO) {
        String bizId = BizIdUtils.snowflakeIdStr();
        DataEquipmentPointEntity equipmentPointEntity = new DataEquipmentPointEntity();
        equipmentPointEntity.setId(null);
        equipmentPointEntity.setPointId(bizId);
        equipmentPointEntity.setPointName(inDO.getPointName());
        equipmentPointEntity.setTagId(inDO.getTagId());
        equipmentPointEntity.setPointLongitude(inDO.getLng());
        equipmentPointEntity.setPointLatitude(inDO.getLat());
        equipmentPointEntity.setPointHeight(inDO.getHeight());
        equipmentPointEntity.setPanoramaDistance(inDO.getPanoramaDis());
        equipmentPointEntity.setGroundDistance(inDO.getGroundDis());
        equipmentPointEntity.setBrief(inDO.getBrief());
        equipmentPointEntity.setOrgCode(inDO.getOrgCode());
        equipmentPointEntity.setCreatorId(TrustedAccessTracerHolder.get().getAccountId());
        equipmentPointEntity.setModifierId(TrustedAccessTracerHolder.get().getAccountId());
        equipmentPointEntity.setCreatedTime(LocalDateTime.now());
        equipmentPointEntity.setModifiedTime(LocalDateTime.now());
        equipmentPointEntity.setDeleted(false);

        List<String> equipmentList = inDO.getEquipmentList();
        List<DataEquipmentPointRelEntity> collect = equipmentList.stream().map(e -> {
            DataEquipmentPointRelEntity dataEquipmentPointRelEntity = new DataEquipmentPointRelEntity();
            dataEquipmentPointRelEntity.setPointId(bizId);
            dataEquipmentPointRelEntity.setEquipmentId(e);
            dataEquipmentPointRelEntity.setOrgCode(inDO.getOrgCode());
            dataEquipmentPointRelEntity.setCreatorId(TrustedAccessTracerHolder.get().getAccountId());
            dataEquipmentPointRelEntity.setModifierId(TrustedAccessTracerHolder.get().getAccountId());
            dataEquipmentPointRelEntity.setCreatedTime(LocalDateTime.now());
            dataEquipmentPointRelEntity.setModifiedTime(LocalDateTime.now());
            dataEquipmentPointRelEntity.setDeleted(false);
            return dataEquipmentPointRelEntity;
        }).collect(Collectors.toList());
        //保存设备点信息
        int insert = dataEquipmentPointMapper.insert(equipmentPointEntity);
        //保存映射关系
        if (insert > 0) {
            dataEquipmentPointRelMapper.saveBatch(collect);
            return true;
        }
        return false;
    }

    @Override
    public boolean update(DataEquipmentPointInDO inDO) {
        DataEquipmentPointEntity equipmentPointEntity = new DataEquipmentPointEntity();
        equipmentPointEntity.setPointId(inDO.getPointId());
        equipmentPointEntity.setPointName(inDO.getPointName());
        equipmentPointEntity.setTagId(inDO.getTagId());
        equipmentPointEntity.setPointLongitude(inDO.getLng());
        equipmentPointEntity.setPointLatitude(inDO.getLat());
        equipmentPointEntity.setPointHeight(inDO.getHeight());
        equipmentPointEntity.setPanoramaDistance(inDO.getPanoramaDis());
        equipmentPointEntity.setGroundDistance(inDO.getGroundDis());
        equipmentPointEntity.setBrief(inDO.getBrief());
        equipmentPointEntity.setOrgCode(inDO.getOrgCode());
        equipmentPointEntity.setCreatorId(TrustedAccessTracerHolder.get().getAccountId());
        equipmentPointEntity.setModifierId(TrustedAccessTracerHolder.get().getAccountId());
        equipmentPointEntity.setCreatedTime(LocalDateTime.now());
        equipmentPointEntity.setModifiedTime(LocalDateTime.now());
        equipmentPointEntity.setDeleted(false);

        List<String> equipmentList = inDO.getEquipmentList();
        List<DataEquipmentPointRelEntity> collect = equipmentList.stream().map(e -> {
            DataEquipmentPointRelEntity dataEquipmentPointRelEntity = new DataEquipmentPointRelEntity();
            dataEquipmentPointRelEntity.setPointId(inDO.getPointId());
            dataEquipmentPointRelEntity.setEquipmentId(e);
            dataEquipmentPointRelEntity.setOrgCode(inDO.getOrgCode());
            dataEquipmentPointRelEntity.setCreatorId(TrustedAccessTracerHolder.get().getAccountId());
            dataEquipmentPointRelEntity.setModifierId(TrustedAccessTracerHolder.get().getAccountId());
            dataEquipmentPointRelEntity.setCreatedTime(LocalDateTime.now());
            dataEquipmentPointRelEntity.setModifiedTime(LocalDateTime.now());
            dataEquipmentPointRelEntity.setDeleted(false);
            return dataEquipmentPointRelEntity;
        }).collect(Collectors.toList());
        //更新设备点信息
        dataEquipmentPointMapper.updateOne(equipmentPointEntity);
        //更新关联设备信息
        List<String> delete = new ArrayList<>();
        delete.add(inDO.getPointId());
        dataEquipmentPointRelMapper.deleteBatch(delete);
        //先删除原有的再批量重新插入
        dataEquipmentPointRelMapper.saveBatch(collect);
        return true;
    }

    @Override
    public boolean deleteBatch(List<String> deletes) {
        dataEquipmentPointMapper.deleteBatch(deletes);
        dataEquipmentPointRelMapper.deleteBatch(deletes);
        return true;
    }

    @Override
    public DataEquipmentPointQueryOutDO queryByCondition(DataEquipmentPointQueryInDO build) {
        DataEquipmentPointQueryOutDO queryOutDO = new DataEquipmentPointQueryOutDO();
        List<DataEquipmentPointEntity> dataEquipmentPointEntities = new ArrayList<>();
        LambdaQueryWrapper<DataEquipmentPointEntity> wrapper = Wrappers.lambdaQuery(DataEquipmentPointEntity.class)
                .like(StringUtils.isNotEmpty(build.getKeyWord()), DataEquipmentPointEntity::getPointName, build.getKeyWord())
                .eq(DataEquipmentPointEntity::getDeleted, false)
                .eq(StringUtils.isNotEmpty(build.getPointId()), DataEquipmentPointEntity::getPointId, build.getPointId())
                .eq(StringUtils.isNotEmpty(build.getTagId()), DataEquipmentPointEntity::getTagId, build.getTagId())
                .eq(StringUtils.isNotEmpty(build.getOrgCode()), DataEquipmentPointEntity::getOrgCode, build.getOrgCode())
                .orderByDesc(DataEquipmentPointEntity::getCreatedTime);
        if (ObjectUtils.isNotEmpty(build.getPageNo())) {
            Page<DataEquipmentPointEntity> dataEquipmentPointEntityPage = dataEquipmentPointMapper.selectPage(new Page<>(build.getPageNo(), build.getPageSize()), wrapper);
            if (CollectionUtil.isNotEmpty(dataEquipmentPointEntityPage.getRecords())) {
                dataEquipmentPointEntities = dataEquipmentPointEntityPage.getRecords();
                queryOutDO.setTotal(dataEquipmentPointEntityPage.getTotal());
            } else {
                queryOutDO.setTotal(0L);
            }
        } else {
            dataEquipmentPointEntities = dataEquipmentPointMapper.selectList(wrapper);
            queryOutDO.setTotal((long) dataEquipmentPointEntities.size());
        }
        if (queryOutDO.getTotal() == 0) {
            return queryOutDO;
        }

        //查询对应设备id
        List<String> pointIds = dataEquipmentPointEntities.stream().map(DataEquipmentPointEntity::getPointId).collect(Collectors.toList());
        LambdaQueryWrapper<DataEquipmentPointRelEntity> queryWrapper = Wrappers.lambdaQuery(DataEquipmentPointRelEntity.class)
                .eq(DataEquipmentPointRelEntity::getDeleted, false)
                .in(DataEquipmentPointRelEntity::getPointId, pointIds);
        List<DataEquipmentPointRelEntity> dataEquipmentPointRelEntities = dataEquipmentPointRelMapper.selectList(queryWrapper);
        //全部设备ID
        List<String> equipmentIds = dataEquipmentPointRelEntities.stream().map(DataEquipmentPointRelEntity::getEquipmentId).collect(Collectors.toList());
        List<PowerEquipmentLegerInfoEntity> powerEquipmentLegerInfoEntities = powerEquipmentLegerInfoManager.queryEquipmentByIds(equipmentIds);
        Map<String, String> equipmentInfos = powerEquipmentLegerInfoEntities.stream().collect(Collectors.toMap(PowerEquipmentLegerInfoEntity::getEquipmentId, PowerEquipmentLegerInfoEntity::getEquipmentName));
        //根据pointId分组
        Map<String, List<DataEquipmentPointRelEntity>> collect = dataEquipmentPointRelEntities.stream().collect(Collectors.groupingBy(DataEquipmentPointRelEntity::getPointId));


        List<DataEquipmentPointQueryOutDO.DataEquipmentPointQueryInfoOutDO> resultInfos = dataEquipmentPointEntities.stream().map(e -> {
            DataEquipmentPointQueryOutDO.DataEquipmentPointQueryInfoOutDO dto = new DataEquipmentPointQueryOutDO.DataEquipmentPointQueryInfoOutDO();
            dto.setPointId(e.getPointId());
            dto.setLng(e.getPointLongitude());
            dto.setLat(e.getPointLatitude());
            dto.setHeight(e.getPointHeight());
            dto.setPointName(e.getPointName());
            dto.setOrgCode(e.getOrgCode());
            dto.setPanoramaDis(e.getPanoramaDistance());
            dto.setGroundDis(e.getGroundDistance());
            dto.setTagId(e.getTagId());
            dto.setBrief(e.getBrief());
            List<DataEquipmentPointRelEntity> equipmentPointRelEntities = collect.get(e.getPointId());
            if (CollectionUtil.isNotEmpty(equipmentPointRelEntities)) {
                List<DataEquipmentPointQueryOutDO.DataEquipmentInfoOutDO> equipmentQueryInfoDTOS = equipmentPointRelEntities.stream().map(item -> {
                    DataEquipmentPointQueryOutDO.DataEquipmentInfoOutDO dataEquipmentQueryInfoDTO = new DataEquipmentPointQueryOutDO.DataEquipmentInfoOutDO();
                    dataEquipmentQueryInfoDTO.setEquipmentId(item.getEquipmentId());
                    dataEquipmentQueryInfoDTO.setEquipmentName(equipmentInfos.get(item.getEquipmentId()));
                    return dataEquipmentQueryInfoDTO;
                }).collect(Collectors.toList());
                dto.setEquipmentList(equipmentQueryInfoDTOS);
            }
            return dto;
        }).collect(Collectors.toList());
        queryOutDO.setDtos(resultInfos);
        return queryOutDO;
    }

    @Override
    public List<DataEquipmentPintRelQueryOutDO> queryRelByCondition(DataEquipmentPintRelQueryInDO build) {
        LambdaQueryWrapper<DataEquipmentPointRelEntity> wrapper = Wrappers.lambdaQuery(DataEquipmentPointRelEntity.class)
                .eq(DataEquipmentPointRelEntity::getDeleted, false)
                .eq(StringUtils.isNotEmpty(build.getEquipmentId()), DataEquipmentPointRelEntity::getEquipmentId, build.getEquipmentId())
                .eq(StringUtils.isNotEmpty(build.getOrgCode()), DataEquipmentPointRelEntity::getOrgCode, build.getOrgCode())
                .eq(StringUtils.isNotEmpty(build.getPointId()), DataEquipmentPointRelEntity::getPointId, build.getPointId());
        List<DataEquipmentPointRelEntity> equipmentPointRelEntities = dataEquipmentPointRelMapper.selectList(wrapper);
        return equipmentPointRelEntities.stream().map(e -> {
            DataEquipmentPintRelQueryOutDO outDO = new DataEquipmentPintRelQueryOutDO();
            outDO.setPointId(e.getPointId());
            outDO.setEquipmentId(e.getEquipmentId());
            outDO.setOrgCode(e.getOrgCode());
            return outDO;
        }).collect(Collectors.toList());
    }
}
