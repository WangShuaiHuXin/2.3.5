package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.GridInspectRecordEntity;
import com.imapcloud.nest.v2.dao.po.in.GridInspectRecordCriteriaInPO;

import java.util.List;

/**
 * @Classname GridInspectRecordMapper
 * @Description 网格巡检记录 Mapper 接口
 * @Date 2023/2/3 14:24
 * @Author Carnival
 */
public interface GridInspectRecordMapper extends BaseMapper<GridInspectRecordEntity>,
        IPageMapper<GridInspectRecordEntity, GridInspectRecordCriteriaInPO, PagingRestrictDo> {

    boolean batchInsert(List<GridInspectRecordEntity> gridInspectRecordEntityList);

    boolean batchUpdateIsNewest(List<Integer> taskIds);
}
