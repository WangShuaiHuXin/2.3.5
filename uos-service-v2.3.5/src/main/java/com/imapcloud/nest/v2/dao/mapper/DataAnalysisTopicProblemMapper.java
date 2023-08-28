package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisTopicProblemEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分析统计-专题行业问题表
 *
 * @author boluo
 * @date 2022-07-14
 */
@Mapper
public interface DataAnalysisTopicProblemMapper extends BaseMapper<DataAnalysisTopicProblemEntity> {
    /**
     * 批量插入
     *
     * @param entityList 添加列表
     * @return int
     */
    int batchInsert(@Param("entityList") List<DataAnalysisTopicProblemEntity> entityList);

    /**
     * 批量更新
     *
     * @param entityList 实体列表
     * @return int
     */
    int batchUpdate(@Param("entityList") List<DataAnalysisTopicProblemEntity> entityList);
}
