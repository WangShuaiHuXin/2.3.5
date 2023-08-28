package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.dao.po.in.PowerMeterFlightDetailInfraredInDTO;
import com.imapcloud.nest.v2.dao.po.out.PowerMeterFlightDetailInfraredOutDTO;
import com.imapcloud.nest.v2.service.dto.out.MeterDetailPhotoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.PowerMeterInfraredRecordOutDTO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 红外测温-飞行数据详情表 服务类
 * </p>
 *
 * @author wmin
 * @since 2022-12-28
 */
public interface PowerMeterFlightDetailInfraredService {

    /**
     * 查询详情列表
     *
     * @param dto
     * @return
     */
    PageResultInfo<PowerMeterFlightDetailInfraredOutDTO> listPages(PowerMeterFlightDetailInfraredInDTO dto);

    /**
     * 获取结果详情
     *
     * @param detailId
     * @return
     */
    List<PowerMeterInfraredRecordOutDTO> getResultDetails(String detailId);

    /**
     * 批量删除
     *
     * @param detailIdList detailIdList
     * @param accountId accountId
     * @return boolean
     */
    Boolean batchDelete(List<String> detailIdList, String accountId);

    List<MeterDetailPhotoOutDTO> batchSavePowerMeterInfraredRecord();

    Map<String, Integer> deviceStateStatistics(String dataId);

    /**
     * 由数据批量删除id列表
     *
     * @param dataIdList 数据id列表
     * @param accountId  帐户id
     */
    void batchDeleteByDataIdList(List<String> dataIdList, String accountId);
}
