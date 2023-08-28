package com.imapcloud.nest.v2.dao.mapper;

import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.PowerMeterFlightDetailEntity;
import com.imapcloud.nest.v2.dao.po.MeterDataDetailQueryCriteriaPO;
import com.imapcloud.nest.v2.dao.po.out.MeterDeviceStateStatsOutPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 电力-表计读数-飞行数据详情表 Mapper 接口
 * </p>
 *
 * @author vastfy
 * @since 2.1.5
 */
@Mapper
public interface PowerMeterFlightDetailMapper extends BaseBatchMapper<PowerMeterFlightDetailEntity>,
        IPageMapper<PowerMeterFlightDetailEntity, MeterDataDetailQueryCriteriaPO, PagingRestrictDo> {

    /**
     * 统计设备状态数据
     * @param dataId    数据ID
     * @return  统计结果
     */
    List<MeterDeviceStateStatsOutPO> groupByDeviceState(@Param("dataId") String dataId);

    /**
     * 重置图片状态
     *
     * @param detailIds 详细id
     * @return int
     */
    int updateByDetailIdList(@Param("detailIdList") List<String> detailIds);
}
