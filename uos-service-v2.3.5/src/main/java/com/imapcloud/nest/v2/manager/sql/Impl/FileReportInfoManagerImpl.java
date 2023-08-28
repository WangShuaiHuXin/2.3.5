package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.dao.entity.FileReportInfoEntity;
import com.imapcloud.nest.v2.dao.mapper.FileReportInfoMapper;
import com.imapcloud.nest.v2.dao.po.in.FileReportInfoInPO;
import com.imapcloud.nest.v2.dao.po.out.FileReportInfoOutPO;
import com.imapcloud.nest.v2.manager.dataobj.in.FileReportInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.FileReportInfoOutDO;
import com.imapcloud.nest.v2.manager.sql.FileReportInfoManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * 文件统计
 *
 * @author boluo
 * @date 2022-10-31
 */
@Component
public class FileReportInfoManagerImpl implements FileReportInfoManager {

    @Resource
    private FileReportInfoMapper fileReportInfoMapper;

    @Override
    public int batchInsert(List<FileReportInfoInDO> fileReportInfoInDOList) {

        if (CollUtil.isEmpty(fileReportInfoInDOList)) {
            return 0;
        }
        List<FileReportInfoEntity> insertList = Lists.newLinkedList();
        List<FileReportInfoEntity> updateList = Lists.newLinkedList();
        for (FileReportInfoInDO fileReportInfoInDO : fileReportInfoInDOList) {

            LambdaQueryWrapper<FileReportInfoEntity> queryWrapper = Wrappers.lambdaQuery(FileReportInfoEntity.class)
                    .eq(FileReportInfoEntity::getOrgCode, fileReportInfoInDO.getOrgCode())
                    .eq(FileReportInfoEntity::getNestId, fileReportInfoInDO.getNestId())
                    .eq(FileReportInfoEntity::getTagVersion, fileReportInfoInDO.getTagVersion())
                    .eq(FileReportInfoEntity::getReportDay, fileReportInfoInDO.getReportDay());
            List<FileReportInfoEntity> fileReportInfoEntityList = fileReportInfoMapper.selectList(queryWrapper);
            if (CollUtil.isEmpty(fileReportInfoEntityList)) {

                FileReportInfoEntity fileReportInfoEntity = new FileReportInfoEntity();
                fileReportInfoEntity.setApp(fileReportInfoInDO.getApp());
                fileReportInfoEntity.setOrgCode(fileReportInfoInDO.getOrgCode());
                fileReportInfoEntity.setNestId(fileReportInfoInDO.getNestId());
                fileReportInfoEntity.setPictureSize(fileReportInfoInDO.getPictureSize());
                fileReportInfoEntity.setVideoSize(fileReportInfoInDO.getVideoSize());
                fileReportInfoEntity.setVideoPictureSize(fileReportInfoInDO.getVideoPictureSize());
                fileReportInfoEntity.setTotalSize(fileReportInfoInDO.getTotalSize());
                fileReportInfoEntity.setReportDay(fileReportInfoInDO.getReportDay());
                fileReportInfoEntity.setReportMonth(fileReportInfoInDO.getReportDay().substring(0, 7));
                fileReportInfoEntity.setTagVersion(fileReportInfoInDO.getTagVersion());
                insertList.add(fileReportInfoEntity);
            } else {
                FileReportInfoEntity fileReportInfoEntity = fileReportInfoEntityList.get(0);
                fileReportInfoEntity.setPictureSize(fileReportInfoInDO.getPictureSize());
                fileReportInfoEntity.setVideoSize(fileReportInfoInDO.getVideoSize());
                fileReportInfoEntity.setVideoPictureSize(fileReportInfoInDO.getVideoPictureSize());
                fileReportInfoEntity.setTotalSize(fileReportInfoInDO.getTotalSize());
                updateList.add(fileReportInfoEntity);
            }
        }
        if (CollUtil.isNotEmpty(insertList)) {
            fileReportInfoMapper.batchInsert(insertList);
        }
        if (CollUtil.isNotEmpty(updateList)) {
            fileReportInfoMapper.batchUpdate(updateList);
        }
        return fileReportInfoInDOList.size();
    }

    @Override
    public FileReportInfoOutDO.FileOutDO queryFileReportByOrgCode(String orgCode) {

        FileReportInfoOutDO.FileOutDO fileOutDO = new FileReportInfoOutDO.FileOutDO();
        fileOutDO.setPictureSize(0L);
        fileOutDO.setVideoSize(0L);
        fileOutDO.setVideoPictureSize(0L);
        fileOutDO.setTotalSize(0L);

        FileReportInfoEntity fileReportInfoEntity = fileReportInfoMapper.selectTotalByOrgCode(orgCode);
        if (fileReportInfoEntity == null) {
            return fileOutDO;
        }
        fileOutDO.setPictureSize(getLong(fileReportInfoEntity.getPictureSize()));
        fileOutDO.setVideoSize(getLong(fileReportInfoEntity.getVideoSize()));
        fileOutDO.setVideoPictureSize(getLong(fileReportInfoEntity.getVideoPictureSize()));
        fileOutDO.setTotalSize(fileOutDO.getPictureSize() + fileOutDO.getVideoSize() + fileOutDO.getVideoPictureSize());
        return fileOutDO;
    }

    private Long getLong(Long l) {
        return l == null ? 0L : l;
    }

    @Override
    public List<FileReportInfoOutDO.FileTrendOutDO> trend(String orgCode, String nestId, List<String> timeList, int type) {

        FileReportInfoInPO.TrendInPO trendInPO = new FileReportInfoInPO.TrendInPO();
        trendInPO.setOrgCode(orgCode);
        trendInPO.setNestId(nestId);
        trendInPO.setTimeList(timeList);
        trendInPO.setType(type);

        List<FileReportInfoOutPO.FileTrendOutPO> fileTrendOutPOList = fileReportInfoMapper.trend(trendInPO);
        if (CollUtil.isEmpty(fileTrendOutPOList)) {
            return Collections.emptyList();
        }
        List<FileReportInfoOutDO.FileTrendOutDO> fileTrendOutDOList = Lists.newLinkedList();
        for (FileReportInfoOutPO.FileTrendOutPO fileTrendOutPO : fileTrendOutPOList) {
            FileReportInfoOutDO.FileTrendOutDO fileTrendOutDO = new FileReportInfoOutDO.FileTrendOutDO();
            fileTrendOutDO.setTime(type == 1 ? fileTrendOutPO.getReportMonth() : fileTrendOutPO.getReportDay());
            fileTrendOutDO.setPictureSize(getLong(fileTrendOutPO.getPictureSize()));
            fileTrendOutDO.setVideoSize(getLong(fileTrendOutPO.getVideoSize()));
            fileTrendOutDO.setVideoPictureSize(getLong(fileTrendOutPO.getVideoPictureSize()));
            fileTrendOutDO.setTotalSize(fileTrendOutDO.getPictureSize() + fileTrendOutDO.getVideoSize() + fileTrendOutDO.getVideoPictureSize());
            fileTrendOutDOList.add(fileTrendOutDO);
        }
        return fileTrendOutDOList;
    }

    private FileReportInfoInPO.ListInPO getListInPO(FileReportInfoInDO.ListInDO listInDO) {
        FileReportInfoInPO.ListInPO listInPO = new FileReportInfoInPO.ListInPO();
        listInPO.setOrgCode(listInDO.getOrgCode());
        listInPO.setNestId(listInDO.getNestId());
        listInPO.setStartTime(listInDO.getStartTime());
        listInPO.setEndTime(listInDO.getEndTime());
        listInPO.setPageNo(listInDO.getPageNo());
        listInPO.setPageSize(listInDO.getPageSize());
        listInPO.setOrderBy(listInDO.getOrderBy());
        listInPO.setAsc(listInDO.getAsc());
        listInPO.setOffset(listInDO.toOffset());
        listInPO.setLimit(listInDO.toLimit());
        return listInPO;
    }

    @Override
    public long listCount(FileReportInfoInDO.ListInDO listInDO) {

        FileReportInfoInPO.ListInPO listInPO = getListInPO(listInDO);
        return fileReportInfoMapper.listCount(listInPO);
    }

    @Override
    public List<FileReportInfoOutDO.ListOutDO> list(FileReportInfoInDO.ListInDO listInDO) {

        FileReportInfoInPO.ListInPO listInPO = getListInPO(listInDO);
        List<FileReportInfoOutPO.FileListOutPO> fileListOutPOList = fileReportInfoMapper.list(listInPO);
        if (CollUtil.isEmpty(fileListOutPOList)) {
            return Collections.emptyList();
        }
        List<FileReportInfoOutDO.ListOutDO> listOutDOList = Lists.newLinkedList();
        for (FileReportInfoOutPO.FileListOutPO fileListOutPO : fileListOutPOList) {
            FileReportInfoOutDO.ListOutDO listOutDO = new FileReportInfoOutDO.ListOutDO();
            listOutDO.setNestId(fileListOutPO.getNestId());
            listOutDO.setPictureSize(fileListOutPO.getPictureSize());
            listOutDO.setVideoSize(fileListOutPO.getVideoSize());
            listOutDO.setVideoPictureSize(fileListOutPO.getVideoPictureSize());
            listOutDO.setTotalSize(fileListOutPO.getTotalSize());
            listOutDOList.add(listOutDO);
        }
        return listOutDOList;
    }

    @Override
    public FileReportInfoOutDO.FileOutDO totalReport(int tagVersion, String reportDay) {
        FileReportInfoEntity fileReportInfoEntity = fileReportInfoMapper.totalReport(tagVersion, reportDay);

        FileReportInfoOutDO.FileOutDO fileOutDO = new FileReportInfoOutDO.FileOutDO();
        fileOutDO.setPictureSize(0L);
        fileOutDO.setVideoSize(0L);
        fileOutDO.setVideoPictureSize(0L);
        fileOutDO.setTotalSize(0L);
        if (fileReportInfoEntity == null) {
            return fileOutDO;
        }

        fileOutDO.setPictureSize(fileReportInfoEntity.getPictureSize());
        fileOutDO.setVideoSize(fileReportInfoEntity.getVideoSize());
        fileOutDO.setVideoPictureSize(fileReportInfoEntity.getVideoPictureSize());
        fileOutDO.setTotalSize(fileReportInfoEntity.getTotalSize());

        return fileOutDO;
    }
}
