package com.imapcloud.nest.v2.manager.sql;

import com.geoai.common.mp.entity.PagingRestrictDo;
import com.imapcloud.nest.v2.dao.po.MeterDataDetailDefectQueryCriteriaPO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerHomeAlarmStatisticsInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerMeterFlightDetailDefectOutDO;

import java.util.List;

/**
 * 缺陷识别
 *
 * @author boluo
 * @date 2023-03-07
 */
public interface PowerMeterFlightDetailDefectManager {

    /**
     * 列表数量
     *
     * @param meterDataDetailDefectQueryCriteriaPO 计数据细节缺陷查询条件
     * @return long
     */
    long countByCondition(MeterDataDetailDefectQueryCriteriaPO meterDataDetailDefectQueryCriteriaPO);

    /**
     * 分页
     *
     * @param meterDataDetailDefectQueryCriteriaPO 计数据细节缺陷查询条件
     * @param pagingRestrictDo                     分页限制做
     * @return {@link List}<{@link PowerMeterFlightDetailDefectOutDO}>
     */
    List<PowerMeterFlightDetailDefectOutDO> selectByCondition(MeterDataDetailDefectQueryCriteriaPO meterDataDetailDefectQueryCriteriaPO
            , PagingRestrictDo pagingRestrictDo);

    /**
     * 查询
     *
     * @param detailIdList 细节id列表
     * @return {@link List}<{@link PowerMeterFlightDetailDefectOutDO}>
     */
    List<PowerMeterFlightDetailDefectOutDO> selectListByDetailIdList(List<String> detailIdList);

    /**
     * 逻辑删除
     *
     * @param detailIdList 细节id列表
     * @param accountId    帐户id
     */
    void deleteByDetailIdList(List<String> detailIdList, String accountId);

    /**
     * 按设备状态统计
     *
     * @param dataId 数据标识
     * @return {@link List}<{@link PowerMeterFlightDetailDefectOutDO.StatisticsOutDO}>
     */
    List<PowerMeterFlightDetailDefectOutDO.StatisticsOutDO> statistics(String dataId);


    /**
     * 按时间统计
     */
    List<PowerMeterFlightDetailDefectOutDO.StatisticsOutDO> statisticsTotal(PowerHomeAlarmStatisticsInDO inDO);

    /**
     * 更新缺陷识别记录核实状态为已核实/误报
     * @param batchIds
     * @param verificationStatus
     */
    void updatePushState(List<String> batchIds, Integer verificationStatus,String userId);

    /**
     * 选择列表数据id列表
     *
     * @param dataIdList 数据id列表
     * @return {@link List}<{@link PowerMeterFlightDetailDefectOutDO}>
     */
    List<PowerMeterFlightDetailDefectOutDO> selectListByDataIdList(List<String> dataIdList);

    /**
     * 任务初始化
     *
     * @param dataId 数据标识
     */
    void taskInit(String dataId);

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
     * 任务前
     *
     * @param detailIdList ids数据图
     */
    void taskPre(List<String> detailIdList);

    /**
     * 任务结束
     *
     * @param detailId ids数据图
     */
    void taskEnd(String detailId);

    /**
     * 任务超时
     *
     * @param detailIdList 超时细节id列表
     */
    void taskTimeout(List<String> detailIdList);

    /**
     * 任务开始
     *
     * @param detailId 详细id
     */
    void taskIng(String detailId);

    /**
     * 根据问题状态查询
     * @param build
     * @return
     */
    List<PowerMeterFlightDetailDefectOutDO> queryByDevicesState(PowerHomeAlarmStatisticsInDO  build);

    /**
     * 任务未授权
     *
     * @param detailId 详细id
     */
    void taskNoAuth(String detailId);

    /**
     * 查询单位下正在识别的任务
     *
     * @param orgCode 组织代码
     * @return {@link List}<{@link String}>
     */
    List<String> selectDataIdByOrgCode(String orgCode);

    /**
     * 平衡
     *
     * @param detailId 详细id
     */
    void balance(String detailId);
}
