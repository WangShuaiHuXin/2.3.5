package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.common.enums.DialReadingTypeEnum;
import com.imapcloud.nest.v2.dao.entity.PowerMeterFlightDetailEntity;
import com.imapcloud.nest.v2.service.dto.in.MeterDataDetailQueryDTO;
import com.imapcloud.nest.v2.service.dto.in.MeterReadingDiscernInfoInDTO;
import com.imapcloud.nest.v2.service.dto.in.MeterReadingInfoInDTO;
import com.imapcloud.nest.v2.service.dto.out.MeterDataDetailInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.MeterDeviceStateStatsOutDTO;

import java.util.Collection;
import java.util.List;

/**
 * 电力表计数据详情业务接口定义
 * @author Vastfy
 * @date 2022/12/5 9:51
 * @since 2.1.5
 */
public interface PowerMeterDetailService {

    /**
     * 统计表计设备状态数据
     * @param dataId    详情ID
     * @return  结果
     */
    List<MeterDeviceStateStatsOutDTO> getMeterDeviceStateStatistics(String dataId);

    /**
     * 分页查询表计详情数据
     * @param condition 查询条件
     * @return  分页结果
     */
    PageResultInfo<MeterDataDetailInfoOutDTO> queryMeterDataDetails(MeterDataDetailQueryDTO condition);

    /**
     * 根据表计详情ID查询详情信息
     * @param meterDetailIds    表计详情ID列表
     * @return  详情列表
     */
    List<PowerMeterFlightDetailEntity> getMeterDetails(Collection<String> meterDetailIds);

    /**
     * 删除表计详情信息
     * @param meterDetailIds    表计详情ID列表
     */
    void deleteMeterDetails(List<String> meterDetailIds);

    /**
     * 修改表计读数信息
     * @param meterDetailId 表计读数详情ID
     * @param info  修改信息
     */
    void modifyMeterReadingInfo(String meterDetailId, MeterReadingInfoInDTO info);

    /**
     * 更新表计状态
     * @param detailId  详情ID
     * @param readingInfo  读数信息
     */
    void updateMeterReadingDiscernInfo(String detailId, MeterReadingDiscernInfoInDTO readingInfo);

    /**
     * 更新表计状态
     * @param detailIds  详情ID
     * @param readingInfo  读数信息
     * @param dialReadingTypeEnum  读数状态
     */
    void updateMeterReadingInfo(Collection<String> detailIds, MeterReadingDiscernInfoInDTO readingInfo, DialReadingTypeEnum dialReadingTypeEnum);

    /**
     * 更新表计详情读数状态
     * @param detailIds 详情ID
     * @param expectReadingState    期望状态
     */
    void updateMeterDetailReadingState(List<String> detailIds, int expectReadingState);

    /**
     * 重置表记状态
     *
     * @param detailIds 详细id
     */
    void init(List<String> detailIds);

    /**
     * 批量删除
     *
     * @param dataIdList 数据id列表
     */
    void batchDeleteByDataIdList(List<String> dataIdList);
}
