package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.AirLineEntity;
import com.imapcloud.nest.model.MissionEntity;
import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.pojo.dto.autoMissionQueueDTO.MissionQueueAddDTO;
import com.imapcloud.nest.pojo.dto.autoMissionQueueDTO.MissionQueueTopDTO;
import com.imapcloud.nest.pojo.dto.reqDto.NestReqDto;
import com.imapcloud.nest.pojo.dto.unifyAirLineDto.AppMissionAircraftDTO;
import com.imapcloud.nest.utils.PageUtils;
import com.imapcloud.nest.utils.RestRes;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 架次表 服务类
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
public interface MissionService extends IService<MissionEntity> {

    /**
     * 批量查询航次,根据taskId
     *
     * @param taskId
     * @return
     */
    List<MissionEntity> listMissionByTaskId(int taskId);

    /**
     * 通过taskId批量查询架次名称和航线的Id
     *
     * @param taskIdList
     * @return
     */
    List<MissionEntity> listByTaskIds(List<Integer> taskIdList);

    /**
     * 使用{@link MissionService#startMission2(com.imapcloud.nest.pojo.dto.StartMissionParamDto)}替代
     */
    @Deprecated
    RestRes startMission(Integer missionId, Integer gainDataMode, Integer gainVideo, Boolean multiTask, Integer flightStrategy);

    RestRes startMission2(StartMissionParamDto paramDto);

    RestRes startMissionDji(StartMissionParamDto paramDto);

    @Deprecated
    RestRes uploadMission(Integer missionId);

    RestRes pauseMission(String nestId);

    RestRes continueMission(Boolean breakPoint, String nestId);

    RestRes stopMission(String nestId);

    Integer add(MissionEntity missionEntity);

    /**
     * 获取所有的任务分页列表(成果列表)
     *
     * @return
     */
    PageUtils getAllMissionPage(Map<String, Object> page, String missionName);

    /**
     * 获取移动终端的任务架次记录分页列表
     *
     * @param page
     * @param appId
     * @param taskName
     * @return
     */
    IPage<MissionDto> getAppMissionRecordPage(@Param("page") IPage<MissionDto> page, @Param("appId") String appId, @Param("taskName") String taskName);

    /**
     * 根据当前的机巢获取所有任务
     *
     * @return
     */
    PageUtils getAllMissionPageByNest(NestReqDto nestReqDto);


    /**
     * 批量查询MissionEntity通过taskIdList
     *
     * @param taskIdList
     * @return
     */
    @Deprecated
    List<MissionEntity> listAirLineIdAndTaskIdByTaskIdList(List<Integer> taskIdList);

    /**
     * 批量软删除
     *
     * @param idList
     * @return
     */
    int batchSoftDeleteByIds(List<Integer> idList);

    /**
     * 通过missionId获取nestId
     *
     * @param id
     * @return
     */
    Integer getNestIdById(Integer id);

    /**
     * 通过missionId获取nestUuid
     *
     * @param id
     * @return
     */
    String getNestUuidById(Integer id);

    /**
     * 通过missionId获取机巢的类型（固定、mini）
     *
     * @param id
     * @return
     */
    Integer getNestTypeById(Integer id);

    /**
     * 获取架次数据传输模式
     *
     * @param taskId
     * @return
     */
    RestRes listMissionDataTransMode(Integer taskId);

    /**
     * 获取架次拍照数量
     *
     * @param missionId
     * @return
     */
    Map<String, Integer> getMissionPhotoCountById(Integer missionId);

    /**
     * app开始任务
     *
     * @param missionId
     * @param gainDataMode
     * @param gainVideo
     * @return
     */
    RestRes appStartMission(Integer missionId, Integer gainDataMode, Integer gainVideo);

    /**
     * 获取app请求的航线数据
     *
     * @param missionId
     * @return
     */
    RestRes getAppMissionData(Integer missionId);

    /**
     * 获取app请求的航线数据--解析获取json
     *
     * @param missionEntity
     * @return
     */
    Map<String, Object> getAppAirLineRes(MissionEntity missionEntity);

    /**
     * 通过机巢uuid获取架次list
     *
     * @return
     */
    List<AppMissionAircraftDTO> getMissionByNestUuid(Integer limit, String nestUuid, Long lastTime);


    /**
     * 批量开始任务
     *
     * @param taskIdList
     * @param gainDataMode
     * @param gainVideo
     * @return
     */
    @Deprecated
    RestRes startBatchTask(List<Integer> taskIdList, Integer gainDataMode, Integer gainVideo, Integer flightStrategy);

    /**
     * 暂停任务
     *
     * @param nestId
     * @param type   1-> 暂停，2 -> 停止,3 -> 结束
     * @return
     */
    @Deprecated
    RestRes pauseOrStopOrEndBatchTask(Integer nestId, Integer type);

    /**
     * 暂停批量中的某一个任务
     *
     * @param nestId 机巢Id
     * @param taskId 任务Id
     * @return
     */
    @Deprecated
    RestRes cancelBatchTaskOfOne(Integer nestId, Integer taskId);


    /**
     * 继续任务
     *
     * @param nestId
     * @return
     */
    @Deprecated
    RestRes continueBatchTask(Integer nestId);

    /**
     * 重新执行任务
     *
     * @param nestId
     * @return
     */
    @Deprecated
    RestRes reStartBatchTask(Integer nestId);

    /**
     * 飞行前检查继续任务
     *
     * @param nestId
     * @return
     */
    @Deprecated
    RestRes batchTaskBeforeCheckContinueExec(Integer nestId);

    /**
     * 飞行前检查取消任务
     *
     * @param nestId
     * @return
     */
    @Deprecated
    RestRes batchTaskBeforeCheckCancelExec(Integer nestId);

    /**
     * 根据missionId获取airLineId
     *
     * @param missionId
     * @return
     */
    Integer getAirLineIdByMissionId(Integer missionId);

    /**
     * 根据taskId获取airLine
     */
    List<AirLineEntity> getAirLineByTaskId(Integer taskId);

    /**
     * 根据MissionId获取airLine
     */
    AirLineEntity getAirLineByMissionId(Integer missionId);


    /**
     * 根据missionId获取taskId
     *
     * @param missionId
     * @return
     */
    Integer getTaskIdByMissionId(Integer missionId);

    List<Map> getAllMissionsByNestTaskName(NestReqDto nestReqDto);

    Map<String, Object> getMissionDetails(Integer missionId);

    /**
     * 通过nestId查询missionId列表
     *
     * @param nestId
     * @return
     */
    @Deprecated
    List<Integer> listMissionIdByNestId(String nestId);

    /**
     * 开始自动任务队列
     *
     * @param param
     * @return
     */
    RestRes startAutoMissionQueue(AutoMissionQueueDTO param);


    /**
     * G503开启任务队列
     * @param param
     * @return
     */
    RestRes startG503AutoMissionQueue(G503AutoMissionQueueDTO param);

    /**
     * 取消任务列
     *
     * @param nestId
     * @return
     */
    RestRes cancelG503AutoMissionQueue(String nestId);

    RestRes rmFinishG503Mission(String nestId,Integer missionId);

    RestRes cancelAutoMissionQueue(String nestId);

    /**
     * 暂停任务列
     *
     * @param nestId
     * @return
     */
    RestRes pauseAutoMissionQueue(String nestId);

    /**
     * 继续任务列
     *
     * @param nestId
     * @return
     */
    RestRes continueAutoMissionQueue(String nestId);

    /**
     * 终止任务列
     *
     * @param nestId
     * @return
     */
    RestRes stopAutoMissionQueue(String nestId);

    /**
     * 任务列置顶
     *
     * @param missionQueueTopDTO
     * @return
     */
    RestRes autoMissionQueueTop(MissionQueueTopDTO missionQueueTopDTO);

    /**
     * 自动任务队列移除
     *
     * @param missionQueueTopDTO
     * @return
     */
    RestRes autoMissionQueueRemove(MissionQueueTopDTO missionQueueTopDTO);

    /**
     * 自动任务列增加
     *
     * @param missionQueueAddDTO
     * @return
     */
    RestRes autoMissionQueueAdd(MissionQueueAddDTO missionQueueAddDTO);

    /**
     * 通过missionId查询出关联的单位id列
     */
    List<String> listUnitIdsByMissionId(Integer missionId);

    /**
     * 自动任务列重发
     *
     * @param nestId
     * @return
     */
    RestRes autoMissionQueueRcStart(String nestId);

    /**
     * 获取架次id
     *
     * @param missionId
     * @return
     */
    RestRes getMissionTypeDetails(Integer missionId);

    /**
     * 立即执行自动同步数据
     *
     * @param nestId
     * @return
     */
    RestRes countDownImmediatelyAutoUploadData(String nestId);

    /**
     * 取消批量同步数据
     *
     * @param nestId
     * @return
     */
    RestRes cancelAutoUploadData(String nestId);

    /**
     * 检测基站是否能执行任务
     *
     * @param nestId
     * @return
     */
    RestRes checkNestEnableStartMission(String nestId);

    /**
     * 终止任务推送流程
     * @param taskId
     * @return
     */
    RestRes stopPushTaskProgressDto(Integer taskId);

    /**
     * 根据任务记录获取任务
     */
    MissionEntity queryMissionByMissionRecordsId(Integer missionRecordsId);
}
