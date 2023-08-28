package com.imapcloud.nest.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.util.MessageUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.common.annotation.Trace;
import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.enums.CameraParamsEnum;
import com.imapcloud.nest.model.MissionEntity;
import com.imapcloud.nest.model.MultiNestMissionDefaultRoleEntity;
import com.imapcloud.nest.model.TaskEntity;
import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.service.*;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.BaseNestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 任务表 前端控制器
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
@ApiSupport(author = "zhongtaibao@geoai.com", order = 1)
@Api(value = "", tags = "任务航线")
@RequestMapping("/task")
@RestController
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private AirLineService airLineService;

    @Autowired
    private MissionService missionService;

    @Autowired
    private NestService nestService;

    @Autowired
    private BaseNestService baseNestService;


    @Autowired
    private MultiNestMissionDefaultRoleService multiNestMissionDefaultRoleService;


    @Deprecated
    @GetMapping("/create/outer/airline")
    public RestRes createTaskIsOuterAirLine(@RequestParam Integer id, @RequestParam String name, HttpServletResponse response, HttpServletRequest request) {
        boolean result = taskService.saveTaskIsOuterAirLine(null);
        return result ? RestRes.ok() : RestRes.err();
    }


    /**
     * 任务列表接口
     *
     * @return
     */
    @GetMapping("/list/task/{nestId}/{tagId}")
    public RestRes listTaskListDto(@PathVariable String nestId, @PathVariable Integer tagId) {
        if (nestId != null && tagId != null) {
            ListTaskListDtoParam listTaskListDtoParam = new ListTaskListDtoParam();
            listTaskListDtoParam.setTagId(tagId)
                    .setNestId(nestId)
                    .setPaging(false);

            return taskService.listTaskListDto(listTaskListDtoParam);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PLEASE_CHECK_THE_PARAMETERS.getContent()));
    }

    @GetMapping("/list/task/{nestId}/{tagId}/{taskType}")
    public RestRes listTaskListDtoByType(@PathVariable String nestId, @PathVariable Integer tagId, @PathVariable Integer taskType) {
        if (nestId != null && tagId != null && taskType != null) {
            ListTaskListDtoParam listTaskListDtoParam = new ListTaskListDtoParam();
            listTaskListDtoParam.setTagId(tagId)
                    .setNestId(nestId)
                    .setPaging(false)
                    .setTaskType(taskType);

            return taskService.listTaskListDto(listTaskListDtoParam);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PLEASE_CHECK_THE_PARAMETERS.getContent()));
    }

    @GetMapping("/list/page/task/{nestId}/{tagId}/{currPage}/{pageSize}")
    public RestRes listPageTaskListDto(@PathVariable String nestId, @PathVariable Integer tagId, @PathVariable Integer currPage, @PathVariable Integer pageSize) {
        if (nestId != null && tagId != null && currPage != null && pageSize != null) {
            ListTaskListDtoParam listTaskListDtoParam = new ListTaskListDtoParam();
            listTaskListDtoParam.setTagId(tagId)
                    .setNestId(nestId)
                    .setPaging(true)
                    .setCurrPage(currPage)
                    .setPageSize(pageSize);

            return taskService.listTaskListDto(listTaskListDtoParam);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PLEASE_CHECK_THE_PARAMETERS.getContent()));
    }


    @GetMapping("/list/task/{nestId}")
    public RestRes listTaskListDto2(@PathVariable String nestId) {
        if (nestId != null) {
            ListTaskListDtoParam param = new ListTaskListDtoParam();
            param.setNestId(nestId).setTagId(0).setPaging(false);
            return taskService.listTaskListDto(param);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PLEASE_CHECK_THE_PARAMETERS.getContent()));
    }

    @GetMapping("/list/task/of/manage")
    public RestRes listTaskOfManageTaskDto(ListTaskRequestParamDto dto) {
        if (checkListTaskRequestParamDto(dto)) {
            return taskService.listTaskOfManageTaskDtoByPages(dto);
        }
        return RestRes.warn(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETER_VERIFICATION_NOT_PASS.getContent()));
    }

    @GetMapping("/list/task/of/manage/no/page/{nestId}")
    public RestRes listTaskOfManageTaskDtoNoPage(@PathVariable String nestId) {
        if (nestId != null) {
            return taskService.listTaskOfManageTaskDtoNoPage(nestId);
        }
        return RestRes.warn(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NEST_ID_CANNOT_BE_EMPTY.getContent()));
    }

    /**
     * 获取规划任务详情
     *
     * @param taskId
     * @return
     */
    @GetMapping("/find/task/details/{taskId}")
    public RestRes findTaskDetails(@PathVariable Integer taskId) {
        if (taskId != null) {
            return taskService.selectTaskDetails(taskId);
        }
        return RestRes.warn(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    /**
     * 编辑保存
     *
     * @param taskDetailsDto
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "本地航线修改", notes = "本地航线修改")
    @PostMapping("/update/task/details")
    public RestRes updateTaskDetails(@RequestBody @Valid TaskDetailsDto taskDetailsDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String defaultMessage = fieldError.getDefaultMessage();
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETER_VERIFICATION_NOT_PASS.getContent()) + defaultMessage);
        }
        return taskService.updateTaskDetailsDto(taskDetailsDto);
    }

    private void checkOrgCodeList(List<String> orgCodeList) {
        if (CollUtil.isEmpty(orgCodeList)) {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PLEASE_SELECT_UNITS.getContent()));
        }
        if (orgCodeList.size() > 10) {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NUMBER_OF_UNITS_UP_TO_10.getContent()));
        }
    }

    /**
     * 新建保存
     *
     * @param taskDetailsDto
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "航线保存", notes = "航线保存")
    @PostMapping("/save/task/details")
    public RestRes saveTaskDetails(@RequestBody @Valid TaskDetailsDto taskDetailsDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String defaultMessage = fieldError.getDefaultMessage();
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETER_VERIFICATION_NOT_PASS.getContent()) + defaultMessage);
        }
        checkOrgCodeList(taskDetailsDto.getOrgCodeList());
        taskDetailsDto.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
        for (String orgCode : taskDetailsDto.getOrgCodeList()) {

            TaskDetailsDto dto = new TaskDetailsDto();
            BeanUtils.copyProperties(taskDetailsDto, dto);
            dto.setUnitId(orgCode);
            RestRes restRes = taskService.saveTaskDetailsDto(dto);
            if (!restRes.isOk()) {
                return restRes;
            }
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_SAVE.getContent()));
    }


    @DeleteMapping("/delete/task/{taskId}")
    public RestRes deleteTask(@PathVariable Integer taskId) {
        return taskService.deleteTask(taskId);
    }

    @DeleteMapping("/batch/delete/task")
    public RestRes batchDeleteTask(@RequestBody List<Integer> taskIdList) {
        if (!CollectionUtils.isEmpty(taskIdList)) {
            return taskService.batchDeleteTask(taskIdList);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BATCH_DELETE_FAILED.getContent()));
    }

    @PostMapping("/copy/task/{taskId}")
    public RestRes copyTask(@PathVariable Integer taskId) {
        return taskService.copyTask(taskId);
    }

    /**
     * 请求动态规划航线，返回架次信息
     *
     * @param dynamicReqDto
     * @return
     */
    @PostMapping("/get/dynamic/airline")
    public RestRes getDynamicAirLine(@RequestBody DynamicReqDto dynamicReqDto) {
        return taskService.getDynamicAirLine(dynamicReqDto);
    }

    /**
     * 保存动态规划任务
     *
     * @param dynamicTaskDto
     * @param bindingResult
     * @return
     */
    @PostMapping("/save/dynamic/airline")
    public RestRes saveDynamicTask(@RequestBody @Valid DynamicTaskDto dynamicTaskDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String defaultMessage = fieldError.getDefaultMessage();
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETER_VERIFICATION_NOT_PASS.getContent()) + defaultMessage);
        }
        checkOrgCodeList(dynamicTaskDto.getOrgCodeList());
        dynamicTaskDto.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
        for (String orgCode : dynamicTaskDto.getOrgCodeList()) {
            DynamicTaskDto dto = new DynamicTaskDto();
            BeanUtils.copyProperties(dynamicTaskDto, dto);
            dto.setUnitId(orgCode);
            RestRes restRes = taskService.saveDynamicTask(dto);
            if (!restRes.isOk()) {
                return restRes;
            }
        }
        return RestRes.ok(MessageEnum.GEOAI_UOS_SUCCESS_SAVE.getContent());
    }

    /**
     * 易飞平台统一格式保存，(全景采集、线状巡视、正射影像)
     *
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "易飞平台统一格式保存", notes = "易飞平台统一格式保存")
    @PostMapping("/save/or/update/unify/task")
    public RestRes saveOrUpdateUnifyTask(@RequestBody @Valid UnifyTaskDto unifyTaskDto, BindingResult bindingResult) {
        if (unifyTaskDto == null || bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String defaultMessage = fieldError.getDefaultMessage();
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETER_VERIFICATION_NOT_PASS.getContent()) + defaultMessage);
        }
        checkOrgCodeList(unifyTaskDto.getOrgCodeList());
        unifyTaskDto.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
        for (String orgCode : unifyTaskDto.getOrgCodeList()) {

            UnifyTaskDto dto = new UnifyTaskDto();
            BeanUtils.copyProperties(unifyTaskDto, dto);
            dto.setUnitId(orgCode);
            RestRes restRes = taskService.saveOrUpdateUnifyTask(dto);
            if (!restRes.isOk()) {
                return restRes;
            }
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_OPERATION.getContent()));
    }

    /**
     * 易飞平台统一格式详情查询，(全景采集、线状巡视、正射影像)
     *
     * @param taskId
     * @return
     */
    @Trace(remarks = "易飞平台统一格式详情查询")
    @GetMapping("/find/unify/task/detail/{taskId}")
    public RestRes findUnifyTaskDetail(@PathVariable Integer taskId) {
        if (taskId != null) {
            return taskService.findUnifyTaskDetail(taskId);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_INCORRECT_PARAMETERS.getContent()));
    }

    @GetMapping("/select/camera/params/{cameraName}")
    public RestRes selectCameraParams(@PathVariable String cameraName) {
        if (cameraName != null) {
            CameraParamsEnum cp = CameraParamsEnum.getInstanceByCameraName(cameraName);
            Map<String, Object> cpMap = new HashMap<>(4);
            cpMap.put("focalLength", cp.getFocalLength());
            cpMap.put("sensorWidth", cp.getSensorWidth());
            cpMap.put("sensorHeight", cp.getSensorHeight());
            cpMap.put("pixelSizeWidth", cp.getPixelSizeWidth());
            return RestRes.ok(cpMap);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETERS_CANNOT_EMPTY.getContent()));
    }

    /**
     * 查找任务、架次、航线、航点详情
     *
     * @param taskId
     * @return
     */
    @GetMapping("/find/unify/detail/line/points/{taskId}")
    public RestRes findUnifyTaskDetails(@PathVariable Integer taskId) {
        if (taskId != null) {
            return taskService.findTaskDetailLinePoints(taskId);
        }
        return RestRes.noDataWarn();
    }

    /**
     * 编辑动态规划任务
     *
     * @param taskDto
     * @param bindingResult
     * @return
     */
    @PostMapping("/update/dynamic/airline")
    public RestRes updateDynamicTask(@RequestBody @Valid TaskDetailsDto taskDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String defaultMessage = fieldError.getDefaultMessage();
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETER_VERIFICATION_NOT_PASS.getContent()) + defaultMessage);
        }
        if (taskDto.getSpeed() == null) {
            taskDto.setSpeed(taskDto.getAutoFlightSpeed());
        }
        return taskService.updateDynamicTask(taskDto);
    }


    /**
     * 易飞终端任务类型查询
     *
     * @param param
     * @return
     */
    @GetMapping("/list/iflyer/task")
    public RestRes listIflyerTasks(@Valid IflyerParam param, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETER_VERIFICATION_NOT_PASS.getContent()));
        }
        return taskService.listIflyerTasks(param);
    }


    @PostMapping("/save/multi/nest/mission")
    public RestRes saveMultiNestMission(@Valid @RequestBody MultiNestMissionParamDTO nestMissionParam, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETER_VERIFICATION_NOT_PASS.getContent()));
        }
        List<MultiNestMissionParamDTO.NestMission> nestMissionList = nestMissionParam.getNestMissionList();
        Integer gainDataMode = nestMissionParam.getGainDataMode();
        Integer gainVideo = nestMissionParam.getGainVideo();
        Long sysUserId = Long.valueOf(nestMissionParam.getSysUserId());

        if (CollectionUtil.isEmpty(nestMissionList)) {
            //如果传回的该数组为空，则表示重置
            List<MultiNestMissionDefaultRoleEntity> multiNmList = multiNestMissionDefaultRoleService.list(
                    new QueryWrapper<MultiNestMissionDefaultRoleEntity>()
                            .lambda()
                            .eq(MultiNestMissionDefaultRoleEntity::getCreatorId, sysUserId)
                            .eq(MultiNestMissionDefaultRoleEntity::getDeleted, false)
            );

            multiNmList.forEach(m -> {
                m.setDeleted(true);
            });
            multiNestMissionDefaultRoleService.updateBatchById(multiNmList);
            return RestRes.ok(MessageEnum.GEOAI_UOS_SUCCESS_SAVE.getContent());
        }
        //首先查询出该sysUserId下的所有记录
        List<MultiNestMissionDefaultRoleEntity> multiNmList = multiNestMissionDefaultRoleService.list(new QueryWrapper<MultiNestMissionDefaultRoleEntity>()
                .lambda()
                .eq(MultiNestMissionDefaultRoleEntity::getCreatorId, sysUserId).eq(MultiNestMissionDefaultRoleEntity::getDeleted, false));

        Map<String, MultiNestMissionDefaultRoleEntity> multiMap = multiNmList.stream().collect(Collectors.toMap(m -> m.getNestId() + "" + m.getMissionId(), m -> m));

        //查询出上次保存的任务，然后统一改成deleted=true
        List<MultiNestMissionDefaultRoleEntity> notDeletedList = multiNmList.stream().filter(m -> m.getDeleted().equals(false)).map(m -> m.setDeleted(true)).collect(Collectors.toList());
        multiNestMissionDefaultRoleService.saveOrUpdateBatch(notDeletedList);

        List<Integer> taskIdList = nestMissionList.stream().map(MultiNestMissionParamDTO.NestMission::getTaskId).collect(Collectors.toList());
        List<MissionEntity> missionList = missionService.list(new QueryWrapper<MissionEntity>().lambda().in(MissionEntity::getTaskId, taskIdList).eq(MissionEntity::getDeleted, false));
        Map<Integer, List<MissionEntity>> taskMissionMap = missionList.stream().collect(Collectors.groupingBy(MissionEntity::getTaskId));

        //创建本次保存的任务，deleted=false
        List<MultiNestMissionDefaultRoleEntity> list = new ArrayList<>(nestMissionList.size());
        for (MultiNestMissionParamDTO.NestMission nestMission : nestMissionList) {
            List<MissionEntity> missionEntities = taskMissionMap.get(nestMission.getTaskId());
            if (CollectionUtil.isNotEmpty(missionEntities)) {
                MissionEntity missionEntity = missionEntities.get(0);
                MultiNestMissionDefaultRoleEntity newEntity = new MultiNestMissionDefaultRoleEntity();
                newEntity.setMissionId(missionEntity.getId())
                        .setMissionName(missionEntity.getName())
                        .setBaseNestId(nestMission.getNestId())
                        .setNestUuid(nestMission.getNestUuid())
                        .setCreatorId(sysUserId)
                        .setGainDataMode(gainDataMode)
                        .setGainVideo(gainVideo)
                        .setTaskId(nestMission.getTaskId())
                        .setDeleted(false);
                MultiNestMissionDefaultRoleEntity oldEntity = multiMap.get(nestMission.getNestId() + "" + missionEntity.getId());
                if (oldEntity != null) {
                    newEntity.setId(oldEntity.getId());
                }
                list.add(newEntity);
            }
        }

        if (CollectionUtil.isNotEmpty(list)) {
            multiNestMissionDefaultRoleService.saveOrUpdateBatch(list);
        }
        return RestRes.ok(MessageEnum.GEOAI_UOS_SUCCESS_SAVE.getContent());
    }

    @GetMapping("/list/multi/nest/mission/{sysUserId}")
    public RestRes listMultiNestMission(@PathVariable Long sysUserId) {
        List<MultiNestMissionDefaultRoleEntity> multiNmList = multiNestMissionDefaultRoleService.list(new QueryWrapper<MultiNestMissionDefaultRoleEntity>()
                .lambda()
                .eq(MultiNestMissionDefaultRoleEntity::getCreatorId, sysUserId)
                .eq(MultiNestMissionDefaultRoleEntity::getDeleted, false));

        if (CollectionUtil.isEmpty(multiNmList)) {
            return RestRes.warn(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_CORRESPONDING_DATA.getContent()));
        }
        List<Integer> taskIdList = multiNmList.stream().map(MultiNestMissionDefaultRoleEntity::getTaskId).collect(Collectors.toList());
        List<String> nestIdList = multiNmList.stream().map(MultiNestMissionDefaultRoleEntity::getBaseNestId).collect(Collectors.toList());
        List<TaskEntity> taskEntityList = taskService.list(new QueryWrapper<TaskEntity>().lambda().in(TaskEntity::getId, taskIdList).select(TaskEntity::getId, TaskEntity::getName));
        Map<Integer, String> taskMap = taskEntityList.stream().collect(Collectors.toMap(TaskEntity::getId, TaskEntity::getName));

//        List<NestEntity> nestEntityList = nestService.list(new QueryWrapper<NestEntity>().lambda().in(NestEntity::getId, nestIdList).select(NestEntity::getId, NestEntity::getName));
//        Map<Integer, String> nestMap = nestEntityList.stream().collect(Collectors.toMap(NestEntity::getId, NestEntity::getName));
        Map<String, String> nestMap = baseNestService.getNestNameMap(nestIdList);
        if (CollectionUtil.isNotEmpty(multiNmList)) {
            MultiNestMissionDefaultRoleEntity entity = multiNmList.get(0);
            Integer gainDataMode = entity.getGainDataMode();
            Integer gainVideo = entity.getGainVideo();
            MultiNestMissionParamDTO multiNestMissionParamDTO = new MultiNestMissionParamDTO();
            multiNestMissionParamDTO.setGainDataMode(gainDataMode).setGainVideo(gainVideo);
            List<MultiNestMissionParamDTO.NestMission> collect = multiNmList.stream().map(m -> {
                MultiNestMissionParamDTO.NestMission nestMission = new MultiNestMissionParamDTO.NestMission();
                nestMission.setTaskId(m.getTaskId());
                nestMission.setTaskName(taskMap.get(m.getTaskId()));
                nestMission.setNestName(nestMap.get(m.getBaseNestId()));
                nestMission.setNestId(m.getBaseNestId());
                nestMission.setNestUuid(m.getNestUuid());
                return nestMission;
            }).collect(Collectors.toList());
            multiNestMissionParamDTO.setNestMissionList(collect);

            Map<String, Object> resData = new HashMap<>(2);
            resData.put("nestMissionParam", multiNestMissionParamDTO);
            return RestRes.ok(resData);
        }
        return RestRes.warn(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_RECORD_SEARCH.getContent()));
    }

    @PostMapping("/save/or/update/file/insp/task")
    public RestRes saveOrUpdateFineInspTask(@RequestBody @Valid FineInspTaskDto fineInspTaskDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String defaultMessage = fieldError.getDefaultMessage();
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETER_VERIFICATION_NOT_PASS.getContent()) + defaultMessage);
        }
        checkOrgCodeList(fineInspTaskDto.getOrgCodeList());
        fineInspTaskDto.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
        for (String orgCode : fineInspTaskDto.getOrgCodeList()) {
            FineInspTaskDto dto = new FineInspTaskDto();
            BeanUtils.copyProperties(fineInspTaskDto, dto);
            dto.setUnitId(orgCode);
            RestRes restRes = taskService.saveOrUpdateFineInspTask(dto);
            if (!restRes.isOk()) {
                return restRes;
            }
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_OPERATION.getContent()));
    }

    @GetMapping("/find/fine/insp/task/details/{taskId}")
    public RestRes findFineInspTaskDetails(@PathVariable Integer taskId) {
        if (taskId != null) {
            return taskService.selectFineInspTask(taskId);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @GetMapping("/select/task/details/recordId/{missionRecordId}")
    public RestRes selectTaskDetailsByRecordId(@PathVariable Integer missionRecordId) {
        if (missionRecordId != null) {
            return taskService.selectTaskDetailsByRecordId(missionRecordId);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }


    private boolean checkListTaskRequestParamDto(ListTaskRequestParamDto dto) {

        if (dto != null) {
            // 网格化不参与校验
            if (!ObjectUtils.isEmpty(dto.getTaskType()) && dto.getTaskType() == 14) {
                return true;
            }

            if (dto.getMold() != null && dto.getPageSize() != null && dto.getCurrPage() != null) {
                return (dto.getMold() == 0 && dto.getNestId() != null) || (dto.getMold() == 1 && dto.getUnitId() != null);
            }
        }
        return false;
    }
}

