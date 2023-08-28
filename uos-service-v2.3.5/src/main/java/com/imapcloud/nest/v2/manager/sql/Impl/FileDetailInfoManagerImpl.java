package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.dao.entity.FileDetailInfoEntity;
import com.imapcloud.nest.v2.dao.mapper.FileDetailInfoMapper;
import com.imapcloud.nest.v2.dao.po.in.FileDetailInfoInPO;
import com.imapcloud.nest.v2.manager.dataobj.in.FileDetailInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.FileDetailInfoOutDO;
import com.imapcloud.nest.v2.manager.sql.FileDetailInfoManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 文件详细信息impl
 *
 * @author boluo
 * @date 2022-10-27
 */
@Component
public class FileDetailInfoManagerImpl implements FileDetailInfoManager {

    @Resource
    private FileDetailInfoMapper fileDetailInfoMapper;

    @Override
    public int batchInsert(List<FileDetailInfoInDO> fileDetailInfoInDOList) {
        if (CollUtil.isEmpty(fileDetailInfoInDOList)) {
            return 0;
        }
        List<FileDetailInfoEntity> fileDetailInfoEntityList = Lists.newLinkedList();
        for (FileDetailInfoInDO infoInDO : fileDetailInfoInDOList) {

            LambdaQueryWrapper<FileDetailInfoEntity> queryWrapper = Wrappers.lambdaQuery(FileDetailInfoEntity.class)
                    .eq(FileDetailInfoEntity::getBucket, infoInDO.getBucket())
                    .eq(FileDetailInfoEntity::getObject, infoInDO.getObject())
                    .eq(FileDetailInfoEntity::getTagVersion, infoInDO.getTagVersion());
            // 过滤掉已存在的
            List<FileDetailInfoEntity> entityList = fileDetailInfoMapper.selectList(queryWrapper);
            if (CollUtil.isNotEmpty(entityList)) {
                continue;
            }

            FileDetailInfoEntity fileDetailInfoEntity = new FileDetailInfoEntity();
            fileDetailInfoEntity.setBucket(infoInDO.getBucket());
            fileDetailInfoEntity.setObject(infoInDO.getObject());
            fileDetailInfoEntity.setEventType(infoInDO.getEventType());
            fileDetailInfoEntity.setObjectType(infoInDO.getObjectType());
            fileDetailInfoEntity.setApp(infoInDO.getApp());
            fileDetailInfoEntity.setOrgCode(infoInDO.getOrgCode());
            fileDetailInfoEntity.setNestId(infoInDO.getNestId());
            fileDetailInfoEntity.setObjectSize(infoInDO.getObjectSize());
            fileDetailInfoEntity.setObjectTime(infoInDO.getObjectTime());
            fileDetailInfoEntity.setTagVersion(infoInDO.getTagVersion());
            fileDetailInfoEntity.setSynStatus(infoInDO.getSynStatus());
            fileDetailInfoEntityList.add(fileDetailInfoEntity);
        }
        if (CollUtil.isNotEmpty(fileDetailInfoEntityList)) {
            return fileDetailInfoMapper.batchInsert(fileDetailInfoEntityList);
        }
        return 0;
    }

    @Override
    public int batchUpdate(List<FileDetailInfoInDO> fileDetailInfoInDOList) {

        if (CollUtil.isEmpty(fileDetailInfoInDOList)) {
            return 0;
        }
        List<FileDetailInfoEntity> fileDetailInfoEntityList = Lists.newLinkedList();
        for (FileDetailInfoInDO infoInDO : fileDetailInfoInDOList) {
            FileDetailInfoEntity fileDetailInfoEntity = new FileDetailInfoEntity();
            fileDetailInfoEntity.setBucket(infoInDO.getBucket());
            fileDetailInfoEntity.setObject(infoInDO.getObject());
            fileDetailInfoEntity.setEventType(infoInDO.getEventType());
            fileDetailInfoEntity.setSynStatus(infoInDO.getSynStatus());
            fileDetailInfoEntityList.add(fileDetailInfoEntity);
        }
        return fileDetailInfoMapper.batchUpdate(fileDetailInfoEntityList);
    }

    @Override
    public List<FileDetailInfoOutDO.NotSynInfoOutDO> selectNotSynListByTagVersion(int tagVersion) {

        List<Map<String, String>> mapList = fileDetailInfoMapper.selectNotSynListByTagVersion(tagVersion);
        if (CollUtil.isEmpty(mapList)) {
            return Collections.emptyList();
        }

        return JSONUtil.toList(JSONUtil.parseArray(mapList), FileDetailInfoOutDO.NotSynInfoOutDO.class);
    }

    @Override
    public List<FileDetailInfoOutDO.DayReportOutDO> dayReport(FileDetailInfoInDO.DayReportInDO dayReportInDO) {

        String startTime = String.format("%s 00:00:00", dayReportInDO.getReportDay());
        String endTime = String.format("%s 23:59:59", dayReportInDO.getReportDay());

        FileDetailInfoInPO.DayReportInPO dayReportInPO = new FileDetailInfoInPO.DayReportInPO();
        dayReportInPO.setOrgCode(dayReportInDO.getOrgCode());
        dayReportInPO.setNestId(dayReportInDO.getNestId());
        dayReportInPO.setTagVersion(dayReportInDO.getTagVersion());
        dayReportInPO.setStartTime(startTime);
        dayReportInPO.setEndTime(endTime);
        List<Map<String, Object>> mapList = fileDetailInfoMapper.dayReport(dayReportInPO);
        if (CollUtil.isEmpty(mapList)) {
            return Collections.emptyList();
        }
        List<FileDetailInfoOutDO.DayReportOutDO> dayReportOutDOList = Lists.newLinkedList();
        for (Map<String, Object> stringStringMap : mapList) {

            FileDetailInfoOutDO.DayReportOutDO reportOutDO = new FileDetailInfoOutDO.DayReportOutDO();
            reportOutDO.setObjectType(stringStringMap.get("objectType").toString());
            BigDecimal objectSize = (BigDecimal) stringStringMap.get("objectSize");
            reportOutDO.setObjectSize(objectSize.longValue());
            dayReportOutDOList.add(reportOutDO);
        }
        return dayReportOutDOList;
    }

    @Override
    public int updateSyn(FileDetailInfoInDO.SynInDO synInDO) {

        String startTime = String.format("%s 00:00:00", synInDO.getReportDay());
        String endTime = String.format("%s 23:59:59", synInDO.getReportDay());
        LambdaUpdateWrapper<FileDetailInfoEntity> between = Wrappers.lambdaUpdate(FileDetailInfoEntity.class)
                .set(FileDetailInfoEntity::getSynStatus, 1)
                .eq(FileDetailInfoEntity::getOrgCode, synInDO.getOrgCode())
                .eq(FileDetailInfoEntity::getNestId, synInDO.getNestId())
                .eq(FileDetailInfoEntity::getTagVersion, synInDO.getTagVersion())
                .between(FileDetailInfoEntity::getObjectTime, startTime, endTime);
        return fileDetailInfoMapper.update(null, between);
    }
}
