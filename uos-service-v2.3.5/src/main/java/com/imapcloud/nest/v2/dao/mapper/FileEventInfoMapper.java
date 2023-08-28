package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.FileEventInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * minio文件事件
 *
 * @author boluo
 * @date 2022-10-27
 */
@Mapper
public interface FileEventInfoMapper extends BaseMapper<FileEventInfoEntity> {

    /**
     * 批量更新
     *
     * @param fileEventInfoEntityList 文件事件信息实体列表
     * @return int
     */
    int batchUpdate(@Param("entityList") List<FileEventInfoEntity> fileEventInfoEntityList);

    /**
     * 选择还未分析eventData的数据
     *
     * @return {@link List}<{@link FileEventInfoEntity}>
     */
    List<FileEventInfoEntity> selectNotAnalysisList();

    /**
     * 查询未同步的列表
     *
     * @param eventType 事件类型 1 删除  2 tag
     * @return {@link List}<{@link FileEventInfoEntity}>
     */
    List<FileEventInfoEntity> selectNotSynList(@Param("eventType") int eventType);

    /**
     * 删除未知事件
     */
    void deleteOther();

    /**
     * 删除已同步的事件
     */
    void deleteSyn();

    /**
     * 删除分片消息
     */
    void deleteChunk();
}
