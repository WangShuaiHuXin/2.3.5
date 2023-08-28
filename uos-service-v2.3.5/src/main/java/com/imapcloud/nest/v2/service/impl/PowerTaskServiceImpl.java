package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.ResultUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.imapcloud.nest.enums.RoleIdenValueEnum;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.AIAnalysisTaskTypeEnum;
import com.imapcloud.nest.v2.common.enums.PowerTaskStateEnum;
import com.imapcloud.nest.v2.manager.ai.SystemAIAnalysisTaskFinishedEvent;
import com.imapcloud.nest.v2.manager.dataobj.in.RocketmqInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.UdaAnalysisInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.*;
import com.imapcloud.nest.v2.manager.feign.AccountServiceClient;
import com.imapcloud.nest.v2.manager.feign.UdaAnalysisServiceClient;
import com.imapcloud.nest.v2.manager.rest.OrgAccountManager;
import com.imapcloud.nest.v2.manager.sql.PowerDataManager;
import com.imapcloud.nest.v2.manager.sql.PowerMeterDataManager;
import com.imapcloud.nest.v2.manager.sql.PowerMeterFlightDetailInfraredManager;
import com.imapcloud.nest.v2.service.*;
import com.imapcloud.nest.v2.service.dto.TaskInfoDTO;
import com.imapcloud.nest.v2.service.dto.in.PowerTaskInDTO;
import com.imapcloud.sdk.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 电力AI任务
 *
 * @author boluo
 * @date 2023-02-28
 */
@Slf4j
@Service
public class PowerTaskServiceImpl implements PowerTaskService {

    @Resource
    private PowerDataManager powerDataManager;

    @Resource
    private RocketmqService rocketmqService;

    @Resource
    private PowerInfraredService powerInfraredService;

    @Resource
    private PowerDefectService powerDefectService;

    @Resource
    private RedisService redisService;

    @Resource
    private OrgAccountManager orgAccountManager;

    @Resource
    private UdaAnalysisServiceClient udaAnalysisServiceClient;

    @Resource
    private PowerMeterDataService powerMeterDataService;

    @Resource
    private PowerMeterDataManager powerMeterDataManager;

    @Resource
    private PowerMeterFlightDetailInfraredManager powerMeterFlightDetailInfraredManager;

    @Resource
    private AccountServiceClient accountServiceClient;

    @Override
    public void autoTask(PowerTaskInDTO.AutoTaskInDTO autoTaskInDTO) {

        log.info("#PowerTaskServiceImpl.autoTask# autoTaskInDTO={}", autoTaskInDTO);

        String orgCode = autoTaskInDTO.getOrgCode();
        RoleIdenValueEnum roleIdenValueEnum = autoTaskInDTO.getRoleIdenValueEnum();

        if (roleIdenValueEnum == null) {
            log.info("#PowerTaskServiceImpl.autoTask# orgCode={} roleIdenValueEnum is null", orgCode);
            return;
        }

        log.info("#PowerTaskServiceImpl.autoTask# orgCode={}", orgCode);
        // 开启自动识别
        boolean autoDiscern = powerDataManager.autoDiscern(orgCode);
        if (!autoDiscern) {

            log.info("#PowerTaskServiceImpl.autoTask# orgCode={} not auto task", orgCode);
            return;
        }

        int idenValue = autoTaskInDTO.getRoleIdenValueEnum().getIdenValue();
        List<String> detailIdList = autoTaskInDTO.getDetailIdList();
        // 查询单位是否设置自动AI识别算法
        List<PowerDiscernFunSettingInfosOutDO> outDOList = powerDataManager
                .queryOrgFunctionSettings(orgCode, String.valueOf(idenValue));
        if (CollUtil.isEmpty(outDOList)) {
            log.info("#PowerTaskServiceImpl.autoTask# orgCode={} not auto task idenValue={}", orgCode, idenValue);
            return;
        }
        List<String> functionIdList = outDOList.stream()
                .map(PowerDiscernFunSettingInfosOutDO::getDiscernFunId).collect(Collectors.toList());

        log.info("#PowerTaskServiceImpl.autoTask# roleIdenValueEnum={}", roleIdenValueEnum);
        try {
            // 检测算法是否被授权
            UdaAnalysisInDO.CheckPictureTaskInDO checkPictureTaskInDO = new UdaAnalysisInDO.CheckPictureTaskInDO();
            checkPictureTaskInDO.setOrgCode(autoTaskInDTO.getOrgCode());
            List<Long> longList = functionIdList.stream()
                    .map(Long::parseLong).collect(Collectors.toList());
            checkPictureTaskInDO.setFunctionIdList(longList);
            checkPictureTaskInDO.setPictureNum(autoTaskInDTO.getDetailIdList().size());

            Result<UdaAnalysisOutDO.CheckPictureTaskOutDO> checkPictureTask = udaAnalysisServiceClient.checkPictureTask(checkPictureTaskInDO);
            UdaAnalysisOutDO.CheckPictureTaskOutDO pictureTaskOutDO = ResultUtils.getData(checkPictureTask);
            // SUCCESS(0, "成功"),NO_AUTH(1, "算法授权问题"),NO_ENABLE(2, "算法未启用");
            if (pictureTaskOutDO.getStatus() != 0) {
                sendChannel13(autoTaskInDTO);
                return;
            }
        } catch (Exception e) {
            log.error("#PowerTaskServiceImpl.autoTask# autoTaskInDTO={}", autoTaskInDTO, e);
            sendChannel13(autoTaskInDTO);
            return;
        }
        // 修改图片任务状态并且将暂停的数据取消
        if (RoleIdenValueEnum.ABNORMAL_FIND_DL_HWCW_NEW == roleIdenValueEnum) {

            powerInfraredService.updateTaskStatePre(detailIdList, autoTaskInDTO.getDataId());
        } else if(RoleIdenValueEnum.ABNORMAL_FIND_DL_QXSB_NEW == roleIdenValueEnum) {

            powerDefectService.updateTaskStatePre(detailIdList, autoTaskInDTO.getDataId());
        }
        log.info("#PowerTaskServiceImpl.autoTask# end roleIdenValueEnum={}", roleIdenValueEnum);

        PowerTaskInDTO.AddTaskInDTO addTaskInDTO = new PowerTaskInDTO.AddTaskInDTO();
        addTaskInDTO.setDataId(autoTaskInDTO.getDataId());
        addTaskInDTO.setDetailIdList(detailIdList);
        addTaskInDTO.setOrgCode(orgCode);
        addTaskInDTO.setRoleIdenValueEnum(roleIdenValueEnum);
        addTaskInDTO.setFunctionIdList(functionIdList);
        addTaskInDTO.setSystem(true);
        addTask(addTaskInDTO);

        if (RoleIdenValueEnum.ABNORMAL_FIND_DL_HWCW_NEW == roleIdenValueEnum) {

            powerInfraredService.sendWs(autoTaskInDTO.getDataId(), false);
        } else if(RoleIdenValueEnum.ABNORMAL_FIND_DL_QXSB_NEW == roleIdenValueEnum) {

            powerDefectService.sendWs(autoTaskInDTO.getDataId(), false);
        }
    }

    private void sendChannel13(PowerTaskInDTO.AutoTaskInDTO autoTaskInDTO) {

        RoleIdenValueEnum roleIdenValueEnum = autoTaskInDTO.getRoleIdenValueEnum();
        List<String> dataIdList = Lists.newArrayList(autoTaskInDTO.getDataId());
        List<PowerMeterFlightDataOutDO> dataOutDOList = powerMeterDataManager.selectListByDataIdList(dataIdList);

        if (CollUtil.isEmpty(dataOutDOList)) {
            return;
        }
        PowerMeterFlightDataOutDO powerMeterFlightDataOutDO = dataOutDOList.get(0);
        SystemAIAnalysisTaskFinishedEvent.EventInfo eventInfo = new SystemAIAnalysisTaskFinishedEvent.EventInfo();
        eventInfo.setOrgCode(autoTaskInDTO.getOrgCode());
        eventInfo.setAiTaskId(autoTaskInDTO.getDataId());
        eventInfo.setAiTaskName(powerMeterFlightDataOutDO.getTaskFlyIndexName());
        AIAnalysisTaskTypeEnum aiTaskType = AIAnalysisTaskTypeEnum.DIALS;
        if (RoleIdenValueEnum.ABNORMAL_FIND_DL_HWCW_NEW == roleIdenValueEnum) {

            aiTaskType = AIAnalysisTaskTypeEnum.INFRARED;
        } else if(RoleIdenValueEnum.ABNORMAL_FIND_DL_QXSB_NEW == roleIdenValueEnum) {

            aiTaskType = AIAnalysisTaskTypeEnum.DEFECTS;
        }
        eventInfo.setAiTaskType(aiTaskType.getType());
        eventInfo.setAiTaskState(false);
        eventInfo.setCenterBaseId(autoTaskInDTO.getDataId());
        eventInfo.setMessage("自动识别失败，单位算法授权异常。");

        Set<String> accountSet = getAccountSetByOrgCode(autoTaskInDTO.getOrgCode());
        powerMeterDataService.sendChannel13Info(eventInfo, accountSet);
    }

    @Override
    public void manualTask(String orgCode) {

        powerDefectService.manualSendWs(orgCode);
        powerInfraredService.manualSendWs(orgCode);
    }

    @Override
    public TaskInfoDTO.DetailInfoDTO removeOneTaskPicture(RocketmqInDO.TaskMqInfo taskMqInfo) {

        String taskLock = getLockKey(taskMqInfo.getDataId());
        String uuid = taskMqInfo.getDataId();
        try {
            redisService.lock(taskLock, uuid, 180);
            Object o = redisService.get(taskMqInfo.getTaskKey());
            if (o == null) {
                return null;
            }
            TaskInfoDTO taskInfoDTO = JSONUtil.toBean(JSONUtil.parseObj(o), TaskInfoDTO.class);
            TaskInfoDTO.DetailInfoDTO detailInfoDTO = taskInfoDTO.getDetailInfoDTOList().remove(0);
            redisService.set(taskMqInfo.getTaskKey(), taskInfoDTO);
            return detailInfoDTO;
        } catch (Exception e) {
            return null;
        } finally {
            redisService.releaseLock(taskLock, uuid);
        }
    }

    @Override
    public boolean taskEmpty(RocketmqInDO.TaskMqInfo taskMqInfo) {
        try {
            Object o = redisService.get(taskMqInfo.getTaskKey());
            if (o == null) {
                return true;
            }
            TaskInfoDTO taskInfoDTO = JSONUtil.toBean(JSONUtil.parseObj(o), TaskInfoDTO.class);
            return CollUtil.isEmpty(taskInfoDTO.getDetailInfoDTOList());
        } catch (Exception e) {
            log.error("#PowerTaskServiceImpl.delTask# taskMqInfo={}", taskMqInfo, e);
            return true;
        }
    }

    private String getLockKey(String dataId) {
        return String.format("power:lock:%s", dataId);
    }

    private String getTaskKey(String dataId) {

        return String.format("power:task:%s", dataId);
    }

    @Override
    public TaskInfoDTO getTaskInfoByDataId(String dataId) {

        String taskKey = getTaskKey(dataId);
        Object o = redisService.get(taskKey);
        if (o == null) {
            return null;
        }
        return JSONUtil.toBean(JSONUtil.parseObj(o), TaskInfoDTO.class);
    }

    @Override
    public void delTask(String dataId) {
        log.info("#PowerTaskServiceImpl.delTask# dataId={}", dataId);
        String taskLock = getLockKey(dataId);
        String taskKey = getTaskKey(dataId);
        String uuid = dataId;
        try {
            redisService.lock(taskLock, uuid, 60);
            redisService.del(taskKey);
        } finally {
            redisService.releaseLock(taskLock, uuid);
        }
    }

    @Override
    public void addTask(PowerTaskInDTO.AddTaskInDTO addTaskInDTO) {

        log.info("#PowerTaskServiceImpl.addTask# addTaskInDTO={}", addTaskInDTO);
        String dataId = addTaskInDTO.getDataId();
        String taskLock = getLockKey(dataId);
        String taskKey = getTaskKey(dataId);
        String uuid = dataId;
        try {
            redisService.lock(taskLock, uuid, 60);

            // 是否是新增
            boolean add = true;
            // 获取任务信息
            Object o = redisService.get(taskKey);
            TaskInfoDTO taskInfoDTO;
            if (o != null) {

                add = false;
                taskInfoDTO = JSONUtil.toBean(JSONUtil.parseObj(o), TaskInfoDTO.class);
            } else {
                log.info("#PowerTaskServiceImpl.addTask# add task info addTaskInDTO={}", addTaskInDTO);
                taskInfoDTO = new TaskInfoDTO();
                taskInfoDTO.setDataId(addTaskInDTO.getDataId());
                taskInfoDTO.setSystem(addTaskInDTO.getSystem());
            }
            // 每张图片的识别算法
            setDetailInfoDTOList(taskInfoDTO, addTaskInDTO);

            // 需要接收的用户
            setAccountIdSet(taskInfoDTO, addTaskInDTO);
            redisService.set(taskKey, taskInfoDTO);
            if (add) {
                log.info("#PowerTaskServiceImpl.addTask# send rocket info addTaskInDTO={}", addTaskInDTO);
                // 发送rocketMQ
                RocketmqInDO.TaskMqInfo taskMqInfo = new RocketmqInDO.TaskMqInfo();
                taskMqInfo.setTaskKey(taskKey);
                taskMqInfo.setDataId(addTaskInDTO.getDataId());
                taskMqInfo.setRoleIdenValueEnum(addTaskInDTO.getRoleIdenValueEnum());
                rocketmqService.sendTask(taskMqInfo);
            }
        } finally {
            redisService.releaseLock(taskLock, uuid);
        }
    }
    private void setDetailInfoDTOList(TaskInfoDTO taskInfoDTO, PowerTaskInDTO.AddTaskInDTO addTaskInDTO) {
        List<TaskInfoDTO.DetailInfoDTO> detailInfoDTOList = Lists.newArrayList();
        if (CollUtil.isNotEmpty(taskInfoDTO.getDetailInfoDTOList())) {
            detailInfoDTOList.addAll(taskInfoDTO.getDetailInfoDTOList());
        }
        for (String detailId : addTaskInDTO.getDetailIdList()) {
            TaskInfoDTO.DetailInfoDTO detailInfoDTO = new TaskInfoDTO.DetailInfoDTO();
            detailInfoDTO.setDetailId(detailId);
            detailInfoDTO.setFunctionIdList(addTaskInDTO.getFunctionIdList());
            detailInfoDTO.setOrgCode(addTaskInDTO.getOrgCode());
            detailInfoDTOList.add(detailInfoDTO);
        }
        taskInfoDTO.setDetailInfoDTOList(detailInfoDTOList);
    }
    private void setAccountIdSet(TaskInfoDTO taskInfoDTO, PowerTaskInDTO.AddTaskInDTO addTaskInDTO) {
        Set<String> accountIdSet = Sets.newHashSet();
        if (CollUtil.isNotEmpty(taskInfoDTO.getAccountIdSet())) {
            accountIdSet.addAll(taskInfoDTO.getAccountIdSet());
        }
        if (StringUtil.isNotEmpty(addTaskInDTO.getAccountId())) {
            accountIdSet.add(addTaskInDTO.getAccountId());
        } else {
            List<OrgAccountOutDO> orgAccountOutDOList = orgAccountManager
                    .listOrgAccountRefInfos(Collections.singletonList(addTaskInDTO.getOrgCode()));
            if (CollUtil.isNotEmpty(orgAccountOutDOList)) {
                for (OrgAccountOutDO orgAccountOutDO : orgAccountOutDOList) {
                    accountIdSet.add(orgAccountOutDO.getAccountId());
                }
            }
        }
        taskInfoDTO.setAccountIdSet(accountIdSet);
    }

    private Set<String> getAccountSetByOrgCode(String orgCode) {
        List<OrgAccountOutDO> orgAccountOutDOList = orgAccountManager
                .listOrgAccountRefInfos(Collections.singletonList(orgCode));
        if (CollUtil.isEmpty(orgAccountOutDOList)) {
            return Collections.emptySet();
        }
        List<String> accountIdList = orgAccountOutDOList.stream()
                .map(OrgAccountOutDO::getAccountId).collect(Collectors.toList());

        Result<List<AccountOutDO>> result = accountServiceClient.listAccountInfos(accountIdList);

        List<AccountOutDO> accountOutDOList = ResultUtils.getData(result);

        if (CollUtil.isEmpty(accountOutDOList)) {
            return Collections.emptySet();
        }

        return accountOutDOList.stream().map(AccountOutDO::getAccount).collect(Collectors.toSet());
    }
}
