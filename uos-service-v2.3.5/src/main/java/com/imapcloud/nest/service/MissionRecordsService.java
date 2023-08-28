package com.imapcloud.nest.service;

import com.imapcloud.nest.model.MissionRecordsEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.NestEntity;
import com.imapcloud.nest.pojo.dto.reqDto.MissionRecordsReqDto;
import com.imapcloud.nest.utils.RestRes;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 架次记录表 服务类
 * </p>
 *
 * @author wmin
 * @since 2020-07-26
 */
public interface MissionRecordsService extends IService<MissionRecordsEntity> {
    /**
     * 批量更新通过execId
     *
     * @param list
     * @return
     */
    int updateBatchByExecId(List<MissionRecordsEntity> list);

    /**
     * 更新通过 execId
     *
     * @param missionRecordsEntity
     * @return
     */
    int updateByExecId(MissionRecordsEntity missionRecordsEntity);

    /**
     * 通过execId批量查出id
     *
     * @param execIds
     * @return
     */
    List<Integer> listIdByExecIds(List<String> execIds);

    Integer saveOrUpdateByExecId(MissionRecordsEntity missionRecordsEntity);

    Integer countMissionRecords(String execId);

    /**
     * 通过id获取架次id、架次执行状态、图片上传状态
     *
     * @param id
     * @return
     */
    MissionRecordsEntity getMissionRecordById(Integer id);


    /**
     * 通过id修改图片上传状态
     *
     * @param dataStatus
     * @param id
     */
    void updateDataStatusById(@Param("dataStatus") Integer dataStatus, @Param("id") Integer id);

    /**
     * 通过execId获取missionId和recordId
     *
     * @param execId
     * @return
     */
    MissionRecordsEntity getByExecId(String execId);

    /**
     * 通过架次执行id获取数据状态
     *
     * @param id
     * @return
     */
    Integer getDataStatusById(Integer id);

    Integer getGainDataModeById(Integer id);

    /**
     * 通过图片id获取zip包名称
     *
     * @param id
     * @return
     */
    String getDataPathById(Integer id);

    /**
     * 通过架次id获取视频zip包名称
     *
     * @param id
     * @return
     */
    String getVideoPathById(Integer id);

    /**
     * 通过id获取execId
     *
     * @param id
     * @return
     */
    String getExecIdById(Integer id);

    /**
     * 通过id获取机巢id
     *
     * @param id
     * @return
     */
    @Deprecated
    NestEntity getNestUuidById(Integer id);

    String getNestUuidByRecordId(Integer id);

    /**
     * 通过id获取app id
     *
     * @param missionRecordsId
     * @return
     */
    String getNestUuidByAppId(Integer missionRecordsId);

    /**
     * 根据id修改back_up_status为1
     *
     * @param id
     */
    void updateBackUpStatus(Integer id);

    /**
     * 更改数据备份状态（飞机 -> 机巢）
     *
     * @param execId
     * @param status
     * @return
     */
    Integer updateDataStatusByExecId(String execId, Integer status);

    /**
     * 更改数据同步状态（机巢 -> 文件服务器）
     *
     * @param execId
     * @param status
     * @return
     */
    Integer updateBackupStatusByExecId(String execId, Integer status);

    /**
     * 统计一个架次的飞行次数
     *
     * @param missionId
     * @return
     */
    Integer countFlyByMissionId(Integer missionId);


    /**
     * 根据id修改deleted为1
     *
     * @param reqDto
     */
    RestRes batchUpdateMissionRecordsBy(MissionRecordsReqDto reqDto);

    /**
     * 通过架次执行id获取是否录制视频
     *
     * @param recordId
     * @return
     */
    Integer getGainVideo(Integer recordId);

    /**
     * 通过架次执行id获取架次名称
     *
     * @param recordId
     * @return
     */
    String getMissionName(Integer recordId);

    /**
     * 通过架次执行id获取架次名称
     *
     * @param recordId
     * @return
     */
    Integer getFlyIndex(Integer recordId);

    /**
     * 通过missionId获取最新的recordId
     *
     * @param missionId
     * @return
     */
    Integer getIdByMissionId(Integer missionId);

    /**
     * 通过recordId获取missionId
     *
     * @param recordId
     * @return
     */
    Integer getMissionIdByRecordId(Integer recordId);

    /**
     * 通过recordId获取app的图传地址
     *
     * @param recordId
     * @return
     */
    String getAppPullHttpUrl(Integer recordId);

    /**
     * 首页-获取巡检记录的统计
     *
     * @param type
     * @return
     */
    Map getTotalMilesAndTime(Integer type);

    /**
     * 首页-获取巡检记录分页列表
     *
     * @param type
     * @return
     */
    RestRes getMissionRecordPageList(String startTime, String endTime, Integer pageNum, Integer pageSize);

    /**
     * 巡检成果统计
     *
     * @return
     */
    RestRes missionRecordsStatistics();

    /**
     * 巡检成果统计
     *
     * @return
     */
    RestRes getInspectStatisticsBy(String nestId, String startTime, String endTime);


    /**
     * 根据架次id获取任务id
     *
     * @param missionRecordsId
     * @return
     */
    Integer getTaskIdByRecordsId(Integer missionRecordsId);

    /**
     * 根据任务id获取架次id
     *
     * @param taskId
     * @return
     */
    List<Integer> getMissionRecordsIdByTaskId(Integer taskId);

    Map getTotalInspectTimes(String startTime, String endTime);

    String getMaxRecordsIdByMissionId(Integer missionId);

    /**
     * 查询最新的记录
     *
     * @param missionId
     * @return
     */
    MissionRecordsEntity getLastByMissionId(Integer missionId);

    /**
     * 批量清空数据
     *
     * @param missionRecordsReqDto 任务记录要求dto
     * @return {@link RestRes}
     */
    RestRes batchClearMissionRecords(MissionRecordsReqDto missionRecordsReqDto);

}
