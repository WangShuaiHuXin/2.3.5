package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.in.FileDetailInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.FileDetailInfoOutDO;

import java.util.List;

/**
 * 文件详细信息管理器
 *
 * @author boluo
 * @date 2022-10-27
 */
public interface FileDetailInfoManager {

    /**
     * 插入或更新
     *
     * @param fileDetailInfoInDOList 在dolist文件详细信息
     * @return int
     */
    int batchInsert(List<FileDetailInfoInDO> fileDetailInfoInDOList);

    /**
     * 批量更新
     *
     * @param fileDetailInfoInDOList 在dolist文件详细信息
     * @return int
     */
    int batchUpdate(List<FileDetailInfoInDO> fileDetailInfoInDOList);

    /**
     * 查询未同步的数据
     *
     * @param tagVersion 标记版本
     * @return {@link List}<{@link FileDetailInfoOutDO.NotSynInfoOutDO}>
     */
    List<FileDetailInfoOutDO.NotSynInfoOutDO> selectNotSynListByTagVersion(int tagVersion);

    /**
     * 统计单位、基站、tag版本、一天内的数据
     *
     * @param dayReportInDO 天在做报告
     * @return {@link List}<{@link FileDetailInfoOutDO.DayReportOutDO}>
     */
    List<FileDetailInfoOutDO.DayReportOutDO> dayReport(FileDetailInfoInDO.DayReportInDO dayReportInDO);

    /**
     * 更新指定单位、基站、某一天的syn
     *
     * @param synInDO syn在做
     * @return int
     */
    int updateSyn(FileDetailInfoInDO.SynInDO synInDO);
}
