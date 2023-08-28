package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.TaskEntity;
import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.service.dto.out.TaskOutDTO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 任务表 服务类
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
public interface TaskService extends IService<TaskEntity> {
    /**
     * 保存SDK
     *
     * @param dto
     * @return
     */
    boolean saveTask(TaskDto dto);

    /**
     * 保存任务，航线为外部航线
     *
     * @param dto
     * @return
     */
    boolean saveTaskIsOuterAirLine(TaskDto dto);

    /**
     * 查询所有的任务
     *
     * @return
     */
    List<TaskEntity> listTask();


    /**
     * 查询任务以及架次详情
     *
     * @param param 机巢id,数据库中的自增Id
     * @return
     */
    RestRes listTaskListDto(ListTaskListDtoParam param);

    /**
     * 通过id查询任务
     *
     * @param id 任务id，数据库中的自增Id
     * @return
     */
    TaskEntity selectById(Integer id);

    /**
     * 查询
     *
     * @param dto
     * @return
     */
    RestRes listTaskOfManageTaskDtoByPages(ListTaskRequestParamDto dto);

    /**
     * 查询机巢任务
     *
     * @param nestId
     * @return
     */
    RestRes listTaskOfManageTaskDtoNoPage(String nestId);

    /**
     * 保存或者修改taskDto
     *
     * @param taskDetailsDto
     * @return
     */
    RestRes updateTaskDetailsDto(TaskDetailsDto taskDetailsDto);

    /**
     * 保存或者修改taskDetailsDto
     *
     * @param taskDetailsDto
     * @return
     */
    RestRes saveTaskDetailsDto(TaskDetailsDto taskDetailsDto);

    /**
     * 删除任务
     *
     * @param taskId
     * @return
     */
    RestRes deleteTask(Integer taskId);

    /**
     * 批量删除任务
     *
     * @param taskIdList
     * @return
     */
    RestRes batchDeleteTask(List<Integer> taskIdList);

    /**
     * 复制任务
     *
     * @param taskId
     * @return
     */
    RestRes copyTask(Integer taskId);

    /**
     * 查询任务详情
     *
     * @param taskId
     * @return
     */
    RestRes selectTaskDetails(Integer taskId);


    /**
     * 查询任务类型
     *
     * @param id
     * @return
     */
    Integer getTaskTypeById(Integer id);

    RestRes getDynamicAirLine(DynamicReqDto dynamicReqDto);

    RestRes saveDynamicTask(DynamicTaskDto dynamicTaskDto);

    /**
     * 保存全景采集任务
     *
     * @param unifyTaskDto
     * @return
     */
    RestRes saveOrUpdateUnifyTask(UnifyTaskDto unifyTaskDto);

    /**
     * 查询全景采集任务详情
     *
     * @param taskId
     * @return
     */
    RestRes findUnifyTaskDetail(Integer taskId);

    TaskEntity getByMissionId(Integer missionId);

    RestRes findTaskDetailLinePoints(Integer taskId);

    RestRes updateDynamicTask(TaskDetailsDto taskDto);

    /**
     * 易飞任务查询
     *
     * @param iflyerParam
     * @return
     */
    RestRes listIflyerTasks(IflyerParam iflyerParam);

    /**
     * 获取该单位的手动飞行航线id
     */
    Integer getAPPManualFlyTaskId(String orgCode);

    /**
     * 查询nestId
     *
     * @param airLineId
     * @return
     */
    TaskEntity getNestIdByAirLineId(Integer airLineId);

    /**
     * 保存或修改精细巡检任务
     *
     * @param fineInspTaskDto
     * @return
     */
    RestRes saveOrUpdateFineInspTask(FineInspTaskDto fineInspTaskDto);

    /**
     * 查看精细巡检任务详情
     *
     * @param taskId
     * @return
     */
    RestRes selectFineInspTask(Integer taskId);

    /**
     * 计算易飞终端航线
     *
     * @param dto
     * @param airLineStr
     * @return
     */
    Map<String, Object> computeIflyerTerminal(TaskDetailsDto dto, String airLineStr);

    /**
     * 通过missionRecordId查询任务详情，包括
     * @param missionRecordId
     * @return
     */
    RestRes selectTaskDetailsByRecordId(Integer missionRecordId);

    void clearTagNameRedisCache(Integer taskId);

    String getBaseNestIdByRecordsId(Integer missionRecordsId);

    DjiStartTaskParamDTO getDjiStartTaskParamDTOByMissionId(Integer missionId);

    /**
     * 根据基站信息查询任务
     * @param baseNestId
     * @param type
     * @return
     */
    List<TaskOutDTO> queryTaskByNest(List<String> baseNestId,Integer type);

    /**
     * 获取基站ID
     */
    List<TaskEntity> queryBatchTaskEntities(List<Integer> taskIds);
}
