package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.common.enums.PowerTaskStateEnum;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerHomeAlarmStatisticsInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerMeterFlightDetailInfraredInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerMeterFlightDetailInfraredOutDO;

import java.util.Collection;
import java.util.List;

/**
 * 红外测温
 *
 * @author boluo
 * @date 2022-12-29
 */
public interface PowerMeterFlightDetailInfraredManager {

    /**
     * detailId
     *
     * @param detailId 详细id
     * @return {@link PowerMeterFlightDetailInfraredOutDO}
     */
    PowerMeterFlightDetailInfraredOutDO selectOneByDetailId(String detailId);

    /**
     * 查询
     *
     * @param detailIdList 细节id列表
     * @return {@link List}<{@link PowerMeterFlightDetailInfraredOutDO}>
     */
    List<PowerMeterFlightDetailInfraredOutDO> selectListByDetailId(List<String> detailIdList);

    /**
     * 更新
     *
     * @param powerMeterFlightDetailInfraredInDO 功率计飞行细节红外
     * @return int
     */
    int updateStat(PowerMeterFlightDetailInfraredInDO powerMeterFlightDetailInfraredInDO);

    /**
     * 批量逻辑删除
     *
     * @param detailIdList 细节id列表
     * @param accountId    帐户id
     * @return int
     */
    int batchDeleteByDetailIdList(List<String> detailIdList, String accountId);

    /**
     * 条件查询设备状态
     *
     * @param build 构建
     * @return {@link List}<{@link PowerMeterFlightDetailInfraredOutDO}>
     */
    List<PowerMeterFlightDetailInfraredOutDO> queryByDeviceStateCondition(PowerHomeAlarmStatisticsInDO build);

    int updatePushState(List<String> batchIds, String toString);

    /**
     * 更新任务状态
     *
     * @param detailIdList   细节id列表
     * @param powerTaskState 权力任务状态
     */
    void updateTaskState(List<String> detailIdList, PowerTaskStateEnum powerTaskState);

    /**
     * 通过
     *
     * @param dataIdCollection 数据id集合
     * @return {@link List}<{@link PowerMeterFlightDetailInfraredOutDO}>
     */
    List<PowerMeterFlightDetailInfraredOutDO> queryByDataIdCollection(Collection<String> dataIdCollection);

    /**
     * 任务暂停
     *
     * @param dataId 数据标识
     */
    void taskPause(String dataId);

    /**
     * 取消暂停
     *
     * @param dataId 数据标识
     */
    void taskUnpause(String dataId);

    /**
     * 任务停止
     *
     * @param dataId 数据标识
     */
    void taskStop(String dataId);

    /**
     * 任务初始化
     *
     * @param dataId 数据标识
     */
    void taskInit(String dataId);

    /**
     * 任务开始
     *
     * @param detailIdList ids数据图
     */
    void taskPre(List<String> detailIdList);

    /**
     * 任务超时
     *
     * @param detailIdList 细节id列表
     */
    void taskTimeout(List<String> detailIdList);

    /**
     * 任务开始
     *
     * @param detailId 详细id
     */
    void taskIng(String detailId);

    /**
     * 任务未授权
     *
     * @param detailId 详细id
     */
    void taskNoAuth(String detailId);

    /**
     * 正在识别的dataId
     *
     * @param orgCode 组织代码
     * @return {@link List}<{@link String}>
     */
    List<String> selectDataIdByOrgCode(String orgCode);

    /**
     * 初始化
     *
     * @param detailIdList 细节id列表
     */
    void init(List<String> detailIdList);
}
