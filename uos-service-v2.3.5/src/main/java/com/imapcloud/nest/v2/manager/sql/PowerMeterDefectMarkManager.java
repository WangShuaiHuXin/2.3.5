package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.in.PowerMeterDefectMarkInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerMeterDefectMarkOutDO;

import java.util.List;

/**
 * 缺陷识别标注
 *
 * @author boluo
 * @date 2023-03-08
 */
public interface PowerMeterDefectMarkManager {

    /**
     * 查询列表
     *
     * @param detailIdList 细节id列表
     * @return {@link List}<{@link PowerMeterDefectMarkOutDO}>
     */
    List<PowerMeterDefectMarkOutDO> selectListByDetailIdList(List<String> detailIdList);

    /**
     * 添加标记
     *
     * @param powerMeterDefectMarkInDO 功率计缺陷标记
     * @return {@link String}
     */
    String addMark(PowerMeterDefectMarkInDO powerMeterDefectMarkInDO);

    /**
     * 删除标记
     *
     * @param defectMarkIdList 缺陷标记id列表
     * @param accountId        帐户id
     * @param detailId        帐户id
     */
    void deleteMark(List<String> defectMarkIdList, String accountId, String detailId);

    /**
     * 删除
     *
     * @param detailId 详细id
     */
    void deleteByDetailId(String detailId);

    /**
     * 批量添加标记
     *
     * @param powerMeterDefectMarkInDOList 功率计在dolist缺陷印记
     * @return int
     */
    int batchAddMark(List<PowerMeterDefectMarkInDO> powerMeterDefectMarkInDOList);

    /**
     * 列表
     *
     * @param defectMarkIdList 缺陷标记id列表
     * @return {@link List}<{@link PowerMeterDefectMarkOutDO}>
     */
    List<PowerMeterDefectMarkOutDO> selectListByDefectMarkList(List<String> defectMarkIdList);
}
