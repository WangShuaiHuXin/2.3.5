package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.in.PowerMeterInfraredRecordInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerMeterInfraredRecordOutDO;

import java.util.List;

/**
 * 红外测温记录
 *
 * @author boluo
 * @date 2022-12-28
 */
public interface PowerMeterInfraredRecordManager {

    /**
     * 插入
     *
     * @param powerMeterInfraredRecord 功率计红外记录
     * @return int
     */
    int insert(PowerMeterInfraredRecordInDO powerMeterInfraredRecord);

    /**
     * 选择通过细节id列表
     *
     * @param detailId 详细id
     * @return {@link List}<{@link PowerMeterInfraredRecordOutDO}>
     */
    List<PowerMeterInfraredRecordOutDO> selectListByDetailId(String detailId);

    /**
     * 选择通过细节id列表查询列表
     *
     * @param detailIds 详细id
     * @return {@link List}<{@link PowerMeterInfraredRecordOutDO}>
     */
    List<PowerMeterInfraredRecordOutDO> selectListByDetailIds(List<String> detailIds);

    /**
     * 逻辑删除
     *
     * @param detailId 详细id
     * @param accountId 详细id
     * @return int
     */
    int deleteByDetailId(String detailId, String accountId);

    List<PowerMeterInfraredRecordOutDO> selectInfraredValueByValueIds(List<String> valueIds);
    List<PowerMeterInfraredRecordOutDO> selectInfraredValueByValueIdsNotDelete(List<String> valueIds);
    /**
     * 逻辑删除
     *
     * @param detailIdList 细节id列表
     * @param accountId    帐户id
     * @return int
     */
    int deleteByDetailIdList(List<String> detailIdList, String accountId);

    /**
     * 获取每个工单的最高温
     */
    List<PowerMeterInfraredRecordOutDO> selectMaxTempByValueIdsNotDelete(List<String> valueIds);

    /**
     * 批量保存
     *
     * @param powerMeterInfraredRecordInDOList 功率计红外dolist记录
     * @return int
     */
    int batchSave(List<PowerMeterInfraredRecordInDO> powerMeterInfraredRecordInDOList);

    /**
     * 红外记录id删除
     *
     * @param infraredRecordId 红外记录id
     */
    void deleteByInfraredRecordId(String infraredRecordId);

    /**
     * 详情
     *
     * @param infraredRecordId 红外记录id
     * @return {@link PowerMeterInfraredRecordOutDO}
     */
    PowerMeterInfraredRecordOutDO selectListByInfraredRecordId(String infraredRecordId);
}
