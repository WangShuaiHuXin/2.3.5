package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.FileReportInfoEntity;
import com.imapcloud.nest.v2.dao.po.in.FileReportInfoInPO;
import com.imapcloud.nest.v2.dao.po.out.FileReportInfoOutPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文件统计
 *
 * @author boluo
 * @date 2022-10-31
 */
@Mapper
public interface FileReportInfoMapper extends BaseMapper<FileReportInfoEntity> {

    /**
     * 批量插入
     *
     * @param insertList 插入列表
     * @return int
     */
    int batchInsert(@Param("entityList") List<FileReportInfoEntity> insertList);

    /**
     * 批量更新
     *
     * @param updateList 更新列表
     * @return int
     */
    int batchUpdate(@Param("entityList") List<FileReportInfoEntity> updateList);

    FileReportInfoEntity selectTotalByOrgCode(@Param("orgCode") String orgCode);

    /**
     * 趋势
     *
     * @param trendInPO 阿宝趋势
     * @return {@link List}<{@link FileReportInfoOutPO.FileTrendOutPO}>
     */
    List<FileReportInfoOutPO.FileTrendOutPO> trend(FileReportInfoInPO.TrendInPO trendInPO);

    /**
     * 列表总数
     *
     * @param listInPO 在做上市
     * @return long
     */
    long listCount(FileReportInfoInPO.ListInPO listInPO);

    /**
     * 列表
     *
     * @param listInPO 在阿宝
     * @return {@link List}<{@link FileReportInfoOutPO.FileListOutPO}>
     */
    List<FileReportInfoOutPO.FileListOutPO> list(FileReportInfoInPO.ListInPO listInPO);

    /**
     * 报告
     *
     * @param tagVersion tagVersion
     * @param reportDay reportDay
     * @return {@link FileReportInfoEntity}
     */
    FileReportInfoEntity totalReport(@Param("tagVersion") int tagVersion, @Param("reportDay") String reportDay);
}
