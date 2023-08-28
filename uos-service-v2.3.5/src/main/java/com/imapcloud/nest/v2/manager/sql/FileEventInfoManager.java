package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.enums.MinioEventTypeEnum;
import com.imapcloud.nest.v2.manager.dataobj.in.FileEventInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.FileEventInfoOutDO;

import java.util.List;

/**
 * 文件事件信息管理器
 *
 * @author boluo
 * @date 2022-10-27
 */
public interface FileEventInfoManager {

    /**
     * 查询未解析eventData的数据
     *
     * @return {@link List}<{@link FileEventInfoOutDO}>
     */
    List<FileEventInfoOutDO> selectNotAnalysisList();

    /**
     * 批量更新
     *
     * @param inDOList 在dolist
     * @return int
     */
    int batchUpdate(List<FileEventInfoInDO> inDOList);

    /**
     * 查询未同步的数据
     *
     * @param eventType 事件类型 1 删除  2 tag
     *
     * @return {@link List}<{@link FileEventInfoOutDO}>
     */
    List<FileEventInfoOutDO> selectNotSynList(MinioEventTypeEnum eventType);

    /**
     * 更新同步状态为已同步
     *
     * @param idList id列表
     * @return int
     */
    int updateSysStatusByIdList(List<Long> idList);

    /**
     * 删除已处理的事件
     */
    void delete();
}
