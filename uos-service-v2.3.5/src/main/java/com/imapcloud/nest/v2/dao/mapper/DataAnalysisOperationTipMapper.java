package com.imapcloud.nest.v2.dao.mapper;

import com.imapcloud.nest.v2.dao.entity.DataAnalysisOperationTipEntity;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisOperationTipMapper.java
 * @Description DataAnalysisOperationTipMapper
 * @createTime 2022年07月26日 19:53:00
 */
public interface DataAnalysisOperationTipMapper {
    /**
     * 新增
     **/
    int insert(DataAnalysisOperationTipEntity dataAnalysisOperationTip);

    /**
     * 更新
     **/
    int update(DataAnalysisOperationTipEntity dataAnalysisOperationTip);

    /**
     * 查询 根据主键 id 查询
     **/
    DataAnalysisOperationTipEntity query(Long creatorId);

}
