package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.GridManageEntity;
import com.imapcloud.nest.v2.dao.entity.LayerVectorsEntity;
import com.imapcloud.nest.v2.dao.po.in.GridManageQueryCriteriaInPO;
import com.imapcloud.nest.v2.dao.po.in.LayerVectorsCriteriaInPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Classname LayerVectorsMapper
 * @Description 图层矢量 Mapper 接口
 * @Date 2022/12/9 15:25
 * @Author Carnival
 */
public interface LayerVectorsMapper extends BaseMapper<LayerVectorsEntity>,
        IPageMapper<LayerVectorsEntity, LayerVectorsCriteriaInPO, PagingRestrictDo> {

}
