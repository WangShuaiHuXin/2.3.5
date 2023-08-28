package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.common.enums.PowerTaskStateEnum;
import com.imapcloud.nest.v2.dao.entity.PowerMeterFlightDataEntity;
import com.imapcloud.nest.v2.manager.ai.SystemAIAnalysisTaskFinishedEvent;
import com.imapcloud.nest.v2.service.dto.in.MeterDataQueryDTO;
import com.imapcloud.nest.v2.service.dto.out.AIAnalysisTaskDataOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataScenePhotoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.MeterDataInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.PowerMeterFlightDataOutDTO;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 电力表计数据业务接口定义
 * @author Vastfy
 * @date 2022/12/5 9:51
 * @since 2.1.5
 */
public interface PowerMeterDataService {

    /**
     * 分页查询表计数据
     * @param condition 查询条件
     * @return  分页结果
     */
    PageResultInfo<MeterDataInfoOutDTO> queryMeterData(MeterDataQueryDTO condition);

    /**
     * 推送电力数据-分析统计
     * @param missionRecordId 任务记录id
     * @param photoIdList     照片id列表  必须在一个任务中
     */
    void push(Integer missionRecordId, List<Integer> photoIdList);

    /**
     * 人工推送电力数据-分析统计
     * @param photoIds     照片id列表  必须在一个任务中
     */
    DataScenePhotoOutDTO.PushOut manualPush(List<Integer> photoIds);

    /**
     * 根据电力表计数据详情ID查询电力表计数据信息
     * @param meterDetailIds 电力表计数据详情ID
     * @return  电力表计数据信息
     */
    PowerMeterFlightDataOutDTO fetchPowerMeterData(Collection<String> meterDetailIds);

    /**
     * 根据数据ID查询数据信息
     * @param dataId    数据ID
     * @return  结果
     */
    Optional<PowerMeterFlightDataEntity> findPowerMeterData(String dataId);

    /**
     * 红外测温
     *
     * @param missionRecordId 任务记录id
     * @param photoIdList    新数组列表
     */
    void pushInfrared(Integer missionRecordId, List<Integer> photoIdList);

    /**
     * 手动推动红外
     *
     * @param photoIdList 照片id列表
     * @return {@link DataScenePhotoOutDTO.PushOut}
     */
    DataScenePhotoOutDTO.PushOut manualPushInfrared(List<Integer> photoIdList);

    /**
     * 缺陷识别
     *
     * @param missionRecordId 任务记录id
     * @param photoIdList     照片id列表
     */
    void pushDefect(Integer missionRecordId, List<Integer> photoIdList);

    /**
     * 手动推送缺陷识别
     *
     * @param photoIdList 图片ID
     * @return {@link DataScenePhotoOutDTO.PushOut}
     */
    DataScenePhotoOutDTO.PushOut manualPushDefect(List<Integer> photoIdList);

    /**
     * 删除表数据
     *
     * @param dataIdList 数据标识
     */
    void deleteMeterData(List<String> dataIdList, String accountId);

    /**
     * 发送ws
     *
     * @param aiAnalysisTaskDataOutDTO 人工智能数据分析任务dto
     * @param accountIdCollection accountIdCollection
     */
    void sendWs(AIAnalysisTaskDataOutDTO aiAnalysisTaskDataOutDTO, Collection<String> accountIdCollection);

    /**
     * 任务修改
     *
     * @param dataId             数据标识
     * @param powerTaskStateEnum 权力任务状态枚举
     */
    void taskChange(String dataId, PowerTaskStateEnum powerTaskStateEnum);

    /**
     * channel13发送信息
     *
     * @param eventInfo           事件信息
     * @param accountCollection 帐户id集合
     */
    void sendChannel13Info(SystemAIAnalysisTaskFinishedEvent.EventInfo eventInfo, Collection<String> accountCollection);

    /**
     * 查询架次记录id所属单位
     *
     * @param missionRecordId 任务记录id
     * @return {@link String}
     */
    String getOrgCodeByMissionRecordsId(String missionRecordId);
}
