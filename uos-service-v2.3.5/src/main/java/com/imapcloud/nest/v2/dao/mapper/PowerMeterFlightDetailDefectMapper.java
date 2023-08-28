package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.PowerMeterFlightDetailDefectEntity;
import com.imapcloud.nest.v2.dao.po.MeterDataDetailDefectQueryCriteriaPO;
import com.imapcloud.nest.v2.dao.po.in.PowerMeterFlightDetailDefectInPO;
import com.imapcloud.nest.v2.dao.po.out.PowerMeterFlightDetailDefectOutPO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerHomeAlarmStatisticsInDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 缺陷识别
 *
 * @author boluo
 * @date 2023-02-28
 */
@Mapper
public interface PowerMeterFlightDetailDefectMapper extends BaseMapper<PowerMeterFlightDetailDefectEntity>,
        IPageMapper<PowerMeterFlightDetailDefectEntity, MeterDataDetailDefectQueryCriteriaPO, PagingRestrictDo> {
    /**
     * 批量保存
     *
     * @param entityList 实体列表
     */
    int batchSave(@Param("list") List<PowerMeterFlightDetailDefectEntity> entityList);

    /**
     * 统计数据
     *
     * @param dataId 数据标识
     * @return {@link List}<{@link PowerMeterFlightDetailDefectOutPO.StatisticsOutPO}>
     */
    List<PowerMeterFlightDetailDefectOutPO.StatisticsOutPO> statistics(@Param("dataId") String dataId);

    /**
     * 批量更新
     *
     * @param defectStateInPOList 缺陷状态polist
     */
    long batchUpdate(@Param("infoList") List<PowerMeterFlightDetailDefectInPO.DefectStateInPO> defectStateInPOList);

    /**
     * 按照时间统计总数
     *
     * @param inDO
     * @return
     */
    List<PowerMeterFlightDetailDefectOutPO.StatisticsOutPO> statisticsTotal(PowerHomeAlarmStatisticsInDO inDO);
}
