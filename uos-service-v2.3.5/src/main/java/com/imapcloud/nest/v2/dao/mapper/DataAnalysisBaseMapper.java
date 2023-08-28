package com.imapcloud.nest.v2.dao.mapper;

import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisCenterBaseEntity;
import com.imapcloud.nest.v2.dao.po.in.DataAnalysisBaseQueryCriteriaPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisCenterBaseMapper.java
 * @Description DataAnalysisBaseMapper
 * @createTime 2022年07月13日 15:01:00
 */
@Mapper
public interface DataAnalysisBaseMapper extends BaseBatchMapper<DataAnalysisCenterBaseEntity>, IPageMapper<DataAnalysisCenterBaseEntity, DataAnalysisBaseQueryCriteriaPO, PagingRestrictDo> {
}
