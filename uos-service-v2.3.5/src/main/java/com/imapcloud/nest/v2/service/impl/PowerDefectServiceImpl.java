package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.geoai.common.web.util.ResultUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.imapcloud.nest.v2.common.constant.UosConstants;
import com.imapcloud.nest.v2.common.enums.*;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.dao.po.MeterDataDetailDefectQueryCriteriaPO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerMeterDefectMarkInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.RocketmqInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.UdaAnalysisInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.*;
import com.imapcloud.nest.v2.manager.feign.UdaAnalysisServiceClient;
import com.imapcloud.nest.v2.manager.listener.rocketmq.PowerAiTaskResultListener;
import com.imapcloud.nest.v2.manager.sql.*;
import com.imapcloud.nest.v2.service.*;
import com.imapcloud.nest.v2.service.dto.TaskInfoDTO;
import com.imapcloud.nest.v2.service.dto.in.PowerDefectInDTO;
import com.imapcloud.nest.v2.service.dto.out.AIAnalysisTaskDataOutDTO;
import com.imapcloud.nest.v2.service.dto.out.PowerDefectOutDTO;
import com.imapcloud.nest.v2.service.dto.out.TopicOutDTO;
import com.imapcloud.sdk.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 缺陷识别
 *
 * @author boluo
 * @date 2023-03-07
 */
@Slf4j
@Service
public class PowerDefectServiceImpl implements PowerDefectService {

    @Resource
    private PowerMeterFlightDetailDefectManager powerMeterFlightDetailDefectManager;

    @Resource
    private PowerMeterDefectMarkManager powerMeterDefectMarkManager;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private RocketmqService rocketmqService;

    @Resource
    private UdaAnalysisServiceClient udaAnalysisServiceClient;

    @Resource
    private PowerMeterDataManager powerMeterDataManager;

    @Resource
    private PowerMeterDataService powerMeterDataService;

    @Resource
    private DataAnalysisAlgoProblemTypeRefManager dataAnalysisAlgoProblemTypeRefManager;

    @Resource
    private TopicService topicService;

    @Resource
    private PowerInspectionReportManager powerInspectionReportManager;

    @Resource
    private PowerTaskService powerTaskService;

    @Override
    public PageResultInfo<PowerDefectOutDTO.ListOutDTO> list(PowerDefectInDTO.ListInDTO listInDTO) {

        MeterDataDetailDefectQueryCriteriaPO listQuery = MeterDataDetailDefectQueryCriteriaPO.builder()
                .dataId(listInDTO.getDataId())
                .defectState(listInDTO.getDefectState())
                .verificationStatus(listInDTO.getVerificationStatus())
                .deviceState(listInDTO.getDeviceState())
                .build();
        long count = powerMeterFlightDetailDefectManager.countByCondition(listQuery);
        if (count <= 0) {
            return PageResultInfo.of(0, Collections.emptyList());
        }
        List<PowerMeterFlightDetailDefectOutDO> outDOList = powerMeterFlightDetailDefectManager
                .selectByCondition(listQuery, PagingRestrictDo.getPagingRestrict(listInDTO));

        List<PowerDefectOutDTO.ListOutDTO> outDTOList = Lists.newArrayList();
        for (PowerMeterFlightDetailDefectOutDO outDO : outDOList) {
            PowerDefectOutDTO.ListOutDTO listOutDTO = new PowerDefectOutDTO.ListOutDTO();
            BeanUtils.copyProperties(outDO, listOutDTO);
            if (StringUtil.isEmpty(listOutDTO.getComponentName())) {
                listOutDTO.setComponentName(outDO.getPhotoName());
            }
            outDTOList.add(listOutDTO);
        }
        return PageResultInfo.of(count, outDTOList);
    }

    @Override
    public PowerDefectOutDTO.DetailOutDTO detail(String detailId) {

        List<String> detailIdList = Lists.newArrayList(detailId);
        List<PowerMeterFlightDetailDefectOutDO> defectOutDOList = powerMeterFlightDetailDefectManager
                .selectListByDetailIdList(detailIdList);
        if (CollUtil.isEmpty(defectOutDOList)) {
            throw new BizException("detailId not exists, please check param.");
        }
        PowerDefectOutDTO.DetailOutDTO detailOutDTO = new PowerDefectOutDTO.DetailOutDTO();
        BeanUtils.copyProperties(defectOutDOList.get(0), detailOutDTO);
        List<PowerDefectOutDTO.DefectMarkInfo> defectMarkInfoList = Lists.newLinkedList();
        detailOutDTO.setDefectMarkInfoList(defectMarkInfoList);

        // 查询标注信息
        List<PowerMeterDefectMarkOutDO> markOutDOList = powerMeterDefectMarkManager.selectListByDetailIdList(detailIdList);
        for (PowerMeterDefectMarkOutDO outDO : markOutDOList) {
            PowerDefectOutDTO.DefectMarkInfo defectMarkInfo = new PowerDefectOutDTO.DefectMarkInfo();
            BeanUtils.copyProperties(outDO, defectMarkInfo);
            defectMarkInfoList.add(defectMarkInfo);
        }
        return detailOutDTO;
    }

    @Override
    public String addMark(PowerDefectInDTO.AddMarkInDTO addMarkInDTO) {

        // 检测图片状态
        checkRunning(Lists.newArrayList(addMarkInDTO.getDetailId()), true);

        PowerMeterDefectMarkInDO powerMeterDefectMarkInDO = new PowerMeterDefectMarkInDO();
        BeanUtils.copyProperties(addMarkInDTO, powerMeterDefectMarkInDO);
        powerMeterDefectMarkInDO.setAiMark(0);
        return powerMeterDefectMarkManager.addMark(powerMeterDefectMarkInDO);
    }

    @Override
    public void deleteMark(List<String> defectMarkIdList, String accountId) {
        List<PowerMeterDefectMarkOutDO> outDOList = powerMeterDefectMarkManager.selectListByDefectMarkList(defectMarkIdList);
        if (CollUtil.isEmpty(outDOList)) {
            return;
        }
        String detailId = outDOList.get(0).getDetailId();
        checkRunning(Lists.newArrayList(detailId), true);

        powerMeterDefectMarkManager.deleteMark(defectMarkIdList, accountId, detailId);
    }

    @Override
    public void batchDeleteByDataIdList(List<String> dataIdList, String accountId) {
        List<PowerMeterFlightDetailDefectOutDO> outDOList = powerMeterFlightDetailDefectManager.selectListByDataIdList(dataIdList);
        List<String> detailIdList = outDOList.stream()
                .map(PowerMeterFlightDetailDefectOutDO::getDetailId)
                .collect(Collectors.toList());
        delete(detailIdList, accountId);
    }

    @Override
    public void checkRunning(List<String> detailIdList, boolean verification) {
        List<PowerMeterFlightDetailDefectOutDO> defectOutDOList = powerMeterFlightDetailDefectManager.selectListByDetailIdList(detailIdList);
        if (CollUtil.isEmpty(defectOutDOList)) {
            return;
        }
        for (PowerMeterFlightDetailDefectOutDO powerMeterFlightDetailDefectOutDO : defectOutDOList) {
            if (powerMeterFlightDetailDefectOutDO.getDefectState() == PowerDefectStateEnum.DEFECT_ING.getCode()) {
                throw new BizException(MessageUtils
                        .getMessage(MessageEnum.GEOAI_UOS_UNABLE_TO_OPERATE_THE_PHOTO_DATA_IN_AI_RECOGNITION.getContent()));
            }
            if (verification && powerMeterFlightDetailDefectOutDO.getVerificationState() != InspectionVerifyStateEnum.DAIHESHI.getTypeInt()) {
                throw new BizException(MessageUtils
                        .getMessage(MessageEnum.GEOAI_UOS_UNABLE_TO_OPERATE_THE_PHOTO_DATA_IN_VERIFICATION.getContent()));
            }
        }
    }

    @Override
    public void delete(List<String> detailIdList, String accountId) {

        checkRunning(detailIdList, false);
        powerMeterFlightDetailDefectManager.deleteByDetailIdList(detailIdList, accountId);

        // 巡检报告
        powerInspectionReportManager.deleteRelBatch(detailIdList);
    }

    @Override
    public Map<Integer, Long> statistics(String dataId) {

        List<PowerMeterFlightDetailDefectOutDO.StatisticsOutDO> outDOList = powerMeterFlightDetailDefectManager
                .statistics(dataId);
        Map<Integer, Long> integerLongMap = outDOList.stream().collect(Collectors
                .toMap(PowerMeterFlightDetailDefectOutDO.StatisticsOutDO::getDeviceState
                        , PowerMeterFlightDetailDefectOutDO.StatisticsOutDO::getNum, (key1, key2) -> key2));
        Map<Integer, Long> result = Maps.newHashMap();
        for (PowerDeviceStateEnum value : PowerDeviceStateEnum.values()) {

            Long aLong = integerLongMap.get(value.getCode());
            result.put(value.getCode(), aLong == null ? 0 : aLong);
        }
        return result;
    }

    @Override
    public void updateTaskStatePre(List<String> detailIdList, String dataId) {

        powerMeterFlightDetailDefectManager.taskPre(detailIdList);

        // 取消暂停
        powerMeterFlightDetailDefectManager.taskUnpause(dataId);
    }

    @Override
    public void manualSendWs(String orgCode) {
        List<String> dataIdList = powerMeterFlightDetailDefectManager.selectDataIdByOrgCode(orgCode);
        for (String dataId : dataIdList) {
            sendWs(dataId, false);
        }
    }

    @Override
    public void sendWs(String dataId, boolean initialized) {
        log.info("#PowerDefectServiceImpl.sendWs# dataId={}", dataId);
        List<String> dataIdList = Lists.newArrayList(dataId);
        List<PowerMeterFlightDataOutDO> dataOutDOList = powerMeterDataManager.selectListByDataIdList(dataIdList);
        log.info("#PowerDefectServiceImpl.sendWs# dataId={}, dataOutDOList={}", dataId, dataOutDOList);
        if (CollUtil.isEmpty(dataOutDOList)) {
            return;
        }
        PowerMeterFlightDataOutDO powerMeterFlightDataOutDO = dataOutDOList.get(0);
        List<PowerMeterFlightDetailDefectOutDO> detailDefectOutDOList = powerMeterFlightDetailDefectManager
                .selectListByDataIdList(dataIdList);
        this.send(powerMeterFlightDataOutDO, detailDefectOutDOList, initialized, null);
    }

    private void send(PowerMeterFlightDataOutDO powerMeterFlightDataOutDO,
                      List<PowerMeterFlightDetailDefectOutDO> defectOutDOList,
                      boolean initialized, PowerTaskStateEnum powerTaskStateEnum) {

        log.info("#PowerDefectServiceImpl.send# defectOutDOList={}", defectOutDOList);
        defectOutDOList = defectOutDOList.stream()
                .filter(bean -> bean.getTaskState() != PowerTaskStateEnum.TASK_INIT.getCode())
                .collect(Collectors.toList());
        log.info("#PowerDefectServiceImpl.send# defectOutDOList={}", defectOutDOList);
        if (CollUtil.isEmpty(defectOutDOList)) {
            return;
        }
        // 查询任务信息
        TaskInfoDTO taskInfoDTO = powerTaskService.getTaskInfoByDataId(powerMeterFlightDataOutDO.getDataId());
        if (taskInfoDTO == null) {
            log.info("#PowerDefectServiceImpl.send# task not exists");
            return;
        }

        LocalDateTime taskStartTime = LocalDateTime.now();
        int successCount = 0;
        int failureCount = 0;
        int runningCount = 0;
        int pauseCount = 0;
        for (PowerMeterFlightDetailDefectOutDO powerMeterFlightDetailDefectOutDO : defectOutDOList) {

            if (taskStartTime.isAfter(powerMeterFlightDetailDefectOutDO.getTaskStartTime())) {
                taskStartTime = powerMeterFlightDetailDefectOutDO.getTaskStartTime();
            }
            if (powerMeterFlightDetailDefectOutDO.getTaskState() == PowerTaskStateEnum.TASK_END.getCode()) {
                successCount ++;
            } else if (powerMeterFlightDetailDefectOutDO.getTaskState() == PowerTaskStateEnum.TASK_PAUSE.getCode()) {
                pauseCount ++;
            } else if (powerMeterFlightDetailDefectOutDO.getTaskState() == PowerTaskStateEnum.TASK_PRE.getCode() ||
                    powerMeterFlightDetailDefectOutDO.getTaskState() == PowerTaskStateEnum.TASK_ING.getCode()) {
                runningCount ++;
            } else {
                failureCount ++;
            }
        }
        long taskStart = taskStartTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        AIAnalysisTaskDataOutDTO aiAnalysisTaskDataOutDTO = new AIAnalysisTaskDataOutDTO();
//        aiAnalysisTaskDataOutDTO.setAccountId();
        aiAnalysisTaskDataOutDTO.setOrgCode(powerMeterFlightDataOutDO.getOrgCode());
        aiAnalysisTaskDataOutDTO.setAiTaskId(powerMeterFlightDataOutDO.getDataId());
        aiAnalysisTaskDataOutDTO.setTaskName(powerMeterFlightDataOutDO.getTaskFlyIndexName());
        if (powerTaskStateEnum == PowerTaskStateEnum.TASK_STOP) {
            aiAnalysisTaskDataOutDTO.setTaskState(PowerTaskStateEnum.TASK_STOP.getCode());
        } else if (pauseCount != 0) {
            aiAnalysisTaskDataOutDTO.setTaskState(PowerTaskStateEnum.TASK_PAUSE.getCode());
        } else if (runningCount != 0) {
            aiAnalysisTaskDataOutDTO.setTaskState(PowerTaskStateEnum.TASK_ING.getCode());
        } else {
            aiAnalysisTaskDataOutDTO.setTaskState(PowerTaskStateEnum.TASK_END.getCode());
        }

        aiAnalysisTaskDataOutDTO.setImageTotalCounts(defectOutDOList.size());
        aiAnalysisTaskDataOutDTO.setImageSuccessCounts(successCount);
        aiAnalysisTaskDataOutDTO.setImageFailedCounts(failureCount);
        aiAnalysisTaskDataOutDTO.setCostSecs((int) ((System.currentTimeMillis() - taskStart) / 1000));
        aiAnalysisTaskDataOutDTO.setCenterBaseId(powerMeterFlightDataOutDO.getDataId());
        aiAnalysisTaskDataOutDTO.setTaskId(String.valueOf(powerMeterFlightDataOutDO.getTaskId()));
        aiAnalysisTaskDataOutDTO.setTagName(powerMeterFlightDataOutDO.getTagName());
        aiAnalysisTaskDataOutDTO.setInitialized(initialized);
        aiAnalysisTaskDataOutDTO.setAuto(taskInfoDTO.getSystem());
        aiAnalysisTaskDataOutDTO.setAiTaskType(AIAnalysisTaskTypeEnum.DEFECTS.getType());
        List<AIAnalysisTaskDataOutDTO.AnalysisPhotoRecord> changedRecords = Lists.newArrayList();
        aiAnalysisTaskDataOutDTO.setChangedRecords(changedRecords);
        for (PowerMeterFlightDetailDefectOutDO powerMeterFlightDetailDefectOutDO : defectOutDOList) {
            AIAnalysisTaskDataOutDTO.AnalysisPhotoRecord analysisPhotoRecord = new AIAnalysisTaskDataOutDTO.AnalysisPhotoRecord();
            analysisPhotoRecord.setRecordId(powerMeterFlightDetailDefectOutDO.getDetailId());
            analysisPhotoRecord.setPhotoName(powerMeterFlightDetailDefectOutDO.getPhotoName());
            analysisPhotoRecord.setState(powerMeterFlightDetailDefectOutDO.getTaskState());
            changedRecords.add(analysisPhotoRecord);
        }
        powerMeterDataService.sendWs(aiAnalysisTaskDataOutDTO, taskInfoDTO.getAccountIdSet());
    }

    private void taskInit(String dataId) {
        powerTaskService.delTask(dataId);
        List<PowerMeterFlightDataOutDO> dataOutDOList = powerMeterDataManager
                .selectListByDataIdList(Lists.newArrayList(dataId));
        if (CollUtil.isNotEmpty(dataOutDOList)) {
            this.taskChange(dataOutDOList.get(0), PowerTaskStateEnum.TASK_INIT);
        }
    }

    @Override
    public void defect(RocketmqInDO.TaskMqInfo taskMqInfo) {
        log.info("#PowerDefectServiceImpl.defect# taskMqInfo={}", taskMqInfo);

        List<PowerMeterFlightDetailDefectOutDO> defectOutDOList = powerMeterFlightDetailDefectManager
                .selectListByDataIdList(Lists.newArrayList(taskMqInfo.getDataId()));

        // 任务是否暂停
        List<PowerMeterFlightDetailDefectOutDO> pauseList = defectOutDOList.stream()
                .filter(infraredOutDO -> infraredOutDO.getTaskState() == PowerTaskStateEnum.TASK_PAUSE.getCode())
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(pauseList)) {

            log.info("#PowerDefectServiceImpl.defect# pause taskMqInfo={}", taskMqInfo);
            sendWs(taskMqInfo.getDataId(), false);
            rocketmqService.sendTask(taskMqInfo);
            return;
        }

        // 任务是否终止
        List<PowerMeterFlightDetailDefectOutDO> stopList = defectOutDOList.stream()
                .filter(infraredOutDO -> infraredOutDO.getTaskState() == PowerTaskStateEnum.TASK_STOP.getCode())
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(stopList)) {

            log.info("#PowerDefectServiceImpl.defect# stop taskMqInfo={}", taskMqInfo);
            taskInit(taskMqInfo.getDataId());
            return;
        }

        // 正在执行的任务
        List<PowerMeterFlightDetailDefectOutDO> runList = defectOutDOList.stream()
                .filter(infraredOutDO -> infraredOutDO.getTaskState() == PowerTaskStateEnum.TASK_ING.getCode())
                .collect(Collectors.toList());
        // 正在执行的任务是否超时
        if (CollUtil.isNotEmpty(runList)) {
            LocalDateTime now = LocalDateTime.now();
            List<String> timeoutDetailIdList = Lists.newLinkedList();
            for (PowerMeterFlightDetailDefectOutDO param : runList) {
                if (param.getTaskPictureStartTime() == null
                        || param.getTaskPictureStartTime()
                        .plusSeconds(geoaiUosProperties.getRocketmq().getTaskTimeoutSecond()).isBefore(now)) {
                    timeoutDetailIdList.add(param.getDetailId());
                }
            }
            if (CollUtil.isNotEmpty(timeoutDetailIdList)) {
                powerMeterFlightDetailDefectManager.taskTimeout(timeoutDetailIdList);
            }
        }

        if (powerTaskService.taskEmpty(taskMqInfo)) {
            sendWs(taskMqInfo.getDataId(), false);
            // 任务已全部发送到UDA
            if (CollUtil.isNotEmpty(runList)) {
                rocketmqService.sendTask(taskMqInfo);
            } else {
                log.info("#PowerDefectServiceImpl.defect# end taskMqInfo={}", taskMqInfo);
                taskInit(taskMqInfo.getDataId());
            }
            return;
        }

        // 任务数判定
        if (runList.size() >= geoaiUosProperties.getRocketmq().getMissionTaskMaxNum()) {

            log.info("#PowerDefectServiceImpl.defect# runSize={} taskMqInfo={}", runList.size(), taskMqInfo);
            sendWs(taskMqInfo.getDataId(), false);
            rocketmqService.sendTask(taskMqInfo);
            return;
        }

        // 获取一张图片执行
        TaskInfoDTO.DetailInfoDTO detailInfoDTO = powerTaskService.removeOneTaskPicture(taskMqInfo);
        log.info("#PowerDefectServiceImpl.defect# detailInfoDTO={}", detailInfoDTO);
        if (detailInfoDTO == null) {
            return;
        }
        // 本次需要执行的任务
        PowerMeterFlightDetailDefectOutDO runInfraredOutDO = null;
        for (PowerMeterFlightDetailDefectOutDO defectOutDO : defectOutDOList) {
            if (detailInfoDTO.getDetailId().equals(defectOutDO.getDetailId())) {
                runInfraredOutDO = defectOutDO;
                break;
            }
        }

        if (runInfraredOutDO != null && runInfraredOutDO.getTaskState() == PowerTaskStateEnum.TASK_PRE.getCode()) {
            powerMeterFlightDetailDefectManager.taskIng(runInfraredOutDO.getDetailId());
            // 调用UDA发送识别任务
            addTask(taskMqInfo, runInfraredOutDO, detailInfoDTO);
        }
        log.info("#PowerDefectServiceImpl.defect# taskMqInfo={}", taskMqInfo);
        sendWs(taskMqInfo.getDataId(), false);
        rocketmqService.sendTask(taskMqInfo);
    }
    private void addTask(RocketmqInDO.TaskMqInfo taskMqInfo, PowerMeterFlightDetailDefectOutDO runInfraredOutDO
            , TaskInfoDTO.DetailInfoDTO detailInfoDTO) {

        try {
            String orgCode = detailInfoDTO.getOrgCode();
            List<String> functionIdList = detailInfoDTO.getFunctionIdList();
            String picAccessPrefix = geoaiUosProperties.getAnalysis().getAiPicAccessHost();
            String pictureUrl = String.format("%s%s", picAccessPrefix, runInfraredOutDO.getPictureUrl());

            RocketmqInDO.BusinessParamInfo businessParamInfo = new RocketmqInDO.BusinessParamInfo();
            businessParamInfo.setDetailId(runInfraredOutDO.getDetailId());
            businessParamInfo.setDataId(runInfraredOutDO.getDataId());

            UdaAnalysisInDO.AddPictureTaskInDO addPictureTaskInDO = new UdaAnalysisInDO.AddPictureTaskInDO();
            addPictureTaskInDO.setOrgCode(orgCode);
            List<Long> collect = functionIdList.stream().map(Long::parseLong).collect(Collectors.toList());
            addPictureTaskInDO.setFunctionIdList(collect);
            addPictureTaskInDO.setPictureUrl(pictureUrl);
            addPictureTaskInDO.setBusinessParam(JSONUtil.toJsonStr(businessParamInfo));

            log.info("#PowerDefectServiceInpl.addTask# addPictureTaskInDO={}", addPictureTaskInDO);
            Result<String> stringResult = udaAnalysisServiceClient.addPictureTask(addPictureTaskInDO);
            log.info("#PowerDefectServiceInpl.addTask# addPictureTaskInDO={}, stringResult={}", addPictureTaskInDO, stringResult);
            String data = ResultUtils.getData(stringResult);
            if ("NOT AUTH".equals(data)) {
                PowerAiTaskResultListener.MqInfo mqInfo = new PowerAiTaskResultListener.MqInfo();
                mqInfo.setOrgCode(orgCode);
                mqInfo.setTaskId(BizIdUtils.snowflakeIdStr());
                mqInfo.setPictureUrl(pictureUrl);
                mqInfo.setBusinessParam("");
                PowerAiTaskResultListener.TaskResultInfo taskResultInfo = new PowerAiTaskResultListener.TaskResultInfo();
                taskResultInfo.setTaskState(PowerAiTaskResultListener.TaskStateEnum.UNAUTH.getCode());
                mqInfo.setTaskResultInfoInDTO(taskResultInfo);
                this.result(mqInfo, businessParamInfo);
            }
        } catch (Exception e) {
            log.error("#PowerDefectServiceInpl.addTask# taskMqInfo={}", taskMqInfo, e);
        }
    }

    @Override
    public void taskChange(PowerMeterFlightDataOutDO powerMeterFlightDataOutDO, PowerTaskStateEnum powerTaskStateEnum) {

        String dataId = powerMeterFlightDataOutDO.getDataId();
        List<String> dataIdList = Lists.newArrayList(dataId);
        List<PowerMeterFlightDetailDefectOutDO> defectOutDOList = null;
        // 更新
        if (PowerTaskStateEnum.TASK_INIT == powerTaskStateEnum) {
            defectOutDOList = powerMeterFlightDetailDefectManager.selectListByDataIdList(dataIdList);
            powerMeterFlightDetailDefectManager.taskInit(dataId);
        } else if (PowerTaskStateEnum.TASK_PAUSE == powerTaskStateEnum) {
            powerMeterFlightDetailDefectManager.taskPause(dataId);
            defectOutDOList = powerMeterFlightDetailDefectManager.selectListByDataIdList(dataIdList);
        } else if (PowerTaskStateEnum.TASK_PRE == powerTaskStateEnum) {
            powerMeterFlightDetailDefectManager.taskUnpause(dataId);
            defectOutDOList = powerMeterFlightDetailDefectManager.selectListByDataIdList(dataIdList);
        } else if (PowerTaskStateEnum.TASK_STOP == powerTaskStateEnum) {
            // 终止的话，需要先取之前的状态（终止会初始化任务状态）
            defectOutDOList = powerMeterFlightDetailDefectManager.selectListByDataIdList(dataIdList);
            for (PowerMeterFlightDetailDefectOutDO powerMeterFlightDetailDefectOutDO : defectOutDOList) {
                if (powerMeterFlightDetailDefectOutDO.getTaskState() == PowerTaskStateEnum.TASK_PRE.getCode() ||
                        powerMeterFlightDetailDefectOutDO.getTaskState() == PowerTaskStateEnum.TASK_ING.getCode() ||
                        powerMeterFlightDetailDefectOutDO.getTaskState() == PowerTaskStateEnum.TASK_PAUSE.getCode()) {
                    powerMeterFlightDetailDefectOutDO.setTaskState(PowerTaskStateEnum.TASK_STOP.getCode());
                }
            }
            powerMeterFlightDetailDefectManager.taskStop(dataId);
        }
        send(powerMeterFlightDataOutDO, defectOutDOList, false, powerTaskStateEnum);
    }

    @Override
    public void result(PowerAiTaskResultListener.MqInfo mqInfo, RocketmqInDO.BusinessParamInfo businessParamInfo) {
        log.info("#PowerDefectServiceImpl.result# businessParamInfo={}, mqInfo={}", businessParamInfo, mqInfo);
        List<PowerMeterFlightDetailDefectOutDO> defectOutDOList = powerMeterFlightDetailDefectManager
                .selectListByDataIdList(Lists.newArrayList(businessParamInfo.getDataId()));
        if (CollUtil.isEmpty(defectOutDOList)) {
            return;
        }
        List<PowerMeterFlightDetailDefectOutDO> collect = defectOutDOList.stream()
                .filter(o -> o.getDetailId().equals(businessParamInfo.getDetailId()))
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(collect)) {
            return;
        }
        PowerMeterFlightDetailDefectOutDO powerMeterFlightDetailDefectOutDO = collect.get(0);
        if (powerMeterFlightDetailDefectOutDO.getTaskState() == PowerTaskStateEnum.TASK_ING.getCode()
                || powerMeterFlightDetailDefectOutDO.getTaskState() == PowerTaskStateEnum.TASK_PAUSE.getCode()
                || powerMeterFlightDetailDefectOutDO.getTaskState() == PowerTaskStateEnum.TASK_PRE.getCode()) {

            PowerAiTaskResultListener.TaskResultInfo taskResultInfoInDTO = mqInfo.getTaskResultInfoInDTO();
            if (taskResultInfoInDTO.getTaskState() == PowerAiTaskResultListener.TaskStateEnum.UNAUTH.getCode()) {
                log.info("#PowerDefectServiceImpl.result# un auth businessParamInfo={}", businessParamInfo);
                powerMeterFlightDetailDefectManager.taskNoAuth(businessParamInfo.getDetailId());
                return;
            }

            List<PowerMeterDefectMarkInDO> powerMeterDefectMarkInDOList = Lists.newLinkedList();

            if (taskResultInfoInDTO.getTaskState() == PowerAiTaskResultListener.TaskStateEnum.SUCCESS.getCode()
                    && CollUtil.isNotEmpty(taskResultInfoInDTO.getInfoInDTOList())) {

                Set<String> problemTypeIdSet = taskResultInfoInDTO.getInfoInDTOList().stream()
                        .map(PowerAiTaskResultListener.MarkInfo::getProblemTypeId)
                        .collect(Collectors.toSet());

                // 查询uda和uos行业类型问题的关系
                List<DataAnalysisAlgoProblemTypeRefOutDO> outDOList = dataAnalysisAlgoProblemTypeRefManager
                        .selectListByTypeRelationIdList(problemTypeIdSet);
                Set<Long> typeIdSet = outDOList.stream().map(DataAnalysisAlgoProblemTypeRefOutDO::getTypeId).collect(Collectors.toSet());
                List<TopicOutDTO.IndustryProblemListOut> industryProblemListOutList = topicService.selectIndustryProblemList(typeIdSet);
                Map<String, DataAnalysisAlgoProblemTypeRefOutDO> typeRefOutDOMap = outDOList.stream()
                        .collect(Collectors.toMap(DataAnalysisAlgoProblemTypeRefOutDO::getTypeRelationId, bean -> bean, (key1, key2) -> key1));
                Map<Long, TopicOutDTO.IndustryProblemListOut> problemListOutMap = industryProblemListOutList.stream()
                        .collect(Collectors.toMap(TopicOutDTO.IndustryProblemListOut::getTopicProblemId, bean -> bean, (key1, key2) -> key1));

                for (PowerAiTaskResultListener.MarkInfo markInfo : taskResultInfoInDTO.getInfoInDTOList()) {
                    PowerMeterDefectMarkInDO powerMeterDefectMarkInDO = new PowerMeterDefectMarkInDO();
                    powerMeterDefectMarkInDO.setDetailId(businessParamInfo.getDetailId());
                    powerMeterDefectMarkInDO.setAiMark(1);
                    powerMeterDefectMarkInDO.setSiteX1(markInfo.getX1());
                    powerMeterDefectMarkInDO.setSiteY1(markInfo.getY1());
                    powerMeterDefectMarkInDO.setSiteX2(markInfo.getX2());
                    powerMeterDefectMarkInDO.setSiteY2(markInfo.getY2());
                    powerMeterDefectMarkInDO.setDeviceState(PowerDeviceStateEnum.NORMAL.getCode());
                    powerMeterDefectMarkInDO.setAccountId(UosConstants.SYSTEM_ACCOUNT_ID);

                    powerMeterDefectMarkInDO.setTopicProblemName(markInfo.getProblemTypeName());
                    DataAnalysisAlgoProblemTypeRefOutDO dataAnalysisAlgoProblemTypeRefOutDO = typeRefOutDOMap.get(markInfo.getProblemTypeId());
                    if (dataAnalysisAlgoProblemTypeRefOutDO != null) {
                        TopicOutDTO.IndustryProblemListOut industryProblemListOut = problemListOutMap.get(dataAnalysisAlgoProblemTypeRefOutDO.getTypeId());
                        if (industryProblemListOut != null) {
                            powerMeterDefectMarkInDO.setIndustryType(industryProblemListOut.getIndustryType());
                            powerMeterDefectMarkInDO.setTopicProblemId(String.valueOf(industryProblemListOut.getTopicProblemId()));
                            powerMeterDefectMarkInDO.setTopicProblemName(industryProblemListOut.getTopicProblemName());
                            powerMeterDefectMarkInDO.setDeviceState(PowerDeviceStateEnum.GNERMAL_DEFECTS.getCode());
                        }
                    }
                    powerMeterDefectMarkInDOList.add(powerMeterDefectMarkInDO);
                }
                // 自动巡检结束，先清空旧数据，再添加
                log.info("#PowerDefectServiceImpl.result# businessParamInfo={}, mark start", businessParamInfo);
                powerMeterDefectMarkManager.deleteByDetailId(businessParamInfo.getDetailId());
                powerMeterDefectMarkManager.batchAddMark(powerMeterDefectMarkInDOList);
                log.info("#PowerDefectServiceImpl.result# businessParamInfo={}, mark end", businessParamInfo);
            }
            powerMeterFlightDetailDefectManager.taskEnd(businessParamInfo.getDetailId());
        }
    }
}
