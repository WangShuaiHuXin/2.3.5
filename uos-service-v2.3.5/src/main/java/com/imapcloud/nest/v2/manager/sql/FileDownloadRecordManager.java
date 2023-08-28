package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.in.FileDownloadRecordInDO;

/**
 * 文件下载记录
 *
 * @author boluo
 * @date 2023-05-09
 */
public interface FileDownloadRecordManager {

    /**
     * 保存
     *
     * @param fileDownloadRecordInDO 文件下载记录
     * @return int
     */
    int save(FileDownloadRecordInDO fileDownloadRecordInDO);

    /**
     * 更新下载状态为已下载
     *
     * @param fileDownloadRecordId 文件下载记录id
     * @return int
     */
    int updateDownloadStatus(String fileDownloadRecordId);
}
