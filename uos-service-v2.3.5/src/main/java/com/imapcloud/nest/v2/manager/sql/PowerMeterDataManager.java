package com.imapcloud.nest.v2.manager.sql;

import com.geoai.common.mp.entity.PagingRestrictDo;
import com.imapcloud.nest.v2.dao.entity.PowerMeterFlightDataEntity;
import com.imapcloud.nest.v2.dao.entity.PowerMeterFlightDetailEntity;
import com.imapcloud.nest.v2.dao.entity.PowerMeterReadingValueEntity;
import com.imapcloud.nest.v2.dao.po.MeterDataQueryCriteriaPO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerMeterFlightDataOutDO;
import com.imapcloud.nest.v2.service.dto.in.PowerMeterFlightDataInDTO;
import com.imapcloud.nest.v2.service.dto.out.MeterDetailPhotoOutDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 电力表计数据管理器接口
 *
 * @author Vastfy
 * @date 2022/12/5 13:52
 * @since 2.1.5
 */
public interface PowerMeterDataManager {

    long countByCondition(MeterDataQueryCriteriaPO queryCriteria);

    List<PowerMeterFlightDataEntity> selectByCondition(MeterDataQueryCriteriaPO queryCriteria, PagingRestrictDo pagingRestrict);

    PowerMeterFlightDataEntity getMeterData(String dataId);

    @Transactional(rollbackFor = Exception.class)
    List<MeterDetailPhotoOutDTO> savePowerMeterFlightData(PowerMeterFlightDataInDTO data);

    List<PowerMeterFlightDetailEntity>  selectByDetailIds(List<String> batchIds);

    List<PowerMeterReadingValueEntity> selectReadValueByDetailIds(List<String> batchIds);

    void updatePushState(List<String> batchIds,String state);

    List<PowerMeterReadingValueEntity> selectReadValueByValueIds(List<String> valueIds);

    /**
     * 删除表数据
     *
     * @param dataIdList 数据标识
     */
    void deleteMeterData(List<String> dataIdList);

    /**
     * 批量获取
     *
     * @param dataIdList 数据id列表
     * @return {@link List}<{@link PowerMeterFlightDataOutDO}>
     */
    List<PowerMeterFlightDataOutDO> selectListByDataIdList(List<String> dataIdList);
}
