package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisTopicIndustryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分析统计-专题行业表
 *
 * @author boluo
 * @date 2022-07-14
 */
@Mapper
public interface DataAnalysisTopicIndustryMapper extends BaseMapper<DataAnalysisTopicIndustryEntity> {

    /**
     * 批量插入
     *
     * @param dataAnalysisTopicIndustryEntityList 数据分析主题行业实体列表
     * @return int
     */
    int batchInsert(@Param("entityList") List<DataAnalysisTopicIndustryEntity> dataAnalysisTopicIndustryEntityList);

    /**
     * 批量更新
     *
     * @param dataAnalysisTopicIndustryEntityList 数据分析主题行业实体列表
     */
    void batchUpdate(@Param("entityList") List<DataAnalysisTopicIndustryEntity> dataAnalysisTopicIndustryEntityList);
}
