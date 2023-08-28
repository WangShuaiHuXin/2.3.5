package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.imapcloud.nest.enums.MinioEventTypeEnum;
import com.imapcloud.nest.v2.dao.entity.FileEventInfoEntity;
import com.imapcloud.nest.v2.dao.mapper.FileEventInfoMapper;
import com.imapcloud.nest.v2.manager.dataobj.in.FileEventInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.FileEventInfoOutDO;
import com.imapcloud.nest.v2.manager.sql.FileEventInfoManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * impl文件事件信息管理器
 *
 * @author boluo
 * @date 2022-10-27
 */
@Component
public class FileEventInfoManagerImpl implements FileEventInfoManager {

    @Resource
    private FileEventInfoMapper fileEventInfoMapper;

    private List<FileEventInfoOutDO> toFileEventInfoOutDO(List<FileEventInfoEntity> fileEventInfoEntityList) {

        List<FileEventInfoOutDO> outList = Lists.newLinkedList();
        for (FileEventInfoEntity record : fileEventInfoEntityList) {
            FileEventInfoOutDO fileEventInfoOutDO = new FileEventInfoOutDO();
            fileEventInfoOutDO.setId(record.getId());
            fileEventInfoOutDO.setEventTime(record.getEventTime());
            fileEventInfoOutDO.setEventData(record.getEventData());
            fileEventInfoOutDO.setEventStatus(record.getEventStatus());
            fileEventInfoOutDO.setSynStatus(record.getSynStatus());
            fileEventInfoOutDO.setEventType(record.getEventType());
            fileEventInfoOutDO.setBucket(record.getBucket());
            fileEventInfoOutDO.setObject(record.getObject());
            outList.add(fileEventInfoOutDO);
        }
        return outList;
    }

    @Override
    public List<FileEventInfoOutDO> selectNotAnalysisList() {

        List<FileEventInfoEntity> fileEventInfoEntityList = fileEventInfoMapper.selectNotAnalysisList();

        return toFileEventInfoOutDO(fileEventInfoEntityList);
    }

    @Override
    public int batchUpdate(List<FileEventInfoInDO> inDOList) {

        if (CollUtil.isEmpty(inDOList)) {
            return 0;
        }
        List<FileEventInfoEntity> fileEventInfoEntityList = Lists.newLinkedList();
        for (FileEventInfoInDO fileEventInfoInDO : inDOList) {
            FileEventInfoEntity fileEventInfoEntity = new FileEventInfoEntity();
            fileEventInfoEntity.setId(fileEventInfoInDO.getId());
            fileEventInfoEntity.setEventStatus(fileEventInfoInDO.getEventStatus());
            fileEventInfoEntity.setSynStatus(fileEventInfoInDO.getSynStatus());
            fileEventInfoEntity.setEventType(fileEventInfoInDO.getEventType());
            fileEventInfoEntity.setBucket(fileEventInfoInDO.getBucket());
            fileEventInfoEntity.setObject(fileEventInfoInDO.getObject());
            fileEventInfoEntityList.add(fileEventInfoEntity);
        }
        return fileEventInfoMapper.batchUpdate(fileEventInfoEntityList);
    }

    @Override
    public List<FileEventInfoOutDO> selectNotSynList(MinioEventTypeEnum eventType) {
        List<FileEventInfoEntity> fileEventInfoEntityList = fileEventInfoMapper.selectNotSynList(eventType.getCode());
        return toFileEventInfoOutDO(fileEventInfoEntityList);
    }

    @Override
    public int updateSysStatusByIdList(List<Long> idList) {

        LambdaUpdateWrapper<FileEventInfoEntity> updateWrapper = Wrappers.lambdaUpdate(FileEventInfoEntity.class)
                .set(FileEventInfoEntity::getSynStatus, 1)
                .in(FileEventInfoEntity::getId, idList);
        return fileEventInfoMapper.update(null, updateWrapper);
    }

    @Override
    public void delete() {

        fileEventInfoMapper.deleteOther();

        fileEventInfoMapper.deleteSyn();

        fileEventInfoMapper.deleteChunk();
    }
}
