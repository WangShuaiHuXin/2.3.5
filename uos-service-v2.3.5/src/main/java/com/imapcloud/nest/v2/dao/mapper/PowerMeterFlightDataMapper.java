package com.imapcloud.nest.v2.dao.mapper;

import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.PowerMeterFlightDataEntity;
import com.imapcloud.nest.v2.dao.po.MeterDataQueryCriteriaPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 电力-表计读数-飞行数据表 Mapper 接口
 * </p>
 *
 * @author vastfy
 * @since 2.1.5
 */
@Mapper
public interface PowerMeterFlightDataMapper extends BaseBatchMapper<PowerMeterFlightDataEntity>,
        IPageMapper<PowerMeterFlightDataEntity, MeterDataQueryCriteriaPO, PagingRestrictDo> {



}
