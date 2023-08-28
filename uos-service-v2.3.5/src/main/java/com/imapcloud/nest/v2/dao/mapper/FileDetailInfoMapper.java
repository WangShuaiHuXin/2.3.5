package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.FileDetailInfoEntity;
import com.imapcloud.nest.v2.dao.po.in.FileDetailInfoInPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 文件详细信息
 *
 * @author boluo
 * @date 2022-10-27
 */
@Mapper
public interface FileDetailInfoMapper extends BaseMapper<FileDetailInfoEntity> {

    /**
     * 插入或更新 唯一键bucket+object+eventType
     *
     * @param fileDetailInfoEntityList 文件详细信息实体列表
     * @return int
     */
    int batchInsert(@Param("entityList") List<FileDetailInfoEntity> fileDetailInfoEntityList);

    /**
     * 批量更新
     *
     * @param fileDetailInfoEntityList 文件详细信息实体列表
     * @return int
     */
    int batchUpdate(@Param("entityList") List<FileDetailInfoEntity> fileDetailInfoEntityList);

    /**
     * 查询未同步的数据
     *
     * @param tagVersion 标记版本
     * @return {@link List}<{@link Map}<{@link String}, {@link String}>>
     */
    List<Map<String, String>> selectNotSynListByTagVersion(@Param("tagVersion") int tagVersion);

    /**
     * 查询指定版本，指定天的数据
     *
     * @param dayReportInPO 天报告阿宝
     * @return {@link List}<{@link Map}<{@link String}, {@link String}>>
     */
    List<Map<String, Object>> dayReport(@Param("entity") FileDetailInfoInPO.DayReportInPO dayReportInPO);
}
