package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.PowerMeterFlightDetailEntity;
import com.imapcloud.nest.v2.dao.entity.PowerMeterFlightDetailInfraredEntity;
import com.imapcloud.nest.v2.dao.po.in.PowerMeterFlightDetailInfraredInPO;
import com.imapcloud.nest.v2.dao.po.out.DeviceStateStatisticPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 红外测温-飞行数据详情表 Mapper 接口
 * </p>
 *
 * @author wmin
 * @since 2022-12-28
 */
public interface PowerMeterFlightDetailInfraredMapper extends BaseMapper<PowerMeterFlightDetailInfraredEntity> {

    int batchDeleteByDetailIdList(@Param("detailIds") List<String> detailIds, @Param("accountId") String accountId);

    int batchSave(@Param("list") List<PowerMeterFlightDetailInfraredEntity> entityList);

    List<DeviceStateStatisticPO> countGroupByDeviceStateByDataId(@Param("dataId") String dataId);

    /**
     * 批量更新红外状态
     *
     * @param infraredStateInPOList infraredStateInPOList
     * @return int
     */
    int batchUpdateInfraredState(@Param("entityList") List<PowerMeterFlightDetailInfraredInPO.InfraredStateInPO> infraredStateInPOList);
}
