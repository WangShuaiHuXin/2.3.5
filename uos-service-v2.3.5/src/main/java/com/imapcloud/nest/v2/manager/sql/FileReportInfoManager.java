package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.in.FileReportInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.FileReportInfoOutDO;

import java.util.List;

/**
 * 文件统计
 *
 * @author boluo
 * @date 2022-10-31
 */
public interface FileReportInfoManager {

    /**
     * 批量插入
     *
     * @param fileReportInfoInDOList 在dolist文件报告信息
     * @return int
     */
    int batchInsert(List<FileReportInfoInDO> fileReportInfoInDOList);

    /**
     * 查询指定单位的的所有存储
     *
     * @param orgCode 组织代码
     * @return {@link FileReportInfoOutDO.FileOutDO}
     */
    FileReportInfoOutDO.FileOutDO queryFileReportByOrgCode(String orgCode);

    /**
     * 月趋势
     *
     * @param orgCode   组织代码
     * @param nestId    巢id
     * @param timeList  时间
     * @param type      0:日 1:月
     * @return {@link List}<{@link FileReportInfoOutDO.FileTrendOutDO}>
     */
    List<FileReportInfoOutDO.FileTrendOutDO> trend(String orgCode, String nestId, List<String> timeList, int type);

    /**
     * 列表
     *
     * @param listInDO 在做上市
     * @return {@link List}<{@link FileReportInfoOutDO.ListOutDO}>
     */
    List<FileReportInfoOutDO.ListOutDO> list(FileReportInfoInDO.ListInDO listInDO);

    /**
     * 列表总数
     *
     * @param listInDO 在做上市
     * @return long
     */
    long listCount(FileReportInfoInDO.ListInDO listInDO);

    /**
     * 报告
     *
     * @param tagVersion tagVersion
     * @param reportDay reportDay
     * @return {@link FileReportInfoOutDO.FileOutDO}
     */
    FileReportInfoOutDO.FileOutDO totalReport(int tagVersion, String reportDay);
}
