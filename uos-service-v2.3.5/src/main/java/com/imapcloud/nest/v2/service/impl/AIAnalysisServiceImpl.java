package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.constant.SymbolConstants;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.core.util.JsonUtils;
import com.geoai.common.web.exception.BizParameterException;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.geoai.common.web.util.ResultUtils;
import com.geoai.rocketmq.spring.core.RocketMQTemplate;
import com.google.common.collect.Lists;
import com.imapcloud.nest.enums.RoleIdenValueEnum;
import com.imapcloud.nest.v2.common.enums.*;
import com.imapcloud.nest.v2.common.exception.AIRecFunctionExpiredException;
import com.imapcloud.nest.v2.common.properties.AnalysisConfig;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.dao.entity.AIAnalysisPhotoEntity;
import com.imapcloud.nest.v2.dao.entity.AIAnalysisTaskEntity;
import com.imapcloud.nest.v2.dao.entity.PowerMeterFlightDataEntity;
import com.imapcloud.nest.v2.dao.entity.PowerMeterFlightDetailEntity;
import com.imapcloud.nest.v2.dao.po.in.AIAnalysisPhotoExtPO;
import com.imapcloud.nest.v2.manager.ai.*;
import com.imapcloud.nest.v2.manager.dataobj.in.CommonFileInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.OrgAlgorithmQueryInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.UdaAnalysisInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.UdaAnalysisTaskInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.*;
import com.imapcloud.nest.v2.manager.feign.AccountServiceClient;
import com.imapcloud.nest.v2.manager.feign.UdaAnalysisServiceClient;
import com.imapcloud.nest.v2.manager.rest.UploadManager;
import com.imapcloud.nest.v2.manager.sql.*;
import com.imapcloud.nest.v2.service.*;
import com.imapcloud.nest.v2.service.dto.in.*;
import com.imapcloud.nest.v2.service.dto.out.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.message.MessageId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.io.UrlResource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AI分析业务接口实现
 *
 * @author Vastfy
 * @date 2022/11/2 14:00
 * @since 2.1.4
 */
@Slf4j
@Service
public class AIAnalysisServiceImpl implements AIAnalysisService, ApplicationEventPublisherAware {

    private static final String UDA_ALGORITHM_INVALID_CODE = "10600";
    private static final String UDA_ALGORITHM_UNAUTHORIZED_CODE = "10601";

    @Resource
    private AIAnalysisTaskManager aiAnalysisTaskManager;

    @Resource
    private AIAnalysisPhotoManager aiAnalysisPhotoManager;

    @Resource
    private UdaAnalysisServiceClient udaAnalysisServiceClient;

    @Resource
    private DataAnalysisBaseService dataAnalysisBaseService;

    @Resource
    private DataAnalysisDetailService dataAnalysisDetailService;

    @Resource
    private InnerTransactionDelegator innerTransactionDelegator;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private PowerDataManager powerDataManager;

    @Resource
    private PowerMeterDetailService powerMeterDetailService;

    @Resource
    private PowerMeterDataService powerMeterDataService;

    @Resource
    private AccountServiceClient accountServiceClient;

    @Resource
    private PowerMeterFlightDetailInfraredManager powerMeterFlightDetailInfraredManager;

    @Resource
    private PowerMeterFlightDetailDefectManager powerMeterFlightDetailDefectManager;

    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private PowerInfraredService powerInfraredService;

    @Override
    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public List<AIAnalysisRepoOutDTO> getGrantedRecFunctions(String orgCode) {
        Result<List<UdaAlgorithmRepoOutDO>> result;
        try {
            if (StringUtils.hasText(orgCode)) {
                OrgAlgorithmQueryInDO params = new OrgAlgorithmQueryInDO();
                params.setRecDataType(RecDataTypeEnum.IMAGE.getType());
                params.setOrgCode(orgCode);
                result = udaAnalysisServiceClient.getUdaGrantedFunctionsByOrg(params);
            } else {
                result = udaAnalysisServiceClient.getUdaGrantedFunctions(RecDataTypeEnum.IMAGE.getType());
            }
        } catch (Exception e) {
            log.error("获取算法识别功能列表失败", e);
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_GET_ALGORITHM_FUNCTIONS.getContent()));
//            throw new BizException("获取算法识别功能列表失败，请稍后重试！");
        }
        if (!result.isOk()) {
            return Collections.emptyList();
        }
        List<UdaAlgorithmRepoOutDO> data = result.getData();
        if (CollectionUtils.isEmpty(data)) {
            return Collections.emptyList();
        }
        return data.stream()
                .filter(r -> !CollectionUtils.isEmpty(r.getDiscernFunctionInfos()))
                .map(r -> {
                    AIAnalysisRepoOutDTO info = new AIAnalysisRepoOutDTO();
                    info.setCategoryId(r.getStorageId());
                    info.setCategoryName(r.getName());
                    if (!CollectionUtils.isEmpty(r.getDiscernFunctionInfos())) {
                        List<AIAnalysisRepoOutDTO.AIRecFunction> aiRecFunctions = r.getDiscernFunctionInfos()
                                .stream()
                                .map(e -> {
                                    AIAnalysisRepoOutDTO.AIRecFunction fun = new AIAnalysisRepoOutDTO.AIRecFunction();
                                    fun.setFunctionId(e.getFunctionId());
                                    fun.setFunctionName(e.getName());
                                    fun.setVersion(e.getVersion());
                                    return fun;
                                })
                                .collect(Collectors.toList());
                        info.setRecFunctions(aiRecFunctions);
                    }
                    return info;
                })
                .collect(Collectors.toList());
    }
    private String pictureTaskInfrared(AIRecognitionTaskInDTO data) {

        powerInfraredService.checkRunning(data.getDataPhotoIds(), true);

        // 授权校验
        List<PowerMeterFlightDetailInfraredOutDO> infraredOutDOList = powerMeterFlightDetailInfraredManager
                .queryByDataIdCollection(Lists.newArrayList(data.getBaseDataId()));
        if (CollUtil.isEmpty(infraredOutDOList)) {
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_VALID_FLIGHT_PHOTO_WAS_FOUND.getContent()));
        }
        for (PowerMeterFlightDetailInfraredOutDO powerMeterFlightDetailInfraredOutDO : infraredOutDOList) {

            if (!data.getDataPhotoIds().contains(powerMeterFlightDetailInfraredOutDO.getDetailId())) {
                continue;
            }
            if (powerMeterFlightDetailInfraredOutDO.getTaskState() != PowerTaskStateEnum.TASK_INIT.getCode()) {
                throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWER_PICTURE_TASK_REPEAT_ADD.getContent()));
            }
        }

        powerInfraredService.updateTaskStatePre(data.getDataPhotoIds(), data.getBaseDataId());

        // 添加任务并发送rocketMQ
        PowerTaskInDTO.AddTaskInDTO addTaskInDTO = new PowerTaskInDTO.AddTaskInDTO();
        addTaskInDTO.setDataId(data.getBaseDataId());
        addTaskInDTO.setDetailIdList(data.getDataPhotoIds());
        addTaskInDTO.setOrgCode(data.getOrgCode());
        addTaskInDTO.setFunctionIdList(data.getRecognitionFunIds());
        addTaskInDTO.setSystem(false);
        addTaskInDTO.setAccountId(data.getAccountId());
        addTaskInDTO.setRoleIdenValueEnum(RoleIdenValueEnum.ABNORMAL_FIND_DL_HWCW_NEW);
        powerTaskService.addTask(addTaskInDTO);
        // 发送WS
        powerInfraredService.sendWs(data.getBaseDataId(), true);
        return "";
    }

    @Resource
    private PowerDefectService powerDefectService;

    @Resource
    private PowerTaskService powerTaskService;

    private String pictureTaskDefect(AIRecognitionTaskInDTO data) {

        powerDefectService.checkRunning(data.getDataPhotoIds(), true);

        // 授权校验
        List<PowerMeterFlightDetailDefectOutDO> defectOutDOList = powerMeterFlightDetailDefectManager
                .selectListByDataIdList(Lists.newArrayList(data.getBaseDataId()));
        if (CollUtil.isEmpty(defectOutDOList)) {
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_VALID_FLIGHT_PHOTO_WAS_FOUND.getContent()));
        }
        for (PowerMeterFlightDetailDefectOutDO defectOutDO : defectOutDOList) {

            if (!data.getDataPhotoIds().contains(defectOutDO.getDetailId())) {
                continue;
            }
            if (defectOutDO.getTaskState() != PowerTaskStateEnum.TASK_INIT.getCode()) {
                throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWER_PICTURE_TASK_REPEAT_ADD.getContent()));
            }
        }
        powerDefectService.updateTaskStatePre(data.getDataPhotoIds(), data.getBaseDataId());

        // 添加任务并发送rocketMQ
        PowerTaskInDTO.AddTaskInDTO addTaskInDTO = new PowerTaskInDTO.AddTaskInDTO();
        addTaskInDTO.setDataId(data.getBaseDataId());
        addTaskInDTO.setDetailIdList(data.getDataPhotoIds());
        addTaskInDTO.setOrgCode(data.getOrgCode());
        addTaskInDTO.setFunctionIdList(data.getRecognitionFunIds());
        addTaskInDTO.setSystem(false);
        addTaskInDTO.setAccountId(data.getAccountId());
        addTaskInDTO.setRoleIdenValueEnum(RoleIdenValueEnum.ABNORMAL_FIND_DL_QXSB_NEW);
        powerTaskService.addTask(addTaskInDTO);
        // 发送WS
        powerDefectService.sendWs(data.getBaseDataId(), true);
        return "";
    }

    private void checkAiTask(AIRecognitionTaskInDTO data) {

        UdaAnalysisInDO.CheckPictureTaskInDO checkPictureTaskInDO = new UdaAnalysisInDO.CheckPictureTaskInDO();
        checkPictureTaskInDO.setOrgCode(data.getOrgCode());
        List<Long> longList = data.getRecognitionFunIds().stream()
                .map(Long::parseLong).collect(Collectors.toList());
        checkPictureTaskInDO.setFunctionIdList(longList);
        checkPictureTaskInDO.setPictureNum(data.getDataPhotoIds().size());

        Result<UdaAnalysisOutDO.CheckPictureTaskOutDO> checkPictureTask = udaAnalysisServiceClient.checkPictureTask(checkPictureTaskInDO);
        UdaAnalysisOutDO.CheckPictureTaskOutDO pictureTaskOutDO = ResultUtils.getData(checkPictureTask);
        // SUCCESS(0, "成功"),NO_AUTH(1, "算法授权问题"),NO_ENABLE(2, "算法未启用");
        if (pictureTaskOutDO.getStatus() != 0) {
            if (CollUtil.isNotEmpty(pictureTaskOutDO.getErrorInfoList())) {
                throw new AIRecFunctionExpiredException(JsonUtils.writeJson(pictureTaskOutDO.getErrorInfoList()));
            }
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ORG_ALGORITHM_UNAUTHORIZED.getContent()));
        }
    }

    @Override
    public synchronized String createManualAIAnalysisTask(AIRecognitionTaskInDTO data) {

        // 红外测温和缺陷识别
        if (AIAnalysisTaskTypeEnum.INFRARED.getType() == data.getAiTaskType()) {
            checkAiTask(data);
            return pictureTaskInfrared(data);
        } else if (AIAnalysisTaskTypeEnum.DEFECTS.getType() == data.getAiTaskType()) {
            checkAiTask(data);
            return pictureTaskDefect(data);
        }
        // 业务参数校验
        // a. 全量识别任务数量限制
        AnalysisConfig analysisConfig = geoaiUosProperties.getAnalysis();
        if (AIAnalysisTasks.getAllTaskCounts() >= analysisConfig.getAiTaskCount()) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SYSTEM_AI_ANALYSIS_TASK_EXCEED_LIMIT.getContent()));
//            throw new BizException("当前系统AI识别任务数量超出限制，请稍后重试");
        }
        // b. 单用户识别任务数量限制
        String accountId = TrustedAccessTracerHolder.get().getAccountId();
        if (AIAnalysisTasks.getTaskCounts(accountId, true) >= analysisConfig.getAccountAiTaskCount()) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ACCOUNT_AI_ANALYSIS_TASK_EXCEED_LIMIT.getContent()));
//            throw new BizException("当前用户AI识别任务数量超出限制，请稍后重试");
        }
        UdaAnalysisTaskInDO udaAnalysisTask;
        AIAnalysisTaskEntity uosAiTask;
        Set<AIAnalysisPhotoExtPO> aiAnalysisPhotos;
        // 表计
        if(AIAnalysisTaskTypeEnum.DIALS.matchEquals(data.getAiTaskType())){
            // c. base数据是否存在
            PowerMeterFlightDataEntity dab = checkAndGotDataInfo(data.getBaseDataId());
            // d. detail数据是否存在且对应同一个base数据
            Map<String, PowerMeterFlightDetailEntity> dadMap = checkAndGotDetailInfos(data, dab);
            uosAiTask = buildAIAnalysisTask(data, dab, dadMap);
            aiAnalysisPhotos = buildAIAnalysisPhoto2(uosAiTask, dadMap);
        }
        // 综合
        else{
            // c. base数据是否存在
            DataAnalysisBaseSimpleOutDTO dab = checkAndGotCenterBaseInfo(data.getBaseDataId());
            // d. detail数据是否存在且对应同一个base数据
            Map<Long, DataAnalysisDetailOutDTO> dadMap = checkAndGotCenterDetailInfos(data, dab);
            uosAiTask = buildAIAnalysisTask(data, dab, dadMap);
            aiAnalysisPhotos = buildAIAnalysisPhoto(uosAiTask, dadMap);
        }
        udaAnalysisTask = buildUdaAIAnalysisTaskData(data, aiAnalysisPhotos);
        // 调用UDA服务接口-创建并执行分析任务
        Result<UdaAlTaskResponseOutDO> result;
        try {
            result = udaAnalysisServiceClient.createAndExecTask(udaAnalysisTask);
        } catch (Exception e) {
            log.error("算法创建任务接口调用失败", e);
            result = Result.error("创建AI分析任务失败，算法服务不可用");
        }
        if (result.isOk()) {
            uosAiTask.setAiTaskId(result.getData().getTaskId());
            aiAnalysisPhotos.forEach(r -> r.setAiTaskId(uosAiTask.getAiTaskId()));
            boolean success = innerTransactionDelegator.createAnalysisTask(uosAiTask, aiAnalysisPhotos);
            if (success) {
                sendTaskTimeoutCheckMessage(uosAiTask.getTaskId());
                IAIAnalysisTask aiAnalysisTask = getOrCreateAIAnalysisTask(uosAiTask);
                asyncNotifyClientRefreshProcessing(aiAnalysisTask.getTaskProcess(), true);
            }
            return uosAiTask.getTaskId();
        }
        if (Objects.equals(result.getCode(), UDA_ALGORITHM_INVALID_CODE)) {
            if (log.isDebugEnabled()) {
                log.debug("UDA部分算法授权已失效，原因 ==> {}", result.getData().getAuthErrorList());
            }
            throw new AIRecFunctionExpiredException(JsonUtils.writeJson(result.getData().getAuthErrorList()));
        }
        throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_CREATE_AI_ANALYSIS_TASK.getContent()));
//        throw new BizException("创建AI分析任务失败，请稍后重试！");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String createAutoAIAnalysisTask(AutoAIRecognitionTaskInDTO autoAiTask) {
        // 实现AI分析任务存储逻辑
        AIAnalysisTaskEntity uosAiTask = new AIAnalysisTaskEntity();
        uosAiTask.setAiTaskType(autoAiTask.getAiTaskType());
        uosAiTask.setAiTaskName(autoAiTask.getAiTaskName());
        uosAiTask.setTaskId(BizIdUtils.snowflakeIdStr());
        uosAiTask.setCenterBaseId(Long.parseLong(autoAiTask.getBaseDataId()));
        uosAiTask.setFlightTaskId(Long.parseLong(autoAiTask.getFlightTaskId()));
        uosAiTask.setTagName(autoAiTask.getFlightTaskTag());
        uosAiTask.setTaskName(autoAiTask.getTaskName());
        uosAiTask.setState(AIAnalysisTaskStateEnum.UNTREATED.getType());
        uosAiTask.setAuto(true);
        uosAiTask.setVersion(0);
        uosAiTask.setOrgCode(autoAiTask.getOrgCode());
        uosAiTask.setTotalImageCount(autoAiTask.getPhotoInfos().size());
        List<AIAnalysisPhotoEntity> photoEntities = autoAiTask.getPhotoInfos()
                .stream()
                .map(r -> {
                    AIAnalysisPhotoEntity photoEntity = new AIAnalysisPhotoEntity();
                    photoEntity.setTaskId(uosAiTask.getTaskId());
                    photoEntity.setRecordId(BizIdUtils.snowflakeIdStr());
                    photoEntity.setCenterDetailId(Long.parseLong(r.getDetailId()));
                    photoEntity.setPhotoName(r.getPhotoName());
                    photoEntity.setPhotoPath(r.getPhotoPath());
                    photoEntity.setVersion(0);
                    photoEntity.setReason(r.getFailedReason());
                    photoEntity.setState(AIAnalysisPhotoStateEnum.UNTREATED.getType());
                    if(StringUtils.hasText(r.getFailedReason())){
                        photoEntity.setState(AIAnalysisPhotoStateEnum.FAILED.getType());
                    }
                    return photoEntity;
                })
                .collect(Collectors.toList());
        aiAnalysisTaskManager.insertAnalysisTask(uosAiTask, photoEntities);
        return uosAiTask.getTaskId();
    }

    @Override
    public void execAutoAIAnalysisTask(WaitingTaskElement waitingTask) {

        String taskId = waitingTask.getTaskId();
        log.info("#AIAnalysisServiceImpl.execAutoAIAnalysisTask# taskId={}", taskId);
        // 查询AI识别任务信息
        // a. 分析任务是否存在、是否系统自动任务、状态是否为【未处理】
        AIAnalysisTaskEntity taskEntity = aiAnalysisTaskManager.findAIAnalysisTaskEntity(taskId);
        if(Objects.isNull(taskEntity)){
            log.warn("未能自动执行AI分析任务：AI分析任务[{}]不存在", taskId);
            applicationEventPublisher.publishEvent(new AIAnalysisTaskHandleCompletedEvent(this));
            return;
        }
        if(!Objects.equals(taskEntity.getAuto(), Boolean.TRUE)){
            log.warn("未能自动执行AI分析任务：AI分析任务[{}]非系统自动创建", taskId);
            applicationEventPublisher.publishEvent(new AIAnalysisTaskHandleCompletedEvent(this));
            return;
        }
        if(!AIAnalysisTaskStateEnum.UNTREATED.matchEquals(taskEntity.getState())){
            log.warn("未能自动执行AI分析任务：AI分析任务[{}]状态[{}]非法", taskId, taskEntity.getState());
            applicationEventPublisher.publishEvent(new AIAnalysisTaskHandleCompletedEvent(this));
            return;
        }
        AnalysisConfig analysisConfig = geoaiUosProperties.getAnalysis();
        // 查询自动识别任务待识别图片列表
        List<AIAnalysisPhotoEntity> analysisPhotos = aiAnalysisPhotoManager.getAnalysisPhotos(taskId);
        log.info("#AIAnalysisServiceImpl.execAutoAIAnalysisTask# taskId={}, analysisPhotos={}", taskId, analysisPhotos);
        List<UdaAnalysisTaskInDO.TaskPic> taskPics = analysisPhotos.stream()
                .filter(r -> AIAnalysisPhotoStateEnum.UNTREATED.matchEquals(r.getState()))
                .map(r -> {
                    UdaAnalysisTaskInDO.TaskPic taskPic = new UdaAnalysisTaskInDO.TaskPic();
                    taskPic.setPicId(r.getRecordId());
                    taskPic.setPicUrl(analysisConfig.getAiPicAccessHost() + r.getPhotoPath());
                    return taskPic;
                })
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(taskPics)){
            log.warn("未能自动执行AI分析任务：AI分析任务[{}]未检索到合法识别图片数据", taskId);
            applicationEventPublisher.publishEvent(new AIAnalysisTaskHandleCompletedEvent(this));
            return;
        }
        // 查询单位下已配置的AI识别功能列表
        List<String> functionIds = getOrgAvailableFunctionIds(waitingTask.getOrgCode(), taskEntity.getAiTaskType());
        if(CollectionUtils.isEmpty(functionIds)){
            log.error("未能自动执行AI分析任务：单位[{}]授权识别功能列表为空", waitingTask.getOrgCode());
            // 自动识别失败，变电站台账管理内未设置任意识别功能
            handleAutoExecuteTaskFailed(waitingTask.getOrgCode(), taskEntity, MessageEnum.GEOAI_UOS_ORG_ALGORITHM_FUNCTION_UNSET.getContent());
            return;
        }
        UdaAnalysisTaskInDO udaAnalysisTask = new UdaAnalysisTaskInDO();
        udaAnalysisTask.setOrgCode(waitingTask.getOrgCode());
        udaAnalysisTask.setTaskPicList(taskPics);
        udaAnalysisTask.setStorageIdList(functionIds);
        // 调用UDA服务接口-创建并执行分析任务
        Result<UdaAlTaskResponseOutDO> result;
        try {
            result = udaAnalysisServiceClient.createAndExecTask(udaAnalysisTask);
            log.info("#AIAnalysisServiceImpl.execAutoAIAnalysisTask# taskId={}, result={}", taskId, result);
        }catch (Exception e){
            log.error("算法创建任务接口调用失败", e);
            result = Result.error("创建AI分析任务失败，算法服务不可用");
        }
        if(Objects.equals(result.getCode(), UDA_ALGORITHM_UNAUTHORIZED_CODE)){
            log.error("自动执行AI分析任务失败：单位[{}]算法未授权", waitingTask.getOrgCode());
            // 自动识别失败，单位未存在算法授权
            handleAutoExecuteTaskFailed(waitingTask.getOrgCode(), taskEntity, "自动识别失败，单位未存在算法授权");
            return;
        }
        if(Objects.equals(result.getCode(), UDA_ALGORITHM_INVALID_CODE)){
            log.error("自动执行AI分析任务失败：单位[{}]部分算法授权已失效 ==> {}", waitingTask.getOrgCode(), result.getData().getAuthErrorList());
            // 自动识别失败，存在识别功能授权条件不满足使用
            handleAutoExecuteTaskFailed(waitingTask.getOrgCode(), taskEntity, "自动识别失败，存在识别功能授权条件不满足使用");
            return;
        }
        if(!result.isOk()){
            log.error("自动执行AI分析任务失败：UDA服务异常 ==> [{}:{}]", result.getCode(), result.getMsg());
            // 更新任务信息，发布自动任务创建失败事件
            // 自动识别失败，算法服务调用异常
            handleAutoExecuteTaskFailed(waitingTask.getOrgCode(), taskEntity, "自动识别失败，算法服务调用异常");
            return;
        }
        // 事务处理：更新AI识别任务信息
        AIAnalysisTaskEntity taskUpdate = new AIAnalysisTaskEntity();
        taskUpdate.setId(taskEntity.getId());
        taskUpdate.setTaskId(taskEntity.getTaskId());
        taskUpdate.setAiTaskId(result.getData().getTaskId());
        taskUpdate.setState(AIAnalysisTaskStateEnum.QUEUING.getType());
        boolean success = innerTransactionDelegator.updateAutoAIAnalysisTask(taskUpdate);
        log.info("#AIAnalysisServiceImpl.execAutoAIAnalysisTask# taskId={}, success={}", taskId, success);
        if(success){
            // 发送AI超时任务消息
            sendTaskTimeoutCheckMessage(taskEntity.getTaskId());
            taskEntity.setAiTaskId(result.getData().getTaskId());
            taskEntity.setState(AIAnalysisTaskStateEnum.QUEUING.getType());
            IAIAnalysisTask aiAnalysisTask = getOrCreateAIAnalysisTask(taskEntity);
            asyncNotifyClientRefreshProcessing(aiAnalysisTask.getTaskProcess(), true);
            log.info("#AIAnalysisServiceImpl.execAutoAIAnalysisTask# taskId={}, end......", taskId);
        }
    }

    @Override
    public void pauseAIRecognitionTask(String taskId) {
        // 业务参数校验
        // a. 分析任务是否存在，状态是否为【识别中】
        AIAnalysisTaskEntity taskEntity = aiAnalysisTaskManager.findAIAnalysisTaskEntity(taskId);
        if (Objects.isNull(taskEntity)) {
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AI_ANALYSIS_TASK_NOT_EXIST.getContent()));
//            throw new BizParameterException("AI分析任务不存在");
        }
        if (!AIAnalysisTaskStateEnum.QUEUING.matchEquals(taskEntity.getState())
                && !AIAnalysisTaskStateEnum.EXECUTING.matchEquals(taskEntity.getState())) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUSPEND_TASK_OPERATION_IS_NOT_ALLOWED.getContent()));
//            throw new BizException("当前状态不允许执行暂停任务操作");
        }
        Result<Void> result;
        try {
            result = udaAnalysisServiceClient.pauseTask(taskEntity.getAiTaskId());
        } catch (Exception e) {
            log.error("暂停AI分析任务失败", e);
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SUSPEND_AI_ANALYSIS_TASK.getContent()));
//            throw new BizException("暂停AI分析任务失败，请稍后重试！");
        }
        // 暂停任务成功
        if (result.isOk()) {
            try {
                this.getOrCreateAIAnalysisTask(taskEntity)
                    .paused(process -> {
                        AIAnalysisTaskEntity updated = new AIAnalysisTaskEntity();
                        updated.setId(taskEntity.getId());
                        updated.setState(AIAnalysisTaskStateEnum.PAUSED.getType());
                        updated.setCostTime((int) (process.getExecutionTime() / 1000));
                        updated.setTaskId(taskId);
                        boolean success = innerTransactionDelegator.updateAIAnalysisTask(updated);
                        if (success) {
                            asyncNotifyClientRefreshProcessing(process);
                        }
                    });
                return;
            } catch (Exception e) {
                // 发生概率 < 买彩票中500w
                log.error("暂停任务失败", e);
                throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AI_ANALYSIS_TASK_DATA_CONSISTENCY_EXCEPTION.getContent()));
//                throw new BizException("AI分析任务数据一致性异常，请联系管理员！");
            }
        }
        log.error("AI分析任务暂停异常 ==> {}", result.getMsg());
        throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AI_ANALYSIS_TASK_PAUSE_EXCEPTION.getContent()));
//        throw new BizException("AI分析任务暂停异常");
    }

    @Override
    public void resumeAIRecognitionTask(String taskId) {
        // 业务参数校验
        // a. 分析任务是否存在，状态是否为【已暂停】
        AIAnalysisTaskEntity taskEntity = aiAnalysisTaskManager.findAIAnalysisTaskEntity(taskId);
        if (Objects.isNull(taskEntity)) {
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AI_ANALYSIS_TASK_NOT_EXIST.getContent()));
//            throw new BizParameterException("AI分析任务不存在");
        }
        if (!AIAnalysisTaskStateEnum.PAUSED.matchEquals(taskEntity.getState())) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RESUME_TASK_OPERATION_IS_NOT_ALLOWED.getContent()));
//            throw new BizException("当前状态不允许执行继续任务操作");
        }
        Result<Void> result;
        try {
            result = udaAnalysisServiceClient.resumeTask(taskEntity.getAiTaskId());
        } catch (Exception e) {
            log.error("继续AI分析任务失败", e);
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_RESUME_AI_ANALYSIS_TASK.getContent()));
//            throw new BizException("继续AI分析任务失败，请稍后重试！");
        }
        // 继续任务成功
        if (result.isOk()) {
            try {
                this.getOrCreateAIAnalysisTask(taskEntity)
                    .resume(process -> {
                        AIAnalysisTaskEntity updated = new AIAnalysisTaskEntity();
                        updated.setId(taskEntity.getId());
                        updated.setState(AIAnalysisTaskStateEnum.QUEUING.getType());
                        updated.setTaskId(taskId);
                        boolean success = innerTransactionDelegator.updateAIAnalysisTask(updated);
                        if (success) {
                            asyncNotifyClientRefreshProcessing(process);
                        }
                    });
                return;
            } catch (Exception e) {
                // 发生概率 < 买彩票中500w
                log.error("继续任务失败", e);
                throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AI_ANALYSIS_TASK_DATA_CONSISTENCY_EXCEPTION.getContent()));
//                throw new BizException("AI分析任务数据一致性异常，请联系管理员！");
            }
        }
        log.error("AI分析任务继续失败 ==> {}", result);
        throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AI_ANALYSIS_TASK_RESUME_EXCEPTION.getContent()));
//        throw new BizException(String.format("AI分析任务继续失败[%s]", result.getMsg()));
    }

    @Override
    public void stopAIRecognitionTask(String taskId) {
        // 业务参数校验
        // a. 分析任务是否存在，状态是否为【执行中】、【已暂停】
        AIAnalysisTaskEntity taskEntity = aiAnalysisTaskManager.findAIAnalysisTaskEntity(taskId);
        if (Objects.isNull(taskEntity)) {
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AI_ANALYSIS_TASK_NOT_EXIST.getContent()));
//            throw new BizParameterException("AI分析任务不存在");
        }
        if (!AIAnalysisTaskStateEnum.PAUSED.matchEquals(taskEntity.getState())
                && !AIAnalysisTaskStateEnum.QUEUING.matchEquals(taskEntity.getState())
                && !AIAnalysisTaskStateEnum.EXECUTING.matchEquals(taskEntity.getState())) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TERMINATE_TASK_OPERATION_IS_NOT_ALLOWED.getContent()));
//            throw new BizException("当前状态不允许执行终止任务操作");
        }
        Result<Void> result;
        try {
            result = udaAnalysisServiceClient.pauseTask(taskEntity.getAiTaskId());
        } catch (Exception e) {
            log.error("终止AI分析任务失败", e);
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_TERMINATE_AI_ANALYSIS_TASK.getContent()));
//            throw new BizException("终止AI分析任务失败，请稍后重试！");
        }
        // 终止任务成功
        if (result.isOk()) {
            try {
                this.getOrCreateAIAnalysisTask(taskEntity)
                    .terminated(process -> {
                        AIAnalysisTaskEntity updated = new AIAnalysisTaskEntity();
                        updated.setId(taskEntity.getId());
                        updated.setAiTaskType(taskEntity.getAiTaskType());
                        updated.setState(AIAnalysisTaskStateEnum.TERMINATED.getType());
                        updated.setCostTime((int) (process.getExecutionTime() / 1000));
                        updated.setTaskId(taskId);
                        boolean success = innerTransactionDelegator.updateAIAnalysisTask(updated);

                        if (success) {
                            asyncNotifyClientRefreshProcessing(process);
                        }
                    });
                return;
            } catch (Exception e) {
                // 发生概率 < 买彩票中500w
                log.error("终止任务失败", e);
                throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AI_ANALYSIS_TASK_DATA_CONSISTENCY_EXCEPTION.getContent()));
//                throw new BizException("AI分析任务数据一致性异常，请联系管理员！");
            }
        }
        log.error("AI分析任务终止失败 ==> {}", result);
        throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AI_ANALYSIS_TASK_TERMINATE_EXCEPTION.getContent()));
//        throw new BizException(String.format("AI分析任务终止失败[%s]", result.getMsg()));
    }

    @Override
    public void handlePhotoAnalysisResults(AIAnalysisPicResultDataOutDTO aiData) {
        if (Objects.isNull(aiData)) {
            return;
        }
        // 查询任务信息
        AIAnalysisTaskEntity task = getAiAnalysisTaskEntity(aiData.getTaskId());
        for (int i = 0; i < 2 && Objects.isNull(task); i++) {
            ThreadUtil.sleep(5 * 1000);
            task = getAiAnalysisTaskEntity(aiData.getTaskId());
        }
        if (Objects.isNull(task)) {
            return;
        }
        // 批量查询图片数据：识别中、已暂停
        AIAnalysisPhotoEntity photo = getAiAnalysisPhotoEntity(aiData);
        if (Objects.isNull(photo)) {
            return;
        }
        // 获取内存分析任务进度
        this.getOrCreateAIAnalysisTask(task)
                .refreshing(aiData, process -> {
                    // AI识别进度持久化
                    boolean success = false;
                    int count = 0;
                    int executionTimes = (int) (process.getExecutionTime() / 1000);
                    if (aiData.isResult()) {
                        while (!success) {
                            count++;
                            success = innerTransactionDelegator.increaseSuccessImage(aiData.getPicId(), executionTimes, aiData.getMarkInfos());
                            log.info("尝试第[{}]次更新本地分析任务已识别完成图片[{} ==> true]数据，更新结果：{}", count, aiData.getPicId(), success);
                        }
                    } else {
                        while (!success) {
                            count++;
                            success = innerTransactionDelegator.increaseFailedImage(aiData.getPicId(), executionTimes);
                            log.info("尝试第[{}]次更新本地分析任务已识别完成图片[{} ==> false]数据，更新结果：{}", count, aiData.getPicId(), success);
                        }
                    }
                    // 首次非终次时，才发消息
                    if(process.isStart() && !process.isCompleted()){
                        asyncNotifyClientRefreshProcessing(process);
                    }
                    // 异步通知
                    asyncNotifyClientRefreshProcessing(process, Collections.singletonList(photo.getRecordId()));
                });
    }

    @Override
    public void handleTaskAnalysisResult(String aiTaskId) {
        if (StringUtils.hasText(aiTaskId)) {
            return;
        }
        // 查询任务信息
        AIAnalysisTaskEntity task = getAiAnalysisTaskEntity(aiTaskId);
        if (Objects.isNull(task)) {
            return;
        }
        if (task.getTotalImageCount() - task.getImageSuccessCount() - task.getImageFailedCount() == 0) {
            log.info("AI分析任务[{}]图片未识别完成，忽略结果", task.getTaskId());
            return;
        }
        this.getOrCreateAIAnalysisTask(task)
                .completed(process -> {
                    AIAnalysisTaskEntity update = new AIAnalysisTaskEntity();
                    update.setId(task.getId());
                    update.setState(AIAnalysisTaskStateEnum.COMPLETED.getType());
                    update.setCostTime((int) (process.getExecutionTime() / 1000));
                    aiAnalysisTaskManager.updateAIAnalysisTaskEntity(update);
                    // 异步通知
                    asyncNotifyClientRefreshProcessing(process);
                });
    }

    @Override
    public void loadProcessingTasks(String accountId) {
        if (!StringUtils.hasText(accountId)) {
            return;
        }
        // 减小力度，只对相应账号加锁
        synchronized (accountId.intern()) {
            // 根据账号查询任务列表
            List<AIAnalysisTaskEntity> tasks = aiAnalysisTaskManager.fetchIncompleteTasks(accountId);
            if (CollectionUtils.isEmpty(tasks)) {
                log.info("账号[{}]未查询到有进行中的AI分析任务", accountId);
//                return;
            }
            // 根据账号查询单位列表[兼容单位创建的自动任务]
            List<AIAnalysisTaskEntity> orgTasks = aiAnalysisTaskManager.fetchOrgIncompleteTasks(getAccountOrgCode(accountId));
            if(!CollectionUtils.isEmpty(orgTasks)){
                tasks.addAll(orgTasks);
            }
            for (AIAnalysisTaskEntity task : tasks) {
                log.info("加载账号[{}]未完成的AI分析任务[{}]", accountId, task.getTaskId());
                IAIAnalysisTask analysisTask = getOrCreateAIAnalysisTask(task);
                // 初始化推送
                asyncNotifyClientRefreshProcessing(analysisTask.getTaskProcess());
            }
        }
    }

    private String getAccountOrgCode(String accountId){
        try {
            Result<OrgSimpleOutDO> result = accountServiceClient.getAccountOrgInfo(accountId);
            if(result.isOk() && Objects.nonNull(result.getData())){
                OrgSimpleOutDO data = result.getData();
                return data.getOrgCode();
            }
        }catch (Exception e){
            log.error("获取账号所属单位编码失败", e);
        }
        return null;
    }

    @Override
    public void loadProcessingTasks() {
        List<AIAnalysisTaskEntity> taskEntities = aiAnalysisTaskManager.fetchAIAnalysisTasks(AIAnalysisTaskStateEnum.PAUSED.getType(), AIAnalysisTaskStateEnum.QUEUING.getType(), AIAnalysisTaskStateEnum.EXECUTING.getType());
        if(CollectionUtils.isEmpty(taskEntities)){
            log.info("未检索到有运行中的AI识别任务");
            return;
        }
        for (AIAnalysisTaskEntity taskEntity : taskEntities) {
            log.info("加载未完成的AI分析任务[{}]", taskEntity.getTaskId());
            IAIAnalysisTask analysisTask = getOrCreateAIAnalysisTask(taskEntity);
            // 初始化推送
            asyncNotifyClientRefreshProcessing(analysisTask.getTaskProcess());
        }
    }

    @Override
    public void loadWaitingTasks() {
        List<AIAnalysisTaskEntity> taskEntities = aiAnalysisTaskManager.fetchAIAnalysisTasks(AIAnalysisTaskStateEnum.UNTREATED.getType());
        if(CollectionUtils.isEmpty(taskEntities)){
            log.info("未检索到有等待运行的AI识别任务");
            return;
        }
        List<AIAnalysisTaskEntity> autoTasks = taskEntities.stream()
                .filter(r -> Objects.equals(r.getAuto(), Boolean.TRUE))
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(autoTasks)){
            log.info("未检索到有等待运行的自动AI识别任务");
            return;
        }
        for (AIAnalysisTaskEntity autoTask : autoTasks) {
            WaitingTaskElementSupport waitingTask = WaitingTaskElementSupport.builder()
                    .taskId(autoTask.getTaskId())
                    .orgCode(autoTask.getOrgCode())
                    .build();
            boolean success = SystemAIAnalysisTask.addWaitingTask(waitingTask);
            if(success){
                log.info("添加等待任务成功 ==> {}", waitingTask);
            }
            // 发布事件
            if(success){
                applicationEventPublisher.publishEvent(new SystemAIAnalysisTaskInitializedEvent(this));
            }
        }
    }

    @Override
    public void handleIfAITaskTimeout(String taskId) {
        if (!StringUtils.hasText(taskId)) {
            log.info("AI分析任务ID为空，忽略本次超时任务消息检查");
            return;
        }
        AIAnalysisTaskEntity taskEntity = aiAnalysisTaskManager.findAIAnalysisTaskEntity(taskId);
        if (Objects.isNull(taskEntity)) {
            log.info("AI分析任务[{}]不存在，忽略本次超时任务消息检查", taskId);
            return;
        }
        Optional<AIAnalysisTaskStateEnum> optional = AIAnalysisTaskStateEnum.findMatch(taskEntity.getState());
        if (!optional.isPresent()) {
            log.info("AI分析任务[{}]状态非法 ==> [{}]，忽略本次超时任务消息检查", taskId, taskEntity.getState());
            return;
        }
        AIAnalysisTaskStateEnum taskState = optional.get();
        if (Objects.equals(taskState, AIAnalysisTaskStateEnum.COMPLETED)
                || Objects.equals(taskState, AIAnalysisTaskStateEnum.TERMINATED)
                || Objects.equals(taskState, AIAnalysisTaskStateEnum.PAUSED)) {
            log.info("AI分析任务[{}]状态为[{}]，忽略本次超时任务消息检查", taskId, taskState);
            return;
        }
        this.getOrCreateAIAnalysisTask(taskEntity)
                .completed(process -> {
                    AIAnalysisTaskEntity updateInfo = new AIAnalysisTaskEntity();
                    updateInfo.setId(taskEntity.getId());
                    updateInfo.setState(AIAnalysisTaskStateEnum.COMPLETED.getType());
                    updateInfo.setCostTime((int) process.getExecutionTime() / 1000);
                    updateInfo.setImageFailedCount(taskEntity.getTotalImageCount() - taskEntity.getImageSuccessCount());
                    innerTransactionDelegator.handleTimeoutAITask(taskId, updateInfo);
                    // 异步通知
                    asyncNotifyClientRefreshProcessing(process);
                });
    }

    @Component
    public static class InnerTransactionDelegator {

        @Resource
        private AIAnalysisTaskManager aiAnalysisTaskManager;

        @Resource
        private AIAnalysisPhotoManager aiAnalysisPhotoManager;

        @Resource
        private PowerComponentRuleInfoManager powerComponentRuleInfoManager;

        @Resource
        private PowerMeterDetailService powerMeterDetailService;

        @Resource
        private DataAnalysisDetailService dataAnalysisDetailService;

        @Resource
        private  TopicService topicService;

        @Resource
        private DataAnalysisMarkService dataAnalysisMarkService;

        @Resource
        private GeoaiUosProperties geoaiUosProperties;

        @Resource
        private UploadManager uploadManager;

        @Transactional(rollbackFor = Exception.class)
        public boolean createAnalysisTask(AIAnalysisTaskEntity uosAiTask, Set<AIAnalysisPhotoExtPO> aiAnalysisPhotos) {
            // 保存进度数据
            boolean success = aiAnalysisTaskManager.insertAnalysisTask(uosAiTask, aiAnalysisPhotos);
            if(AIAnalysisTaskTypeEnum.DIALS.matchEquals(uosAiTask.getAiTaskType())){
                // 更新表计详情读数状态为识别中
                List<String> detailIds = aiAnalysisPhotos.stream()
                        // 失败的不更新表计读数状态
                        .filter(r -> !AIAnalysisPhotoStateEnum.FAILED.matchEquals(r.getState()))
                        .map(AIAnalysisPhotoExtPO::getCenterDetailId)
                        .map(Object::toString)
                        .collect(Collectors.toList());
//                powerMeterDetailService.updateMeterDetailReadingState(detailIds, DialReadingTypeEnum.RECOGNIZING.getStatus());
                powerMeterDetailService.init(detailIds);
            }
            return success;
        }

        @Transactional(rollbackFor = Exception.class)
        public boolean increaseSuccessImage(String recordId, int costTimes, List<AIAnalysisPicResultDataOutDTO.MarkInfo> aiMarkInfos) {
            // 更新图片分析进度数据
            AIAnalysisPhotoEntity photoEntity = aiAnalysisPhotoManager.findAIAnalysisPhotoEntity(recordId);
            if (Objects.nonNull(photoEntity)) {
                // 第一次更新任务进度时，需要检测是否排队中，修改整个任务数据状态为识别中
                AIAnalysisTaskEntity dbTask = doUpdateTaskIfQueuing(photoEntity);
                if (doUpdateAIPhotoInfoFailed(photoEntity, true)) {
                    return false;
                }
                if(Objects.isNull(dbTask)){
                    dbTask = aiAnalysisTaskManager.findAIAnalysisTaskEntity(photoEntity.getTaskId());
                }
                // 更新任务分析进度数据
                if (doUpdateAITaskInfoFailed(dbTask, costTimes, true)) {
                    return false;
                }
                // 通用目标识别
                if(AIAnalysisTaskTypeEnum.UNIVERSAL_TARGETS.matchEquals(dbTask.getAiTaskType())){
                    doSaveAIMarkInfos(photoEntity.getCenterDetailId(), aiMarkInfos);
                }
                // 表计读数识别
                else if(AIAnalysisTaskTypeEnum.DIALS.matchEquals(dbTask.getAiTaskType())){
                    List<PowerMeterFlightDetailEntity> meterDetails = powerMeterDetailService.getMeterDetails(Collections.singletonList(photoEntity.getCenterDetailId().toString()));
                    if(!CollectionUtils.isEmpty(meterDetails)){
                        PowerMeterFlightDetailEntity detailEntity = meterDetails.get(0);
                        // 回写表计读数信息
                        MeterReadingDiscernInfoInDTO meterDiscernResult = null;
                        if(StringUtils.hasText(detailEntity.getComponentId())){
                            List<PowerComponentRuleInfoOutDO> componentRuleInfos = powerComponentRuleInfoManager.selectByComponentId(detailEntity.getComponentId());
                            // 算法返回读数信息
                            if(!CollectionUtils.isEmpty(aiMarkInfos)){
                                if(aiMarkInfos.size() == 1){
                                    meterDiscernResult = getMeterDiscernResult(componentRuleInfos, aiMarkInfos.get(0));
                                }else{
                                    meterDiscernResult = getMeterDiscernResult(detailEntity.getComponentName(), componentRuleInfos, aiMarkInfos);
                                }
                            }
                            // 算法未返回读数信息
                            else{
                                meterDiscernResult = new MeterReadingDiscernInfoInDTO();
                                meterDiscernResult.setDeviceState(DialDeviceTypeEnum.ABNORMAL_ALARM.getStatus());
                                meterDiscernResult.setReadingState(DialReadingTypeEnum.NO_READING.getStatus());
                                meterDiscernResult.setAlarmReasonType(DialAlarmReasonTypeEnum.READING_NOT_RECOGNIZED.getType());
                            }
                        }
                        if(Objects.nonNull(meterDiscernResult)){
                            if(StringUtils.hasText(meterDiscernResult.getAlgorithmPicUrl())){
                                String picPath = downloadAlgorithmPicAndUploadToServer(meterDiscernResult.getAlgorithmPicUrl());
                                meterDiscernResult.setDiscernPicPath(picPath);
                            }
                            // 回写读数信息
                            powerMeterDetailService.updateMeterReadingDiscernInfo(detailEntity.getDetailId(), meterDiscernResult);
                        }else{
                            log.info("未查询到部件[{}]读数规则信息", detailEntity.getComponentId());
                        }
                    }
                }
            }
            return true;
        }

        @Transactional(rollbackFor = Exception.class)
        public boolean increaseFailedImage(String recordId, int costTimes) {
            // 更新图片分析进度数据
            AIAnalysisPhotoEntity photoEntity = aiAnalysisPhotoManager.findAIAnalysisPhotoEntity(recordId);
            if (Objects.nonNull(photoEntity)) {
                // 第一次更新任务进度时，需要检测是否排队中，修改整个任务数据状态为识别中
                AIAnalysisTaskEntity dbTask = doUpdateTaskIfQueuing(photoEntity);
                if (doUpdateAIPhotoInfoFailed(photoEntity, false)) {
                    return false;
                }
                if(Objects.isNull(dbTask)){
                    dbTask = aiAnalysisTaskManager.findAIAnalysisTaskEntity(photoEntity.getTaskId());
                }
                // 更新任务分析进度数据
                if (doUpdateAITaskInfoFailed(dbTask, costTimes, false)) {
                    return false;
                }
                // 通用目标识别
                if(AIAnalysisTaskTypeEnum.UNIVERSAL_TARGETS.matchEquals(dbTask.getAiTaskType())){
                    // 更新图片详情数据
                    doUpdateDetailPhotoState(photoEntity.getCenterDetailId());
                }
                // 表计读数识别
                else if(AIAnalysisTaskTypeEnum.DIALS.matchEquals(dbTask.getAiTaskType())){
                    List<PowerMeterFlightDetailEntity> meterDetails = powerMeterDetailService.getMeterDetails(Collections.singletonList(photoEntity.getCenterDetailId().toString()));
                    if(!CollectionUtils.isEmpty(meterDetails)){
                        MeterReadingDiscernInfoInDTO discernInfo = new MeterReadingDiscernInfoInDTO();
                        discernInfo.setReadingState(DialReadingTypeEnum.NO_READING.getStatus());
                        discernInfo.setDeviceState(DialDeviceTypeEnum.ABNORMAL_ALARM.getStatus());
                        discernInfo.setAlarmReasonType(DialAlarmReasonTypeEnum.READING_NOT_RECOGNIZED.getType());
                        powerMeterDetailService.updateMeterReadingDiscernInfo(meterDetails.get(0).getDetailId(), discernInfo);
                    }
                }
            }
            return true;
        }

        @Transactional(rollbackFor = Exception.class)
        public boolean updateAutoAIAnalysisTask(AIAnalysisTaskEntity updateInfo){
            List<Integer> sources = new ArrayList<>();
            AIAnalysisPhotoStateEnum photoState = null;
            DialReadingTypeEnum readingState = null;
            DialDeviceTypeEnum deviceState = null;
            DialAlarmReasonTypeEnum alarmReasonType = null;
            // 任务识别状态：未处理 ==> 排队中；图片识别状态：未处理 ==> 排队中；表计读数状态：未处理 ==> 识别中
            if(AIAnalysisTaskStateEnum.QUEUING.matchEquals(updateInfo.getState())){
                photoState = AIAnalysisPhotoStateEnum.QUEUING;
                readingState = DialReadingTypeEnum.RECOGNIZING;
                sources.add(AIAnalysisPhotoStateEnum.UNTREATED.getType());
            }
            // [自动执行任务失败]：任务识别状态：未处理 ==> 已完成；图片识别状态：未处理 ==> 已完成；表计读数状态：识别中 ==> 无读数
            else if(AIAnalysisTaskStateEnum.COMPLETED.matchEquals(updateInfo.getState())){
                photoState = AIAnalysisPhotoStateEnum.FAILED;
                readingState = DialReadingTypeEnum.NO_READING;
                deviceState = DialDeviceTypeEnum.ABNORMAL_ALARM;
                alarmReasonType = DialAlarmReasonTypeEnum.READING_NOT_RECOGNIZED;
                sources.add(AIAnalysisPhotoStateEnum.UNTREATED.getType());
            }
            if(log.isTraceEnabled()){
                log.trace("photo state sources ==> {}", sources);
                log.trace("photo state target  ==> {}", photoState);
            }
            String taskId = updateInfo.getTaskId();
            // 不允许修改任务ID字段
            updateInfo.setTaskId(null);
            // a. 更新任务信息
            aiAnalysisTaskManager.updateAIAnalysisTaskEntity(updateInfo);
            // b. 更新图片信息
            if(Objects.nonNull(photoState)){
                aiAnalysisPhotoManager.updateAutoAIAnalysisPhoto(photoState.getType(), updateInfo.getAiTaskId(), taskId, sources);
            }
            // c. 更新表计详情读数状态
            if(Objects.nonNull(readingState)){
                // 查询图片关联的详情ID
                List<AIAnalysisPhotoEntity> analysisPhotos = aiAnalysisPhotoManager.getAnalysisPhotos(taskId);
                List<String> detailIds = analysisPhotos.stream()
                        .map(AIAnalysisPhotoEntity::getCenterDetailId)
                        .map(Object::toString)
                        .collect(Collectors.toList());
                // 更新表计详情状态
                MeterReadingDiscernInfoInDTO info = new MeterReadingDiscernInfoInDTO();
                info.setReadingState(readingState.getStatus());
                if(Objects.nonNull(deviceState)){
                    info.setDeviceState(deviceState.getStatus());
                }
                if(Objects.nonNull(alarmReasonType)){
                    info.setAlarmReasonType(alarmReasonType.getType());
                }
                powerMeterDetailService.updateMeterReadingInfo(detailIds, info, null);
            }
            return true;
        }

        @Transactional(rollbackFor = Exception.class)
        public boolean updateAIAnalysisTask(AIAnalysisTaskEntity updateInfo) {
            log.info("#AiAnalysisServiceImpl.updateAIAnalysisTask# updateInfo={}", JSONUtil.toJsonStr(updateInfo));
            List<Integer> sources = new ArrayList<>();
            AIAnalysisPhotoStateEnum photoState = null;
            DialReadingTypeEnum readingState = null;
            DialDeviceTypeEnum deviceState = null;
            DialAlarmReasonTypeEnum alarmReasonType = null;
            // 任务识别状态：排队中|识别中 ==> 已暂停；图片识别状态：排队中|识别中 ==> 已暂停
            if (AIAnalysisTaskStateEnum.PAUSED.matchEquals(updateInfo.getState())) {
                sources.add(AIAnalysisPhotoStateEnum.QUEUING.getType());
                sources.add(AIAnalysisPhotoStateEnum.EXECUTING.getType());
                photoState = AIAnalysisPhotoStateEnum.PAUSED;
            }
            // 任务识别状态：已暂停 ==> 排队中；图片识别状态：已暂停 ==> 排队中
            else if (AIAnalysisTaskStateEnum.QUEUING.matchEquals(updateInfo.getState())) {
                sources.add(AIAnalysisPhotoStateEnum.PAUSED.getType());
                photoState = AIAnalysisPhotoStateEnum.QUEUING;
            }
            // 任务识别状态：已暂停|排队中|识别中 ==> 已终止；图片识别状态：已暂停|排队中|识别中 ==> 已终止
            else if (AIAnalysisTaskStateEnum.TERMINATED.matchEquals(updateInfo.getState())) {
                sources.add(AIAnalysisPhotoStateEnum.PAUSED.getType());
                sources.add(AIAnalysisPhotoStateEnum.QUEUING.getType());
                sources.add(AIAnalysisPhotoStateEnum.EXECUTING.getType());
                photoState = AIAnalysisPhotoStateEnum.FAILED;
                // 表计相关状态：识别中 ==> 无读数；
                readingState = DialReadingTypeEnum.NO_READING;
                deviceState = DialDeviceTypeEnum.ABNORMAL_ALARM;
                alarmReasonType = DialAlarmReasonTypeEnum.READING_NOT_RECOGNIZED;
            }
            if (log.isTraceEnabled()) {
                log.trace("photo state sources ==> {}", sources);
                log.trace("photo state target  ==> {}", photoState);
            }
            // c. 更新表计读数状态
            Integer aiTaskType = updateInfo.getAiTaskType();
            if(aiTaskType != null && AIAnalysisTaskTypeEnum.DIALS.matchEquals(aiTaskType) && Objects.nonNull(readingState)){
                // 查询图片关联的详情ID
                List<AIAnalysisPhotoEntity> analysisPhotos = aiAnalysisPhotoManager.getAnalysisPhotos(updateInfo.getTaskId());
                List<String> detailIds = analysisPhotos.stream()
                        .map(AIAnalysisPhotoEntity::getCenterDetailId)
                        .map(Object::toString)
                        .collect(Collectors.toList());
                // 更新表计详情状态
                MeterReadingDiscernInfoInDTO info = new MeterReadingDiscernInfoInDTO();
                info.setReadingState(readingState.getStatus());
                info.setDeviceState(deviceState.getStatus());
                info.setAlarmReasonType(alarmReasonType.getType());
                log.info("#AiAnalysisServiceImpl.updateAIAnalysisTask# info={}", JSONUtil.toJsonStr(info));
                log.info("#AiAnalysisServiceImpl.updateAIAnalysisTask# detailIds={}", JSONUtil.toJsonStr(detailIds));
                // 只终止识别中
                powerMeterDetailService.updateMeterReadingInfo(detailIds, info, DialReadingTypeEnum.RECOGNIZING);
            }
            String taskId = updateInfo.getTaskId();
            // 不允许修改任务ID字段
            updateInfo.setTaskId(null);
            updateInfo.setAiTaskType(null);
            // a. 更新任务状态
            log.info("#AiAnalysisServiceImpl.updateAIAnalysisTask# updateInfo={}", JSONUtil.toJsonStr(updateInfo));
            aiAnalysisTaskManager.updateAIAnalysisTaskEntity(updateInfo);
            // 任务分析完成，无需修改图片状态
            // b. 更新图片状态
            if (Objects.nonNull(photoState)) {
                aiAnalysisPhotoManager.updateAIAnalysisPhotoState(photoState.getType(), taskId, sources);
            }
            return true;
        }

        @Transactional(rollbackFor = Exception.class)
        public void handleTimeoutAITask(String taskId, AIAnalysisTaskEntity updateInfo) {
            aiAnalysisTaskManager.updateAIAnalysisTaskEntity(updateInfo);
            List<Integer> states = new ArrayList<>(2);
            states.add(AIAnalysisPhotoStateEnum.EXECUTING.getType());
            states.add(AIAnalysisPhotoStateEnum.PAUSED.getType());
            aiAnalysisPhotoManager.updateAIAnalysisPhotoState(AIAnalysisPhotoStateEnum.FAILED.getType(), taskId, states);
            // 未完成AI识别的图片详情状态改为【无问题】
            List<AIAnalysisPhotoEntity> analysisPhotos = aiAnalysisPhotoManager.getAnalysisPhotos(taskId);
            if (!CollectionUtils.isEmpty(analysisPhotos)) {
                List<Long> centerDetailIds = analysisPhotos.stream()
                        .map(AIAnalysisPhotoEntity::getCenterDetailId)
                        .distinct()
                        .collect(Collectors.toList());
                List<DataAnalysisDetailOutDTO> dataAnalysisDetails = dataAnalysisDetailService.findDataAnalysisDetails(centerDetailIds);
                if (!CollectionUtils.isEmpty(dataAnalysisDetails)) {
                    // 只更新图片状态为【待分析】的数据状态为无问题
                    centerDetailIds = dataAnalysisDetails.stream()
                            .filter(r -> DataAnalysisPicStatusEnum.NEED_ANALYZE.matchEquals(r.getPhotoState()))
                            .map(DataAnalysisDetailOutDTO::getCenterDetailId)
                            .distinct()
                            .collect(Collectors.toList());
                    dataAnalysisDetailService.writeBackPicState(centerDetailIds, DataAnalysisPicStatusEnum.NO_PROBLEM.getType(), null);
                }
            }
        }

        private AIAnalysisTaskEntity doUpdateTaskIfQueuing(AIAnalysisPhotoEntity photoEntity) {
            // 排队中：修改任务识别状态和图片识别状态为执行中
            if(AIAnalysisPhotoStateEnum.QUEUING.matchEquals(photoEntity.getState())){
                log.info("当前任务首次识别到图片数据，更新整个任务状态为【识别中】");
                AIAnalysisTaskEntity dbTask = aiAnalysisTaskManager.findAIAnalysisTaskEntity(photoEntity.getTaskId());
                AIAnalysisTaskEntity update = new AIAnalysisTaskEntity();
                update.setId(dbTask.getId());
                update.setState(AIAnalysisPhotoStateEnum.EXECUTING.getType());
                update.setVersion(dbTask.getVersion() + 1);
                // TODO 是否需要判断更新成功与否？
                aiAnalysisTaskManager.updateAIAnalysisTaskEntity(update);
                aiAnalysisPhotoManager.updateAIAnalysisPhotoState(AIAnalysisPhotoStateEnum.EXECUTING.getType(), photoEntity.getTaskId(), Collections.singletonList(AIAnalysisPhotoStateEnum.QUEUING.getType()));
                dbTask.setState(update.getState());
                dbTask.setVersion(update.getVersion());
                return dbTask;
            }
            return null;
        }

        private boolean doUpdateAIPhotoInfoFailed(AIAnalysisPhotoEntity photoEntity, boolean successImage) {
            AIAnalysisPhotoEntity upe = new AIAnalysisPhotoEntity();
            upe.setId(photoEntity.getId());
            upe.setState(successImage ? AIAnalysisPhotoStateEnum.SUCCEED.getType() : AIAnalysisPhotoStateEnum.FAILED.getType());
            upe.setVersion(photoEntity.getVersion() + 1);
            boolean success = aiAnalysisPhotoManager.updateAIAnalysisPhotoEntity(upe);
            return !success;
        }

        private boolean doUpdateAITaskInfoFailed(AIAnalysisTaskEntity taskEntity, int costTimes, boolean successImage) {
            if(Objects.nonNull(taskEntity)){
                AIAnalysisTaskEntity ute = new AIAnalysisTaskEntity();
                int total = taskEntity.getTotalImageCount();
                int success = taskEntity.getImageSuccessCount();
                int failed = taskEntity.getImageFailedCount();
                ute.setId(taskEntity.getId());
                if (successImage) {
                    success++;
                    ute.setImageSuccessCount(success);
                } else {
                    failed++;
                    ute.setImageFailedCount(failed);
                }
                // 只能粗略估算，取最大值
                ute.setCostTime(Math.max(costTimes, taskEntity.getCostTime()));
                ute.setVersion(taskEntity.getVersion() + 1);
                // 更新任务状态：如果如果数量已达到最大值，则修改任务状态为已结束
                if (total - failed - success <= 0) {
                    log.info("AI分析任务[{}]图片已全部识别完成，设置状态为[{}]", taskEntity.getTaskId(), AIAnalysisTaskStateEnum.COMPLETED);
                    ute.setState(AIAnalysisTaskStateEnum.COMPLETED.getType());
                }
                return !aiAnalysisTaskManager.updateAIAnalysisTaskEntity(ute);
            }
            return false;
        }

        private void doSaveAIMarkInfos(Long centerDetailId, List<AIAnalysisPicResultDataOutDTO.MarkInfo> aiMarkInfos) {
            // 查询图片详情信息
            List<DataAnalysisDetailOutDTO> dataAnalysisDetails = dataAnalysisDetailService.findDataAnalysisDetails(Collections.singletonList(centerDetailId));
            if (CollectionUtils.isEmpty(dataAnalysisDetails)) {
                return;
            }
            DataAnalysisDetailOutDTO dataAnalysisDetailOutDTO = dataAnalysisDetails.get(0);
            // 无AI标注数据
            if (CollectionUtils.isEmpty(aiMarkInfos)) {
                if (DataAnalysisPicStatusEnum.NEED_ANALYZE.matchEquals(dataAnalysisDetailOutDTO.getPhotoState())) {
                    // 图片状态为【待分析】：修改问题类型为【无问题】
                    dataAnalysisDetailService.writeBackPicState(Collections.singletonList(centerDetailId), DataAnalysisPicStatusEnum.NO_PROBLEM.getType(), null);
                }
                return;
            }
            // 插入AI标注数据
            Map<String, AIProblemTypeMappingOutDTO> problemTypeMappings = getUosProblemTypeMappings(aiMarkInfos);
            List<DataAnalysisMarkSaveInDTO> analysisMarkSaveInDTOs = new ArrayList<>();
            for (AIAnalysisPicResultDataOutDTO.MarkInfo aiMarkInfo : aiMarkInfos) {
                if (Objects.nonNull(aiMarkInfo.getX())) {
                    DataAnalysisMarkSaveInDTO analysisMarkSaveInDTO = new DataAnalysisMarkSaveInDTO();
                    analysisMarkSaveInDTO.setPhotoId(dataAnalysisDetailOutDTO.getPhotoId());
                    analysisMarkSaveInDTO.setDetailId(dataAnalysisDetailOutDTO.getCenterDetailId());
                    analysisMarkSaveInDTO.setRecX(aiMarkInfo.getX());
                    analysisMarkSaveInDTO.setRecY(aiMarkInfo.getY());
                    analysisMarkSaveInDTO.setRecWidth(aiMarkInfo.getX1().subtract(aiMarkInfo.getX()));
                    analysisMarkSaveInDTO.setRecHeight(aiMarkInfo.getY1().subtract(aiMarkInfo.getY()));
                    // 原图无缩放标注，默认为 0
                    analysisMarkSaveInDTO.setRelX(BigDecimal.ZERO);
                    analysisMarkSaveInDTO.setRelY(BigDecimal.ZERO);
                    // 裁剪图宽高/原图宽高，AI标注为 1
                    analysisMarkSaveInDTO.setCutWidth(BigDecimal.ONE);
                    analysisMarkSaveInDTO.setCutHeight(BigDecimal.ONE);
                    // AI标注缩放比例为 1
                    analysisMarkSaveInDTO.setPicScale(BigDecimal.ONE);
                    analysisMarkSaveInDTO.setMarkState(DataAnalysisMarkStatusEnum.NOT_CONFIRM.getType());
                    analysisMarkSaveInDTO.setExistMark(true);
                    analysisMarkSaveInDTO.setAiMark(true);
                    analysisMarkSaveInDTO.setTopicLevelId(topicService.getDefaultTopicLevelId());
                    analysisMarkSaveInDTO.setAiProblemName(aiMarkInfo.getProblemTypeName());
                    // 匹配UOS问题类型
                    if (problemTypeMappings.containsKey(aiMarkInfo.getProblemTypeId())) {
                        AIProblemTypeMappingOutDTO problemTypeMapping = problemTypeMappings.get(aiMarkInfo.getProblemTypeId());
                        analysisMarkSaveInDTO.setTopicProblemId(problemTypeMapping.getProblemTypeId());
                        analysisMarkSaveInDTO.setAiProblemName(problemTypeMapping.getProblemTypeName());
                        analysisMarkSaveInDTO.setIndustryType(problemTypeMapping.getIndustryType());
                    }
                    analysisMarkSaveInDTOs.add(analysisMarkSaveInDTO);
                }
            }
            dataAnalysisMarkService.saveBatch(analysisMarkSaveInDTOs);
        }

        private Map<String, AIProblemTypeMappingOutDTO> getUosProblemTypeMappings(List<AIAnalysisPicResultDataOutDTO.MarkInfo> markInfos) {
            Set<String> aiProblemTypeIds = markInfos.stream()
                    .map(AIAnalysisPicResultDataOutDTO.MarkInfo::getProblemTypeId)
                    .filter(StringUtils::hasText)
                    .collect(Collectors.toSet());
            List<AIProblemTypeMappingOutDTO> problemInfos = topicService.fetchProblemInfos(aiProblemTypeIds);
            if (!CollectionUtils.isEmpty(problemInfos)) {
                return problemInfos.stream()
                        .collect(Collectors.toMap(AIProblemTypeMappingOutDTO::getAiProblemTypeId, r -> r));
            }
            return Collections.emptyMap();
        }

        private void doUpdateDetailPhotoState(Long centerDetailId) {
            List<DataAnalysisDetailOutDTO> dataAnalysisDetails = dataAnalysisDetailService.findDataAnalysisDetails(Collections.singletonList(centerDetailId));
            if (!CollectionUtils.isEmpty(dataAnalysisDetails)) {
                DataAnalysisDetailOutDTO detail = dataAnalysisDetails.get(0);
                if (DataAnalysisPicStatusEnum.NEED_ANALYZE.matchEquals(detail.getPhotoState())) {
                    // 图片状态为【待分析】：修改问题类型为【无问题】
                    dataAnalysisDetailService.writeBackPicState(Collections.singletonList(centerDetailId), DataAnalysisPicStatusEnum.NO_PROBLEM.getType(), null);
                }
            }
        }

        private MeterReadingDiscernInfoInDTO getMeterDiscernResult(String componentName,
                                                                   List<PowerComponentRuleInfoOutDO> componentRuleInfos,
                                                                   Collection<AIAnalysisPicResultDataOutDTO.MarkInfo> markInfos){
            // b.算法侧返回的识别结果为多个问题类型时，优先取与部件名称精确匹配的问题类型的读取结果按照规则a获取显示，
            // 未能精确匹配时，则以算法返回的问题类型顺序，依次按照规则a获取读数显示。
            MeterReadingDiscernInfoInDTO result = new MeterReadingDiscernInfoInDTO();

            boolean debug = log.isDebugEnabled();
            if(debug){
                log.debug("表计图片关联部件名称：{}", componentName);
            }

            if(StringUtils.hasText(componentName)){
                Optional<AIAnalysisPicResultDataOutDTO.MarkInfo> first = markInfos.stream().filter(r -> Objects.equals(r.getProblemTypeName(), componentName)).findFirst();
                if(first.isPresent()){
                    return getMeterDiscernResult(componentRuleInfos, first.get());
                }
            }

            if(debug){
                log.debug("表计图片关联部件规则：{}", componentRuleInfos);
            }
            // 未定义部件规则
            if(CollectionUtils.isEmpty(componentRuleInfos)){
                result.setReadingState(DialReadingTypeEnum.NO_READING.getStatus());
                result.setDeviceState(DialDeviceTypeEnum.ABNORMAL_ALARM.getStatus());
                result.setAlarmReasonType(DialAlarmReasonTypeEnum.READING_ITEM_NOT_SET.getType());
                return result;
            }

            // 遍历匹配取最优值：配置读数项跟算法返回读数项一致的首个识别结果
            for (AIAnalysisPicResultDataOutDTO.MarkInfo markInfo : markInfos) {
                // 无读数信息
                String readingValues = markInfo.getReadingValues();
                if(!StringUtils.hasText(readingValues)){
                    if(debug){
                        log.debug("算法返回表计信息【无读数信息】，尝试解析下一个识别问题类型数据 ==> {}", markInfo);
                    }
                    continue;
                }
                // 读数项数量不匹配
                String[] values = readingValues.split(SymbolConstants.SEMICOLON);
                if(componentRuleInfos.size() != values.length){
                    if(debug){
                        log.debug("算法返回表计信息【读数项数量不匹配】，尝试解析下一个识别问题类型数据 ==> {}", markInfo);
                    }
                    continue;
                }
                // 读数项数量一致
                return matchDiscernResult(componentRuleInfos, values, result);
            }

            if(debug){
                log.debug("算法返回表计信息【读数项均未匹配】");
            }

            //均未匹配上
            result.setReadingState(DialReadingTypeEnum.NO_READING.getStatus());
            result.setDeviceState(DialDeviceTypeEnum.ABNORMAL_ALARM.getStatus());
            result.setAlarmReasonType(DialAlarmReasonTypeEnum.ABNORMAL_READING.getType());

            return result;
        }

        private MeterReadingDiscernInfoInDTO getMeterDiscernResult(List<PowerComponentRuleInfoOutDO> ruleInfos,
                                                                   AIAnalysisPicResultDataOutDTO.MarkInfo markInfo){
            // a. 算法侧返回的识别结果为单个问题类型，按照识别图片对应的部件设置的读数规则进行顺序匹配，
            // 如果所识别读数数量不满足读数规则项数量则不获取AI的读数值（即部件设置了2个读数规则项，但返回读数项≠2）；
            MeterReadingDiscernInfoInDTO result = new MeterReadingDiscernInfoInDTO();

            // 未定义部件规则
            if(CollectionUtils.isEmpty(ruleInfos)){
                result.setReadingState(DialReadingTypeEnum.NO_READING.getStatus());
                result.setDeviceState(DialDeviceTypeEnum.ABNORMAL_ALARM.getStatus());
                result.setAlarmReasonType(DialAlarmReasonTypeEnum.READING_ITEM_NOT_SET.getType());
                return result;
            }

            // 无读数信息
            String readingValues = markInfo.getReadingValues();
            if(!StringUtils.hasText(readingValues)){
                result.setReadingState(DialReadingTypeEnum.NO_READING.getStatus());
                result.setDeviceState(DialDeviceTypeEnum.ABNORMAL_ALARM.getStatus());
                result.setAlarmReasonType(DialAlarmReasonTypeEnum.READING_NOT_RECOGNIZED.getType());
                return result;
            }

            // 读数项数量不匹配
            String[] values = readingValues.split(SymbolConstants.COMMA);
            if(ruleInfos.size() != values.length){
                result.setReadingState(DialReadingTypeEnum.NO_READING.getStatus());
                result.setDeviceState(DialDeviceTypeEnum.ABNORMAL_ALARM.getStatus());
                result.setAlarmReasonType(DialAlarmReasonTypeEnum.READING_NOT_RECOGNIZED.getType());
                return result;
            }

            if(StringUtils.hasText(markInfo.getScreenshotUrl())){
                result.setAlgorithmPicUrl(markInfo.getScreenshotUrl());
            }

            // 读数项数量一致
            return matchDiscernResult(ruleInfos, values, result);
        }

        private MeterReadingDiscernInfoInDTO matchDiscernResult(List<PowerComponentRuleInfoOutDO> ruleInfos,
                                                                String[] values, MeterReadingDiscernInfoInDTO result) {
            result.setReadingState(DialReadingTypeEnum.WITH_READING.getStatus());
            result.setDeviceState(DialDeviceTypeEnum.NORMAL.getStatus());
            List<MeterReadingDiscernInfoInDTO.MeterReadingValue> readingValueEntities = new ArrayList<>(ruleInfos.size());
            result.setReadingValues(readingValueEntities);
            for (int i = 0; i < values.length; i++) {
                PowerComponentRuleInfoOutDO ruleInfo = ruleInfos.get(i);
                MeterReadingDiscernInfoInDTO.MeterReadingValue readingValueEntity = new MeterReadingDiscernInfoInDTO.MeterReadingValue();
                readingValueEntity.setReadingRuleId(ruleInfo.getComponentRuleId());
                readingValueEntity.setReadingRuleName(ruleInfo.getComponentRuleName());
                String value = values[i];
                BigDecimal aiValue = parseReadingValue(values[i]);
                readingValueEntity.setReadingValue(Objects.nonNull(aiValue) ? aiValue.toString() : value);
                readingValueEntity.setValid(true);
                readingValueEntities.add(readingValueEntity);
                // 需要告警判断
                if(Objects.equals(ruleInfo.getAlarmStatus(), 1)){
                    // 读数值解析异常
                    if(Objects.isNull(aiValue)){
                        readingValueEntity.setValid(false);
                        readingValueEntity.setRemark("读数值解析异常");
                        result.setDeviceState(DialDeviceTypeEnum.DEFECT_ALARM.getStatus());
                        result.setAlarmReasonType(DialAlarmReasonTypeEnum.READING_NOT_RECOGNIZED.getType());
                    }
                    // 读数值不满足规则范围
                    else if(aiValue.compareTo(ruleInfo.getAlarmMin()) < 0 || aiValue.compareTo(ruleInfo.getAlarmMax()) > 0){
                        readingValueEntity.setValid(false);
                        readingValueEntity.setRemark("读数值不满足规则范围");
                        result.setDeviceState(DialDeviceTypeEnum.DEFECT_ALARM.getStatus());
                        result.setAlarmReasonType(DialAlarmReasonTypeEnum.ABNORMAL_READING.getType());
                    }
                }
            }
            return result;
        }

        private BigDecimal parseReadingValue(String value) {
            BigDecimal aiValue = null;
            try {
                aiValue = new BigDecimal(value).setScale(3, RoundingMode.HALF_UP);
            }catch (NumberFormatException e){
                log.warn("算法返回表计数据解析失败 ==> {}", e.getMessage());
            }
            return aiValue;
        }

        private String downloadAlgorithmPicAndUploadToServer(String algorithmPicUrl) {
//            String picPath = String.format("%s%s.%s", UploadTypeEnum.MINIO_COMMON_PICTURE.getPath(), BizIdUtils.snowflakeIdStr(), StringUtils.getFilenameExtension(algorithmPicUrl));
            try {
                log.info("下载算法识别图片[{}]", algorithmPicUrl);
                UrlResource resource = new UrlResource(algorithmPicUrl);
                FastByteArrayOutputStream byteArrayOutputStream = IoUtil.read(resource.getInputStream());
                byte[] byteArray = byteArrayOutputStream.toByteArray();
//                String fileType = MinIoUnit.getFileType(new ByteArrayInputStream(byteArray));
//                boolean success = MinIoUnit.putObject(picPath, new ByteArrayInputStream(byteArray), fileType);
                try(ByteArrayInputStream bis = new ByteArrayInputStream(byteArray)){
                    CommonFileInDO commonFileInDO = new CommonFileInDO();
                    commonFileInDO.setFileName(BizIdUtils.snowflakeIdStr() + SymbolConstants.POINT + StringUtils.getFilenameExtension(algorithmPicUrl));
                    commonFileInDO.setInputStream(bis);
                    Optional<FileStorageOutDO> optional = uploadManager.uploadFile(commonFileInDO);
                    if(optional.isPresent()){
                        String storagePath = optional.get().getStoragePath() + SymbolConstants.SLASH_LEFT + optional.get().getFilename();
                        log.info("上传算法识别图片至文件服务器【{}】成功", storagePath);
                        return storagePath;
                    }
                }
                // 拼接上origin
            } catch (Exception e) {
                log.error("下载算法识别表计图片失败", e);
            }
            return null;
        }

    }

    private void sendTaskTimeoutCheckMessage(String taskId) {
        AnalysisConfig analysisConfig = geoaiUosProperties.getAnalysis();
        long deliveryTimestamp = System.currentTimeMillis() + analysisConfig.getAiTaskTimeout().toMillis();
        MessageId messageId = rocketMQTemplate.sendAndReceive(analysisConfig.getAiTaskTimeoutCheckTopic(), taskId, deliveryTimestamp);
        log.info("AI分析任务[{}]超时检查消息发送成功 ==> {}", taskId, messageId);
    }

    private void handleAutoExecuteTaskFailed(String orgCode, AIAnalysisTaskEntity taskEntity, String message) {
        AIAnalysisTaskEntity updateInfo  = new AIAnalysisTaskEntity();
        updateInfo.setId(taskEntity.getId());
        updateInfo.setTaskId(taskEntity.getTaskId());
        updateInfo.setState(AIAnalysisTaskStateEnum.COMPLETED.getType());
        updateInfo.setImageFailedCount(taskEntity.getImageFailedCount());
        innerTransactionDelegator.updateAutoAIAnalysisTask(updateInfo);
        SystemAIAnalysisTaskFinishedEvent.EventInfo eventInfo = new SystemAIAnalysisTaskFinishedEvent.EventInfo();
        eventInfo.setOrgCode(orgCode);
        eventInfo.setAiTaskId(taskEntity.getTaskId());
        eventInfo.setAiTaskType(taskEntity.getAiTaskType());
        eventInfo.setAiTaskName(taskEntity.getAiTaskName());
        eventInfo.setAiTaskState(false);
        eventInfo.setCenterBaseId(taskEntity.getCenterBaseId().toString());
        eventInfo.setMessage(message);
        applicationEventPublisher.publishEvent(new SystemAIAnalysisTaskFinishedEvent(this, eventInfo));
        applicationEventPublisher.publishEvent(new AIAnalysisTaskHandleCompletedEvent(this));
    }

    private List<String> getOrgAvailableFunctionIds(String orgCode, Integer aiTaskType) {
        Optional<RoleIdenValueEnum> optional = AIAnalysisTaskTypeEnum.findMatch(aiTaskType).map(AIAnalysisTaskTypeEnum::covert);
        if(!optional.isPresent()){
            throw new BizException(MessageEnum.GEOAI_UOS_AI_ANALYSIS_TASK_TYPE_ERROR.getContent());
        }
        List<PowerDiscernFunSettingInfosOutDO> functions = powerDataManager.queryOrgFunctionSettings(orgCode, Integer.toString(optional.get().getIdenValue()));
        if(!CollectionUtils.isEmpty(functions)){
            return functions.stream()
                    .map(PowerDiscernFunSettingInfosOutDO::getDiscernFunId)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }


    private void asyncNotifyClientRefreshProcessing(IAITaskProcess process) {
        asyncNotifyClientRefreshProcessing(process, false);
    }

    private void asyncNotifyClientRefreshProcessing(IAITaskProcess process, Collection<String> photoRecordIds) {
        AIAnalysisTaskDataOutDTO latestInfo = buildAIAnalysisTaskDataLatestInfo(process, photoRecordIds);
        asyncNotifyClientRefreshProcessing(process, latestInfo);
    }

    private void asyncNotifyClientRefreshProcessing(IAITaskProcess process, boolean initialized) {
        AIAnalysisTaskDataOutDTO latestInfo = buildAIAnalysisTaskDataLatestInfo(process, null);
        latestInfo.setInitialized(initialized);
        asyncNotifyClientRefreshProcessing(process, latestInfo);
    }

    private void asyncNotifyClientRefreshProcessing(IAITaskProcess process, AIAnalysisTaskDataOutDTO latestInfo) {
        applicationEventPublisher.publishEvent(new AIAnalysisDataChangedEvent(this, latestInfo));
        if(process instanceof ISystemAITaskProcess && process.isCompleted()){
            log.info("自动AI识别任务[{}]完成，发布【SystemAIAnalysisTaskFinishedEvent】事件", process.getTaskId());
            ISystemAITaskProcess systemAITaskProcess = (ISystemAITaskProcess) process;
            SystemAIAnalysisTaskFinishedEvent.EventInfo eventInfo = new SystemAIAnalysisTaskFinishedEvent.EventInfo();
            eventInfo.setOrgCode(systemAITaskProcess.getOrgCode());
            eventInfo.setAiTaskId(systemAITaskProcess.getTaskId());
            eventInfo.setAiTaskType(systemAITaskProcess.getTaskType());
            eventInfo.setAiTaskName(systemAITaskProcess.getTaskName());
            eventInfo.setAiTaskState(true);
            eventInfo.setCenterBaseId(systemAITaskProcess.getDataId());
            eventInfo.setMessage("AI识别已完成");
            applicationEventPublisher.publishEvent(new SystemAIAnalysisTaskFinishedEvent(this, eventInfo));
        }
    }

    private AIAnalysisPhotoEntity getAiAnalysisPhotoEntity(AIAnalysisPicResultDataOutDTO aiData) {
        List<AIAnalysisPhotoEntity> photos = aiAnalysisPhotoManager.fetchIncompleteAnalysisPhotos(Collections.singletonList(aiData.getPicId()));
        if (CollectionUtils.isEmpty(photos)) {
            log.error("算法推送分析任务识别结果数据异常，未匹配到本地分析任务图片信息");
            return null;
        }
        if (photos.size() > 1) {
            log.error("算法推送分析任务识别结果数据异常，匹配到多个本地分析任务图片信息");
            return null;
        }
        AIAnalysisPhotoEntity photo = photos.get(0);
        if (!Objects.equals(photo.getAiTaskId(), aiData.getTaskId())) {
            log.error("算法推送分析任务识别结果数据异常，任务图片数据无法匹配到本地分析任务信息");
            return null;
        }
        if (!AIAnalysisPhotoStateEnum.QUEUING.matchEquals(photo.getState())
                && !AIAnalysisPhotoStateEnum.EXECUTING.matchEquals(photo.getState())
                && !AIAnalysisPhotoStateEnum.PAUSED.matchEquals(photo.getState())) {
            log.error("本地分析任务图片已结束，忽略推送数据");
            return null;
        }
        return photo;
    }

    private AIAnalysisTaskEntity getAiAnalysisTaskEntity(String aiTaskId) {
        List<AIAnalysisTaskEntity> tasks = aiAnalysisTaskManager.fetchAIAnalysisTasks(Collections.singletonList(aiTaskId));
        if (CollectionUtils.isEmpty(tasks)) {
            log.error("算法推送分析任务识别结果数据异常，未匹配到本地分析任务信息");
            return null;
        }
        if (tasks.size() > 1) {
            log.error("算法推送分析任务识别结果数据异常，匹配到多个本地分析任务信息");
            return null;
        }
        AIAnalysisTaskEntity task = tasks.get(0);
        if (!AIAnalysisTaskStateEnum.QUEUING.matchEquals(task.getState())
                && !AIAnalysisTaskStateEnum.EXECUTING.matchEquals(task.getState())
                && !AIAnalysisTaskStateEnum.PAUSED.matchEquals(task.getState())) {
            log.error("本地分析任务已结束，忽略推送数据");
            return null;
        }
        return task;
    }

    private AIAnalysisTaskEntity buildAIAnalysisTask(final AIRecognitionTaskInDTO data, final DataAnalysisBaseSimpleOutDTO dab,
                                                     final Map<Long, DataAnalysisDetailOutDTO> dadMap) {
        AIAnalysisTaskEntity uosAiTask = new AIAnalysisTaskEntity();
        uosAiTask.setTaskId(BizIdUtils.snowflakeIdStr());
        uosAiTask.setTaskName(dab.getTaskName());
        uosAiTask.setAiTaskType(data.getAiTaskType());
        uosAiTask.setAiTaskName(data.getAiTaskName());
        uosAiTask.setFlightTaskId(dab.getTaskId());
        uosAiTask.setAuto(false);
        uosAiTask.setState(AIAnalysisTaskStateEnum.QUEUING.getType());
        uosAiTask.setCenterBaseId(dab.getCenterBaseId());
        uosAiTask.setFlightTaskId(dab.getTaskId());
        uosAiTask.setTagName(dab.getTagName());
        uosAiTask.setTotalImageCount(dadMap.size());
        uosAiTask.setImageFailedCount(0);
        uosAiTask.setImageSuccessCount(0);
        return uosAiTask;
    }

    private AIAnalysisTaskEntity buildAIAnalysisTask(final AIRecognitionTaskInDTO data, final PowerMeterFlightDataEntity dab,
                                                     final Map<String, PowerMeterFlightDetailEntity> dadMap) {
        AIAnalysisTaskEntity uosAiTask = new AIAnalysisTaskEntity();
        uosAiTask.setTaskId(BizIdUtils.snowflakeIdStr());
        uosAiTask.setTaskName(dab.getTaskName());
        uosAiTask.setAiTaskType(data.getAiTaskType());
        uosAiTask.setAiTaskName(data.getAiTaskName());
        uosAiTask.setFlightTaskId(dab.getTaskId());
        uosAiTask.setAuto(false);
        uosAiTask.setState(AIAnalysisTaskStateEnum.QUEUING.getType());
        uosAiTask.setCenterBaseId(Long.parseLong(dab.getDataId()));
        uosAiTask.setFlightTaskId(dab.getTaskId());
        uosAiTask.setTagName(dab.getTagName());
        uosAiTask.setTotalImageCount(dadMap.size());
        uosAiTask.setImageFailedCount(0);
        uosAiTask.setImageSuccessCount(0);
        return uosAiTask;
    }

    private Set<AIAnalysisPhotoExtPO> buildAIAnalysisPhoto(final AIAnalysisTaskEntity uosAiTask, final Map<Long, DataAnalysisDetailOutDTO> dadMap) {
        Set<Long> invalidCenterDetailIds = fetchInvalidCenterDetailIds(dadMap.keySet());
        Set<AIAnalysisPhotoExtPO> aiAnalysisPhotos = new HashSet<>();
        boolean debugEnabled = log.isDebugEnabled();
        AnalysisConfig analysisConfig = geoaiUosProperties.getAnalysis();
        if (!StringUtils.hasText(analysisConfig.getAiPicAccessHost())) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AI_ANALYSIS_NOT_CONFIGURE_PHOTO_INTRANET_ACCESS_ADDRESS.getContent()));
//            throw new BizException("系统未配置AI识别图片内网访问地址，无法进行识别");
        }
        String picAccessPrefix = analysisConfig.getAiPicAccessHost();

        int failedCount = 0;
        for (Map.Entry<Long, DataAnalysisDetailOutDTO> entry : dadMap.entrySet()) {
            DataAnalysisDetailOutDTO detail = entry.getValue();
            AIAnalysisPhotoExtPO analysisPhoto = new AIAnalysisPhotoExtPO();
            analysisPhoto.setRecordId(BizIdUtils.snowflakeIdStr());
            analysisPhoto.setAiTaskId(uosAiTask.getAiTaskId());
            analysisPhoto.setTaskId(uosAiTask.getTaskId());
            analysisPhoto.setCenterDetailId(detail.getCenterDetailId());
            analysisPhoto.setPhotoName(detail.getPhotoName());
            // 默认正常
            analysisPhoto.setState(AIAnalysisPhotoStateEnum.QUEUING.getType());
            analysisPhoto.setImageUrl(picAccessPrefix + detail.getImagePath());
            analysisPhoto.setPhotoPath(detail.getImagePath());
            analysisPhoto.setVersion(0);
            // a. 剔除非法图片格式[.JPG]的数据（添加至图片识别失败集合）
            String filenameExtension = StringUtils.getFilenameExtension(detail.getImagePath());
            if (Objects.isNull(filenameExtension) || analysisConfig.getAiPicTypes().stream().noneMatch(r -> Objects.equals(filenameExtension.toLowerCase(), r))) {
                if (debugEnabled) {
                    log.debug("成果详情[{}]图片格式[{}]非法", detail.getCenterDetailId(), filenameExtension);
                }
                analysisPhoto.setState(AIAnalysisPhotoStateEnum.FAILED.getType());
                analysisPhoto.setReason("图片格式不符合条件");
            }
            // b. 剔除已核实[有问题、无问题]的数据（添加至图片识别失败集合）
            if (!DataAnalysisPicPushStatusEnum.COMMIT.matchEquals(detail.getPushState())) {
                if (debugEnabled) {
                    log.debug("成果详情[{}]图片推送状态[{}]非法", detail.getCenterDetailId(), detail.getPushState());
                }
                analysisPhoto.setState(AIAnalysisPhotoStateEnum.FAILED.getType());
                analysisPhoto.setReason("图片推送状态非提交态");
            }
            // c. 剔除图片AI识别状态为识别中的数据（添加至图片识别失败集合）
            if (invalidCenterDetailIds.contains(analysisPhoto.getCenterDetailId())) {
                if (debugEnabled) {
                    log.debug("成果详情[{}]图片AI识别状态非法[排队中/识别中/已暂停]", detail.getCenterDetailId());
                }
                analysisPhoto.setState(AIAnalysisPhotoStateEnum.FAILED.getType());
                analysisPhoto.setReason("图片已在AI识别任务队列中");
            }
            if (AIAnalysisPhotoStateEnum.FAILED.matchEquals(analysisPhoto.getState())) {
                failedCount++;
            }
            aiAnalysisPhotos.add(analysisPhoto);
        }
        // 更新失败数量
        uosAiTask.setImageFailedCount(failedCount);
        return aiAnalysisPhotos;
    }

    private Set<AIAnalysisPhotoExtPO> buildAIAnalysisPhoto2(final AIAnalysisTaskEntity uosAiTask, final Map<String, PowerMeterFlightDetailEntity> dadMap) {
        Set<Long> invalidCenterDetailIds = fetchInvalidCenterDetailIds(dadMap.keySet().stream().map(Long::parseLong).collect(Collectors.toSet()));
        Set<AIAnalysisPhotoExtPO> aiAnalysisPhotos = new HashSet<>();
        boolean debugEnabled = log.isDebugEnabled();
        AnalysisConfig analysisConfig = geoaiUosProperties.getAnalysis();
        if (!StringUtils.hasText(analysisConfig.getAiPicAccessHost())) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AI_ANALYSIS_NOT_CONFIGURE_PHOTO_INTRANET_ACCESS_ADDRESS.getContent()));
//            throw new BizException("系统未配置AI识别图片内网访问地址，无法进行识别");
        }
        String picAccessPrefix = analysisConfig.getAiPicAccessHost();

        int failedCount = 0;
        for (Map.Entry<String, PowerMeterFlightDetailEntity> entry : dadMap.entrySet()) {
            PowerMeterFlightDetailEntity detail = entry.getValue();
            AIAnalysisPhotoExtPO analysisPhoto = new AIAnalysisPhotoExtPO();
            analysisPhoto.setRecordId(BizIdUtils.snowflakeIdStr());
            analysisPhoto.setAiTaskId(uosAiTask.getAiTaskId());
            analysisPhoto.setTaskId(uosAiTask.getTaskId());
            analysisPhoto.setCenterDetailId(Long.parseLong(detail.getDetailId()));
            analysisPhoto.setPhotoName(detail.getPhotoName());
            // 默认正常
            analysisPhoto.setState(AIAnalysisPhotoStateEnum.QUEUING.getType());
            analysisPhoto.setImageUrl(picAccessPrefix + detail.getOriginalPicUrl());
            analysisPhoto.setPhotoPath(detail.getOriginalPicUrl());
            analysisPhoto.setVersion(0);
            // a. 剔除非法图片格式[.JPG]的数据（添加至图片识别失败集合）
            String filenameExtension = StringUtils.getFilenameExtension(detail.getOriginalPicUrl());
            if (Objects.isNull(filenameExtension) || analysisConfig.getAiPicTypes().stream().noneMatch(r -> Objects.equals(filenameExtension.toLowerCase(), r))) {
                if (debugEnabled) {
                    log.debug("成果详情[{}]图片格式[{}]非法", detail.getDetailId(), filenameExtension);
                }
                analysisPhoto.setState(AIAnalysisPhotoStateEnum.FAILED.getType());
                analysisPhoto.setReason("图片格式不符合条件");
            }
            // b. 剔除图片AI识别状态为识别中的数据（添加至图片识别失败集合）
            if (invalidCenterDetailIds.contains(analysisPhoto.getCenterDetailId())) {
                if (debugEnabled) {
                    log.debug("成果详情[{}]图片AI识别状态非法[排队中/识别中/已暂停]", detail.getDetailId());
                }
                analysisPhoto.setState(AIAnalysisPhotoStateEnum.FAILED.getType());
                analysisPhoto.setReason("图片已在AI识别任务队列中");
            }
            // c. 剔除表计读数状态为识别中的数据（添加至图片识别失败集合）
            if (DialReadingTypeEnum.RECOGNIZING.matchEquals(detail.getReadingState())) {
                if (debugEnabled) {
                    log.debug("表计详情[{}]读数状态非法[识别中]", detail.getDetailId());
                }
                analysisPhoto.setState(AIAnalysisPhotoStateEnum.FAILED.getType());
                analysisPhoto.setReason("表计图片读数状态【识别中】");
            }
            // d. 剔除表计未关联部件的数据（添加至图片识别失败集合）
            if (!StringUtils.hasText(detail.getComponentId())) {
                if (debugEnabled) {
                    log.debug("表计详情[{}]未关联部件信息", detail.getDetailId());
                }
                analysisPhoto.setState(AIAnalysisPhotoStateEnum.FAILED.getType());
                analysisPhoto.setReason("表计图片未关联部件信息");
            }
            if (AIAnalysisPhotoStateEnum.FAILED.matchEquals(analysisPhoto.getState())) {
                failedCount++;
            }
            aiAnalysisPhotos.add(analysisPhoto);
        }
        // 更新失败数量
        uosAiTask.setImageFailedCount(failedCount);
        return aiAnalysisPhotos;
    }

    private UdaAnalysisTaskInDO buildUdaAIAnalysisTaskData(final AIRecognitionTaskInDTO data, final Set<AIAnalysisPhotoExtPO> aiAnalysisPhotos) {
        List<UdaAnalysisTaskInDO.TaskPic> taskPics = aiAnalysisPhotos.stream()
                .filter(r -> AIAnalysisPhotoStateEnum.QUEUING.matchEquals(r.getState()))
                .map(r -> {
                    UdaAnalysisTaskInDO.TaskPic taskPic = new UdaAnalysisTaskInDO.TaskPic();
                    taskPic.setPicId(r.getRecordId());
                    taskPic.setPicUrl(r.getImageUrl());
                    return taskPic;
                })
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(taskPics)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_LEGAL_FLIGHT_PHOTO.getContent()));
//            throw new BizException("未检索到合法的AI算法识别图片数据");
        }
        UdaAnalysisTaskInDO udaAnalysisTask = new UdaAnalysisTaskInDO();
        udaAnalysisTask.setTaskPicList(taskPics);
        udaAnalysisTask.setStorageIdList(data.getRecognitionFunIds());
        return udaAnalysisTask;
    }

    private Set<Long> fetchInvalidCenterDetailIds(Collection<Long> checkCenterDetailIds) {
        List<AIAnalysisPhotoEntity> photoEntities = aiAnalysisPhotoManager.fetchAnalysisPhotos(checkCenterDetailIds,
                AIAnalysisPhotoStateEnum.QUEUING.getType(), AIAnalysisPhotoStateEnum.EXECUTING.getType(), AIAnalysisPhotoStateEnum.PAUSED.getType());
        if (!CollectionUtils.isEmpty(photoEntities)) {
            return photoEntities.stream()
                    .map(AIAnalysisPhotoEntity::getCenterDetailId)
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    private IAIAnalysisTask getOrCreateAIAnalysisTask(AIAnalysisTaskEntity uosAiTask) {
        return AIAnalysisTasks.createIfAbsent(uosAiTask.getTaskId(), () -> AITaskProcessBuilder.builder()
                .taskId(uosAiTask.getTaskId())
                .taskName(uosAiTask.getAiTaskName())
                .taskType(uosAiTask.getAiTaskType())
                .taskState(uosAiTask.getState())
                .executionTime(Objects.nonNull(uosAiTask.getCostTime()) ? uosAiTask.getCostTime() * 1000 : 0)
                .totalPic(uosAiTask.getTotalImageCount())
                .successPic(uosAiTask.getImageSuccessCount())
                .failedPic(uosAiTask.getImageFailedCount())
                .dataId(uosAiTask.getCenterBaseId().toString())
                .flightTaskId(uosAiTask.getFlightTaskId().toString())
                .flightTaskTag(uosAiTask.getTagName())
                .orgCode(uosAiTask.getOrgCode())
                .accountId(uosAiTask.getCreatorId())
                .auto(uosAiTask.getAuto())
                .build());
    }

    private Map<Long, DataAnalysisDetailOutDTO> checkAndGotCenterDetailInfos(AIRecognitionTaskInDTO data, DataAnalysisBaseSimpleOutDTO dab) {
        List<String> dataPhotoIds = data.getDataPhotoIds();
        if (!CollectionUtils.isEmpty(dataPhotoIds)) {
            List<Long> centerDetailIds = dataPhotoIds.stream()
                    .map(this::convertToLong)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            List<DataAnalysisDetailOutDTO> dadList = dataAnalysisDetailService.findDataAnalysisDetails(centerDetailIds);
            if (!CollectionUtils.isEmpty(dadList)) {
                boolean allMatch = dadList.stream().allMatch(r -> Objects.equals(r.getCenterBaseId(), dab.getCenterBaseId()));
                if (allMatch) {
                    return dadList.stream()
                            .collect(Collectors.toMap(DataAnalysisDetailOutDTO::getCenterDetailId, r -> r));
                }
                throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MULTIPLE_FLIGHT_PHOTO_NOT_BELONG_TO_SAME_MISSION.getContent()));
//                throw new BizException("同一个AI分析任务不能包含多个飞行成果的图片数据");
            }
        }
        throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_VALID_FLIGHT_PHOTO_WAS_FOUND.getContent()));
//        throw new BizException("未查询到有效的飞行成果图片数据");
    }

    private Map<String, PowerMeterFlightDetailEntity> checkAndGotDetailInfos(AIRecognitionTaskInDTO data, PowerMeterFlightDataEntity dab) {
        List<String> dataPhotoIds = data.getDataPhotoIds();
        if (!CollectionUtils.isEmpty(dataPhotoIds)) {
            List<String> centerDetailIds = dataPhotoIds.stream()
                    .filter(StringUtils::hasText)
                    .collect(Collectors.toList());
            List<PowerMeterFlightDetailEntity> meterDetails = powerMeterDetailService.getMeterDetails(centerDetailIds);
            if (!CollectionUtils.isEmpty(meterDetails)) {
                boolean allMatch = meterDetails.stream().allMatch(r -> Objects.equals(r.getDataId(), dab.getDataId()));
                if (allMatch) {
                    return meterDetails.stream()
                            .collect(Collectors.toMap(PowerMeterFlightDetailEntity::getDetailId, r -> r));
                }
                throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MULTIPLE_FLIGHT_PHOTO_NOT_BELONG_TO_SAME_MISSION.getContent()));
//                throw new BizException("同一个AI分析任务不能包含多个飞行成果的图片数据");
            }
        }
        throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_VALID_FLIGHT_PHOTO_WAS_FOUND.getContent()));
//        throw new BizException("未查询到有效的飞行成果图片数据");
    }

    private DataAnalysisBaseSimpleOutDTO checkAndGotCenterBaseInfo(String centerBaseId) {
        Long temp = convertToLong(centerBaseId);
        Optional<DataAnalysisBaseSimpleOutDTO> optional = dataAnalysisBaseService.findDataAnalysisBase(temp);
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FLIGHT_SORTIES_RESULT_DATA_NOT_EXIST.getContent()));
//        throw new BizParameterException("飞行架次成果数据不存在");
    }

    private PowerMeterFlightDataEntity checkAndGotDataInfo(String baseDataId) {
        Optional<PowerMeterFlightDataEntity> optional = powerMeterDataService.findPowerMeterData(baseDataId);
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FLIGHT_SORTIES_RESULT_DATA_NOT_EXIST.getContent()));
//        throw new BizParameterException("飞行架次成果数据不存在");
    }

    private Long convertToLong(String data) {
        Long temp = null;
        try {
            temp = Long.parseLong(data);
        } catch (NumberFormatException e) {
            log.warn("参数无法解析，原因：{}", e.getMessage());
        }
        return temp;
    }

    private AIAnalysisTaskDataOutDTO buildAIAnalysisTaskDataLatestInfo(IAITaskProcess process, Collection<String> photoRecordIds) {
        AIAnalysisTaskDataOutDTO changedData = new AIAnalysisTaskDataOutDTO();
        if (process instanceof IManualAITaskProcess) {
            changedData.setAccountId(((IManualAITaskProcess) process).getAccountId());
        }
        if (process instanceof ISystemAITaskProcess) {
            changedData.setAuto(true);
            changedData.setOrgCode(((ISystemAITaskProcess) process).getOrgCode());
        }
        changedData.setAiTaskId(process.getTaskId());
        changedData.setAiTaskType(process.getTaskType());
        changedData.setTaskName(process.getTaskName());
        changedData.setTaskId(process.getFlightTaskId());
        changedData.setCenterBaseId(process.getDataId());
        changedData.setTagName(process.getTaskName());
        changedData.setTaskState(process.getTaskState());
        changedData.setCostSecs((int) (process.getExecutionTime() / 1000));
        changedData.setImageTotalCounts(process.getTotalPic());
        changedData.setImageSuccessCounts(process.getSuccessPic());
        changedData.setImageFailedCounts(process.getFailedPic());
        List<AIAnalysisPhotoEntity> analysisPhotos;
        if (!CollectionUtils.isEmpty(photoRecordIds)) {
            // 某些图片进度发送变更
            analysisPhotos = aiAnalysisPhotoManager.fetchAnalysisPhotos(photoRecordIds);
        } else {
            // 初始化时全量查询
            analysisPhotos = aiAnalysisPhotoManager.getAnalysisPhotos(process.getTaskId());
        }
        if (!CollectionUtils.isEmpty(analysisPhotos)) {
            List<AIAnalysisTaskDataOutDTO.AnalysisPhotoRecord> changedRecords = analysisPhotos.stream()
                    .map(r -> {
                        AIAnalysisTaskDataOutDTO.AnalysisPhotoRecord record = new AIAnalysisTaskDataOutDTO.AnalysisPhotoRecord();
                        record.setRecordId(r.getRecordId());
                        record.setPhotoName(r.getPhotoName());
                        record.setState(r.getState());
                        return record;
                    })
                    .collect(Collectors.toList());
            changedData.setChangedRecords(changedRecords);
        }
        return changedData;
    }

}
