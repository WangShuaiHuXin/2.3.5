package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.geoai.common.web.util.ResultUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.enums.RoleIdenValueEnum;
import com.imapcloud.nest.utils.SSHHelper;
import com.imapcloud.nest.v2.common.constant.UosConstants;
import com.imapcloud.nest.v2.common.enums.*;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerMeterFlightDetailInfraredInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerMeterInfraredRecordInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.RocketmqInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.UdaAnalysisInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerComponentRuleInfraredInfoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerMeterFlightDataOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerMeterFlightDetailInfraredOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerMeterInfraredRecordOutDO;
import com.imapcloud.nest.v2.manager.feign.UdaAnalysisServiceClient;
import com.imapcloud.nest.v2.manager.listener.rocketmq.PowerAiTaskResultListener;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.nest.v2.manager.sql.*;
import com.imapcloud.nest.v2.manager.sql.Impl.PowerMeterFlightDetailInfraredManagerImpl;
import com.imapcloud.nest.v2.service.PowerInfraredService;
import com.imapcloud.nest.v2.service.PowerMeterDataService;
import com.imapcloud.nest.v2.service.PowerTaskService;
import com.imapcloud.nest.v2.service.RocketmqService;
import com.imapcloud.nest.v2.service.dto.TaskInfoDTO;
import com.imapcloud.nest.v2.service.dto.in.PowerInfraredInDTO;
import com.imapcloud.nest.v2.service.dto.out.AIAnalysisTaskDataOutDTO;
import com.imapcloud.nest.v2.service.dto.out.PowerInfraredOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 红外服务impl
 *
 * @author boluo
 * @date 2022-12-28
 */
@Slf4j
@Service
public class PowerInfraredServiceImpl implements PowerInfraredService {

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private PowerMeterInfraredRecordManager powerMeterInfraredRecordManager;

    @Resource
    private PowerMeterFlightDetailInfraredManager powerMeterFlightDetailInfraredManager;

    @Resource
    private PowerComponentRuleInfraredInfoManager powerComponentRuleInfraredInfoManager;

    @Resource
    private SSHHelper sshHelper;

    @Resource
    private UdaAnalysisServiceClient udaAnalysisServiceClient;

    @Resource
    private RocketmqService rocketmqService;

    @Resource
    private PowerMeterDataManager powerMeterDataManager;

    @Resource
    private PowerMeterDataService powerMeterDataService;

    @Resource
    private PowerTaskService powerTaskService;

    @Resource
    private FileManager fileManager;

    private static final BigDecimal HUNDRED = new BigDecimal(100);

    private String decimal(BigDecimal decimal) {
        DecimalFormat df = new DecimalFormat("0.00%");
        return df.format(decimal);
    }

    private BigDecimal getDecimal(String percent) {

        percent = percent.replace("%","");
        return new BigDecimal(percent).divide(HUNDRED);
    }

    @Override
    public PowerInfraredOutDTO.PictureOutDTO pictureGet(PowerInfraredInDTO.PictureInDTO powerInfraredInDTO) {

        String pictureUrl;
        if (powerInfraredInDTO.getPictureUrl() != null) {
            pictureUrl = powerInfraredInDTO.getPictureUrl();
        } else {
            // 查询图片信息
            PowerMeterFlightDetailInfraredOutDO powerMeterFlightDetailInfraredOutDO = powerMeterFlightDetailInfraredManager.selectOneByDetailId(powerInfraredInDTO.getDetailId());
            if (powerMeterFlightDetailInfraredOutDO == null) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PROBLEM_WITH_THE_IMAGE_FORMAT.getContent()));
            }
            pictureUrl = powerMeterFlightDetailInfraredOutDO.getInfratedUrl();
        }

        String replace = fileManager.downloadLocal(pictureUrl);
        String shellFormat = "cd /data/dji-measure && /data/dji-measure/djimeasure %s %s %s %s %s";

        String cmd = String.format(shellFormat, replace
                , decimal(powerInfraredInDTO.getSiteX1()), decimal(powerInfraredInDTO.getSiteY1())
                , decimal(powerInfraredInDTO.getSiteX2()), decimal(powerInfraredInDTO.getSiteY2()));

        log.info("#PowerInfraredServiceImpl.pictureGet# cmd={}", cmd);
        String result = sshHelper.exec(cmd);
        log.info("#PowerInfraredServiceImpl.pictureGet# cmd={}, result={}", cmd, result);

        if (StringUtils.isBlank(result)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PROBLEM_UPLOADING_THE_COORDINATES.getContent()));
        }
        if (result.contains("ERROR")) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PROBLEM_WITH_THE_IMAGE_FORMAT.getContent()));
        }
        result = result.replace("\n", "");
        String[] split = result.split(" ");
        log.info("#PowerInfraredServiceImpl.pictureGet# cmd={}, result={}, split={}", cmd, result, JSONUtil.toJsonStr(split));
        if (split.length != 7) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PROBLEM_UPLOADING_THE_COORDINATES.getContent()));
        }

        PowerInfraredOutDTO.PictureOutDTO pictureOutDTO = new PowerInfraredOutDTO.PictureOutDTO();
        pictureOutDTO.setMaxSiteX(getDecimal(split[0]));
        pictureOutDTO.setMaxSiteY(getDecimal(split[1]));
        pictureOutDTO.setMinSiteX(getDecimal(split[2]));
        pictureOutDTO.setMinSiteY(getDecimal(split[3]));
        pictureOutDTO.setMaxTemperature(new BigDecimal(split[4]));
        pictureOutDTO.setMinTemperature(new BigDecimal(split[5]));
        pictureOutDTO.setAvgTemperature(new BigDecimal(split[6]));

        fileManager.deleteLocalFile(replace);
        return pictureOutDTO;
    }

    @Override
    public void checkRunning(List<String> detailIdList, boolean verification) {
        List<PowerMeterFlightDetailInfraredOutDO> outDOList = powerMeterFlightDetailInfraredManager.selectListByDetailId(detailIdList);
        if (CollUtil.isEmpty(outDOList)) {
            return;
        }
        for (PowerMeterFlightDetailInfraredOutDO powerMeterFlightDetailInfraredOutDO : outDOList) {

            if (powerMeterFlightDetailInfraredOutDO.getTemperatureState() == PowerTemperatureStateEnum.IDENTIFY.getCode()) {
                throw new BizException(MessageUtils
                        .getMessage(MessageEnum.GEOAI_UOS_UNABLE_TO_OPERATE_THE_PHOTO_DATA_IN_AI_RECOGNITION.getContent()));
            }

            if (verification && powerMeterFlightDetailInfraredOutDO.getVerificationState() != InspectionVerifyStateEnum.DAIHESHI.getTypeInt()) {
                throw new BizException(MessageUtils
                        .getMessage(MessageEnum.GEOAI_UOS_UNABLE_TO_OPERATE_THE_PHOTO_DATA_IN_VERIFICATION.getContent()));
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String save(PowerInfraredInDTO.SaveInDTO saveInDTO) {

        checkRunning(Lists.newArrayList(saveInDTO.getDetailId()), true);

        PowerMeterInfraredRecordInDO powerMeterInfraredRecordInDO = new PowerMeterInfraredRecordInDO();
        BeanUtils.copyProperties(saveInDTO, powerMeterInfraredRecordInDO);
        powerMeterInfraredRecordInDO.setInfraredRecordId(BizIdUtils.snowflakeIdStr());
        BigDecimal maxTemperature = saveInDTO.getMaxTemperature();
        // 查询旧的红外测温数据
        List<PowerMeterInfraredRecordOutDO> recordOutDOList = powerMeterInfraredRecordManager.selectListByDetailId(saveInDTO.getDetailId());
        if (CollUtil.isNotEmpty(recordOutDOList)) {
            for (PowerMeterInfraredRecordOutDO powerMeterInfraredRecordOutDO : recordOutDOList) {
                if (maxTemperature.compareTo(powerMeterInfraredRecordOutDO.getMaxTemperature()) < 0) {
                    maxTemperature = powerMeterInfraredRecordOutDO.getMaxTemperature();
                }
            }
        }

        powerMeterInfraredRecordManager.insert(powerMeterInfraredRecordInDO);
        updateInfrared(saveInDTO.getDetailId(), saveInDTO.getAccountId(), maxTemperature);
        return powerMeterInfraredRecordInDO.getInfraredRecordId();
    }

    private void updateInfrared(String detailId, String accountId, BigDecimal maxTemperature) {
        // 判定温度
        PowerMeterFlightDetailInfraredOutDO powerMeterFlightDetailInfraredOutDO =
                powerMeterFlightDetailInfraredManager.selectOneByDetailId(detailId);
        if (powerMeterFlightDetailInfraredOutDO == null) {
            return;
        }
        PowerMeterFlightDetailInfraredInDO powerMeterFlightDetailInfraredInDO = new PowerMeterFlightDetailInfraredInDO();
        powerMeterFlightDetailInfraredInDO.setDetailId(detailId);
        if (maxTemperature == null) {
            powerMeterFlightDetailInfraredInDO.setTemperatureState(PowerTemperatureStateEnum.UNMEASURED_TEMPERATURE.getCode());
            powerMeterFlightDetailInfraredInDO.setDeviceState(PowerDeviceStateEnum.GNERMAL_DEFECTS.getCode());
            powerMeterFlightDetailInfraredInDO.setReason(PowerMeterFlightDetailInfraredManagerImpl.SYSTEM_ERROR_REASON);
            powerMeterFlightDetailInfraredManager.updateStat(powerMeterFlightDetailInfraredInDO);
            return;
        }

        powerMeterFlightDetailInfraredInDO.setTemperatureState(PowerTemperatureStateEnum.MEASURED_TEMPERATURE.getCode());
        powerMeterFlightDetailInfraredInDO.setReason("");
        powerMeterFlightDetailInfraredInDO.setAccountId(accountId);
        // 部件库规则
        if (StringUtils.isBlank(powerMeterFlightDetailInfraredOutDO.getComponentId())) {
            powerMeterFlightDetailInfraredInDO.setDeviceState(PowerDeviceStateEnum.GNERMAL_DEFECTS.getCode());
            powerMeterFlightDetailInfraredInDO.setReason("未设置测温规则");
        } else {
            List<PowerComponentRuleInfraredInfoOutDO> infoOutDOList = powerComponentRuleInfraredInfoManager
                    .selectListByComponentIdCollection(Lists.newArrayList(powerMeterFlightDetailInfraredOutDO.getComponentId()));
            if (CollUtil.isEmpty(infoOutDOList)) {
                powerMeterFlightDetailInfraredInDO.setDeviceState(PowerDeviceStateEnum.GNERMAL_DEFECTS.getCode());
                powerMeterFlightDetailInfraredInDO.setReason("未设置测温规则");
            } else {
                infoOutDOList.sort(Comparator.comparing(PowerComponentRuleInfraredInfoOutDO::getThreshold).reversed());
                PowerComponentRuleInfraredInfoOutDO ruleInfraredInfoOutDO = null;
                for (PowerComponentRuleInfraredInfoOutDO powerComponentRuleInfraredInfoOutDO : infoOutDOList) {

                    if (PowerInfraredRuleEnum.compareTo(maxTemperature
                            , powerComponentRuleInfraredInfoOutDO.getInfraredRuleState()
                            , powerComponentRuleInfraredInfoOutDO.getThreshold())) {
                        ruleInfraredInfoOutDO = powerComponentRuleInfraredInfoOutDO;
                        break;
                    }
                }
                if (ruleInfraredInfoOutDO == null) {
                    powerMeterFlightDetailInfraredInDO.setDeviceState(PowerDeviceStateEnum.NORMAL.getCode());
                    powerMeterFlightDetailInfraredInDO.setReason("");
                } else {
                    powerMeterFlightDetailInfraredInDO.setDeviceState(ruleInfraredInfoOutDO.getDeviceState());
                    powerMeterFlightDetailInfraredInDO.setReason(PowerInfraredRuleEnum.reason(ruleInfraredInfoOutDO.getInfraredRuleState(), ruleInfraredInfoOutDO.getThreshold()));
                }
            }
        }
        powerMeterFlightDetailInfraredManager.updateStat(powerMeterFlightDetailInfraredInDO);
    }

    @Override
    public void auto(List<String> detailIdList) {
        for (String detailId: detailIdList) {
            try {
                // 测温
                PowerInfraredInDTO.PictureInDTO pictureInDTO = new PowerInfraredInDTO.PictureInDTO();
                pictureInDTO.setDetailId(detailId);
                pictureInDTO.setSiteX1(new BigDecimal("0.3"));
                pictureInDTO.setSiteX2(new BigDecimal("0.7"));
                pictureInDTO.setSiteY1(new BigDecimal("0.3"));
                pictureInDTO.setSiteY2(new BigDecimal("0.7"));
                PowerInfraredOutDTO.PictureOutDTO pictureOutDTO = this.pictureGet(pictureInDTO);
                log.error("#PowerInfraredServiceImpl.auto# DetailId={}, pictureOutDTO={}", detailId, pictureOutDTO);
                PowerInfraredInDTO.SaveInDTO saveInDTO = toSaveInDTO(pictureOutDTO, pictureInDTO, detailId);
                this.save(saveInDTO);
            } catch (Exception e) {
                log.error("#PowerInfraredServiceImpl.auto# DetailId={}", detailId, e);
            }
        }
    }

    private PowerInfraredInDTO.SaveInDTO toSaveInDTO(PowerInfraredOutDTO.PictureOutDTO pictureOutDTO
            , PowerInfraredInDTO.PictureInDTO pictureInDTO, String detailId) {
        PowerInfraredInDTO.SaveInDTO saveInDTO = new PowerInfraredInDTO.SaveInDTO();
        saveInDTO.setAccountId(UosConstants.SYSTEM_ACCOUNT_ID);
        saveInDTO.setDetailId(detailId);
        saveInDTO.setSiteX1(pictureInDTO.getSiteX1());
        saveInDTO.setSiteX2(pictureInDTO.getSiteX2());
        saveInDTO.setSiteY1(pictureInDTO.getSiteY1());
        saveInDTO.setSiteY2(pictureInDTO.getSiteY2());
        saveInDTO.setMaxTemperature(pictureOutDTO.getMaxTemperature());
        saveInDTO.setMinTemperature(pictureOutDTO.getMinTemperature());
        saveInDTO.setAvgTemperature(pictureOutDTO.getAvgTemperature());
        saveInDTO.setMaxSiteX(pictureOutDTO.getMaxSiteX());
        saveInDTO.setMaxSiteY(pictureOutDTO.getMaxSiteY());
        saveInDTO.setMinSiteX(pictureOutDTO.getMinSiteX());
        saveInDTO.setMinSiteY(pictureOutDTO.getMinSiteY());
        return saveInDTO;
    }

    @Override
    public void result(PowerAiTaskResultListener.MqInfo mqInfo, RocketmqInDO.BusinessParamInfo businessParamInfo) {
        log.info("#PowerInfraredServiceImpl.result# businessParamInfo={}, mqInfo={}", businessParamInfo, mqInfo);
        // 红外
        List<PowerMeterFlightDetailInfraredOutDO> infraredOutDOList = powerMeterFlightDetailInfraredManager
                .queryByDataIdCollection(Lists.newArrayList(businessParamInfo.getDataId()));
        if (CollUtil.isEmpty(infraredOutDOList)) {
            return;
        }
        List<PowerMeterFlightDetailInfraredOutDO> collect = infraredOutDOList.stream()
                .filter(o -> o.getDetailId().equals(businessParamInfo.getDetailId()))
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(collect)) {
            return;
        }
        PowerMeterFlightDetailInfraredOutDO infraredOutDO = collect.get(0);
        log.info("#PowerInfraredServiceImpl.result# businessParamInfo={}, infraredOutDO={}", businessParamInfo, infraredOutDO);
        if (infraredOutDO.getTaskState() == PowerTaskStateEnum.TASK_ING.getCode()
                || infraredOutDO.getTaskState() == PowerTaskStateEnum.TASK_PAUSE.getCode()
                || infraredOutDO.getTaskState() == PowerTaskStateEnum.TASK_PRE.getCode()) {

            PowerAiTaskResultListener.TaskResultInfo taskResultInfoInDTO = mqInfo.getTaskResultInfoInDTO();
            List<PowerMeterInfraredRecordInDO> powerMeterInfraredRecordInDOList = Lists.newLinkedList();
            if (taskResultInfoInDTO.getTaskState() == PowerAiTaskResultListener.TaskStateEnum.UNAUTH.getCode()) {
                // 算法未授权
                powerMeterFlightDetailInfraredManager.taskNoAuth(infraredOutDO.getDetailId());
                return;
            }
            if (taskResultInfoInDTO.getTaskState() == PowerAiTaskResultListener.TaskStateEnum.SUCCESS.getCode()) {

                if (CollUtil.isNotEmpty(taskResultInfoInDTO.getInfoInDTOList())) {
                    for (PowerAiTaskResultListener.MarkInfo markInfo : taskResultInfoInDTO.getInfoInDTOList()) {

                        PowerInfraredInDTO.PictureInDTO powerInfraredInDTO = new PowerInfraredInDTO.PictureInDTO();
                        powerInfraredInDTO.setSiteX1(markInfo.getX1());
                        powerInfraredInDTO.setSiteX2(markInfo.getX2());
                        powerInfraredInDTO.setSiteY1(markInfo.getY1());
                        powerInfraredInDTO.setSiteY2(markInfo.getY2());
                        powerInfraredInDTO.setPictureUrl(infraredOutDO.getInfratedUrl());
                        long start = System.currentTimeMillis();
                        PowerInfraredOutDTO.PictureOutDTO pictureOutDTO = this.pictureGet(powerInfraredInDTO);
                        log.info("#PowerInfraredServiceImpl.result# runTime={}", (System.currentTimeMillis() - start));
                        PowerMeterInfraredRecordInDO powerMeterInfraredRecordInDO = new PowerMeterInfraredRecordInDO();
                        powerMeterInfraredRecordInDO.setInfraredRecordId(BizIdUtils.snowflakeIdStr());
                        powerMeterInfraredRecordInDO.setDetailId(infraredOutDO.getDetailId());
                        powerMeterInfraredRecordInDO.setMaxTemperature(pictureOutDTO.getMaxTemperature());
                        powerMeterInfraredRecordInDO.setMinTemperature(pictureOutDTO.getMinTemperature());
                        powerMeterInfraredRecordInDO.setAvgTemperature(pictureOutDTO.getAvgTemperature());
                        powerMeterInfraredRecordInDO.setSiteX1(powerInfraredInDTO.getSiteX1());
                        powerMeterInfraredRecordInDO.setSiteY1(powerInfraredInDTO.getSiteY1());
                        powerMeterInfraredRecordInDO.setSiteX2(powerInfraredInDTO.getSiteX2());
                        powerMeterInfraredRecordInDO.setSiteY2(powerInfraredInDTO.getSiteY2());
                        powerMeterInfraredRecordInDO.setMaxSiteX(pictureOutDTO.getMaxSiteX());
                        powerMeterInfraredRecordInDO.setMaxSiteY(pictureOutDTO.getMaxSiteY());
                        powerMeterInfraredRecordInDO.setMinSiteX(pictureOutDTO.getMinSiteX());
                        powerMeterInfraredRecordInDO.setMinSiteY(pictureOutDTO.getMinSiteY());
                        powerMeterInfraredRecordInDO.setAccountId(UosConstants.SYSTEM_ACCOUNT_ID);
                        powerMeterInfraredRecordInDOList.add(powerMeterInfraredRecordInDO);
                    }
                }
                // 自动巡检结果，先清空旧数据，再添加
                powerMeterInfraredRecordManager.deleteByDetailId(infraredOutDO.getDetailId(), UosConstants.SYSTEM_ACCOUNT_ID);
                powerMeterInfraredRecordManager.batchSave(powerMeterInfraredRecordInDOList);
            }
            updateTaskState(infraredOutDO.getDetailId(), powerMeterInfraredRecordInDOList);
        }
    }

    private void updateTaskState(String detailId, List<PowerMeterInfraredRecordInDO> powerMeterInfraredRecordInDOList) {

        log.info("#PowerInfraredServiceImpl.updateTaskStateEnd# detailId={}", detailId);
        PowerMeterInfraredRecordInDO powerMeterInfraredRecordInDO = powerMeterInfraredRecordInDOList.stream()
                .max(Comparator.comparing(PowerMeterInfraredRecordInDO::getMaxTemperature)).orElse(null);
        BigDecimal maxTemperature = null;
        if (powerMeterInfraredRecordInDO != null) {
            maxTemperature = powerMeterInfraredRecordInDO.getMaxTemperature();
        }
        log.info("#PowerInfraredServiceImpl.updateTaskStateEnd# detailId={}, maxTemperature={}", detailId, maxTemperature);
        updateInfrared(detailId, UosConstants.SYSTEM_ACCOUNT_ID, maxTemperature);
        powerMeterFlightDetailInfraredManager.updateTaskState(Lists.newArrayList(detailId), PowerTaskStateEnum.TASK_END);
    }

    @Override
    public void updateTaskStatePre(List<String> detailIdList, String dataId) {

        powerMeterFlightDetailInfraredManager.taskPre(detailIdList);

        // 取消暂停
        powerMeterFlightDetailInfraredManager.taskUnpause(dataId);
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
    public void infrared(RocketmqInDO.TaskMqInfo taskMqInfo) {

        log.info("#PowerInfraredServiceImpl.infrared# taskMqInfo={}", taskMqInfo);
        List<PowerMeterFlightDetailInfraredOutDO> infraredOutDOS = powerMeterFlightDetailInfraredManager
                .queryByDataIdCollection(Lists.newArrayList(taskMqInfo.getDataId()));

        // 任务是否暂停
        List<PowerMeterFlightDetailInfraredOutDO> pauseList = infraredOutDOS.stream()
                .filter(infraredOutDO -> infraredOutDO.getTaskState() == PowerTaskStateEnum.TASK_PAUSE.getCode())
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(pauseList)) {

            log.info("#PowerInfraredServiceImpl.infrared# pause taskMqInfo={}", taskMqInfo);
            sendWs(taskMqInfo.getDataId(), false);
            rocketmqService.sendTask(taskMqInfo);
            return;
        }
        // 任务是否终止
        List<PowerMeterFlightDetailInfraredOutDO> stopList = infraredOutDOS.stream()
                .filter(infraredOutDO -> infraredOutDO.getTaskState() == PowerTaskStateEnum.TASK_STOP.getCode())
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(stopList)) {
            log.info("#PowerInfraredServiceImpl.infrared# stop taskMqInfo={}", taskMqInfo);
            taskInit(taskMqInfo.getDataId());
            return;
        }
        // 正在执行的任务
        List<PowerMeterFlightDetailInfraredOutDO> runList = infraredOutDOS.stream()
                .filter(infraredOutDO -> infraredOutDO.getTaskState() == PowerTaskStateEnum.TASK_ING.getCode())
                .collect(Collectors.toList());

        // 正在执行的任务是否超时
        if (CollUtil.isNotEmpty(runList)) {

            LocalDateTime now = LocalDateTime.now();
            List<String> timeoutDetailIdList = Lists.newLinkedList();
            for (PowerMeterFlightDetailInfraredOutDO powerMeterFlightDetailInfraredOutDO : runList) {
                if (powerMeterFlightDetailInfraredOutDO.getTaskPictureStartTime() == null
                        || powerMeterFlightDetailInfraredOutDO.getTaskPictureStartTime()
                        .plusSeconds(geoaiUosProperties.getRocketmq().getTaskTimeoutSecond()).isBefore(now)) {
                    timeoutDetailIdList.add(powerMeterFlightDetailInfraredOutDO.getDetailId());
                }
            }
            if (CollUtil.isNotEmpty(timeoutDetailIdList)) {
                powerMeterFlightDetailInfraredManager.taskTimeout(timeoutDetailIdList);
            }
        }
        if (powerTaskService.taskEmpty(taskMqInfo)) {
            sendWs(taskMqInfo.getDataId(), false);
            // 任务已全部发送到UDA
            if (CollUtil.isNotEmpty(runList)) {
                rocketmqService.sendTask(taskMqInfo);
            } else {
                taskInit(taskMqInfo.getDataId());
            }
            return;
        }
        // 任务数判定
        if (runList.size() >= geoaiUosProperties.getRocketmq().getMissionTaskMaxNum()) {

            log.info("#PowerInfraredServiceImpl.infrared# runSize={} taskMqInfo={}", runList.size(), taskMqInfo);
            sendWs(taskMqInfo.getDataId(), false);
            rocketmqService.sendTask(taskMqInfo);
            return;
        }

        // 获取一张图片执行
        TaskInfoDTO.DetailInfoDTO detailInfoDTO = powerTaskService.removeOneTaskPicture(taskMqInfo);
        log.info("#PowerInfraredServiceImpl.infrared# detailInfoDTO={}", detailInfoDTO);
        if (detailInfoDTO == null) {
            return;
        }
        // 本次需要执行的任务
        PowerMeterFlightDetailInfraredOutDO runInfraredOutDO = null;
        for (PowerMeterFlightDetailInfraredOutDO infraredOutDO : infraredOutDOS) {
            if (detailInfoDTO.getDetailId().equals(infraredOutDO.getDetailId())) {
                runInfraredOutDO = infraredOutDO;
                break;
            }
        }

        if (runInfraredOutDO != null
                && runInfraredOutDO.getTaskState() == PowerTaskStateEnum.TASK_PRE.getCode()) {
            // 准备识别
            powerMeterFlightDetailInfraredManager.taskIng(runInfraredOutDO.getDetailId());
            // 调用UDA发送识别任务
            addTask(taskMqInfo, runInfraredOutDO, detailInfoDTO);
        }
        log.info("#PowerInfraredServiceImpl.infrared# detailInfoDTO={} end", detailInfoDTO);
        sendWs(taskMqInfo.getDataId(), false);
        rocketmqService.sendTask(taskMqInfo);
    }

    private PowerAiTaskResultListener.MqInfo toMqInfo(RocketmqInDO.TaskMqInfo taskMqInfo, String pictureUrl, String orgCode) {
        PowerAiTaskResultListener.MqInfo mqInfo = new PowerAiTaskResultListener.MqInfo();
        mqInfo.setOrgCode(orgCode);
        mqInfo.setTaskId(BizIdUtils.snowflakeIdStr());
        mqInfo.setPictureUrl(pictureUrl);
        mqInfo.setBusinessParam("");
        PowerAiTaskResultListener.TaskResultInfo taskResultInfo = new PowerAiTaskResultListener.TaskResultInfo();
        taskResultInfo.setTaskState(PowerAiTaskResultListener.TaskStateEnum.SUCCESS.getCode());
        List<PowerAiTaskResultListener.MarkInfo> infoInDTOList = Lists.newLinkedList();
        taskResultInfo.setInfoInDTOList(infoInDTOList);
        mqInfo.setTaskResultInfoInDTO(taskResultInfo);

        PowerAiTaskResultListener.MarkInfo markInfo = new PowerAiTaskResultListener.MarkInfo();
        markInfo.setFunctionId("");
        markInfo.setProblemTypeId("");
        markInfo.setProblemTypeName("");
        markInfo.setX1(new BigDecimal("0.3"));
        markInfo.setY1(new BigDecimal("0.3"));
        markInfo.setX2(new BigDecimal("0.7"));
        markInfo.setY2(new BigDecimal("0.7"));
        infoInDTOList.add(markInfo);

        return mqInfo;
    }

    private void addTask(RocketmqInDO.TaskMqInfo taskMqInfo, PowerMeterFlightDetailInfraredOutDO runInfraredOutDO
            , TaskInfoDTO.DetailInfoDTO detailInfoDTO) {

        try {
            String orgCode = detailInfoDTO.getOrgCode();
            List<String> functionIdList = detailInfoDTO.getFunctionIdList();
            String picAccessPrefix = geoaiUosProperties.getAnalysis().getAiPicAccessHost();
            String pictureUrl = String.format("%s%s", picAccessPrefix, runInfraredOutDO.getInfratedUrl());

            RocketmqInDO.BusinessParamInfo businessParamInfo = new RocketmqInDO.BusinessParamInfo();
            businessParamInfo.setDetailId(runInfraredOutDO.getDetailId());
            businessParamInfo.setDataId(runInfraredOutDO.getDataId());
            if (CollUtil.isEmpty(functionIdList)) {

                // 没有识别算法，使用默认识别
                PowerAiTaskResultListener.MqInfo mqInfo = toMqInfo(taskMqInfo, pictureUrl, orgCode);
                this.result(mqInfo, businessParamInfo);
                return;
            }

            UdaAnalysisInDO.AddPictureTaskInDO addPictureTaskInDO = new UdaAnalysisInDO.AddPictureTaskInDO();
            addPictureTaskInDO.setOrgCode(orgCode);
            List<Long> collect = functionIdList.stream().map(Long::parseLong).collect(Collectors.toList());
            addPictureTaskInDO.setFunctionIdList(collect);
            addPictureTaskInDO.setPictureUrl(pictureUrl);
            addPictureTaskInDO.setBusinessParam(JSONUtil.toJsonStr(businessParamInfo));

            log.info("#PowerAiTaskListener.addTask# addPictureTaskInDO={}", addPictureTaskInDO);
            Result<String> stringResult = udaAnalysisServiceClient.addPictureTask(addPictureTaskInDO);
            log.info("#PowerAiTaskListener.addTask# addPictureTaskInDO={}, stringResult={}", addPictureTaskInDO, stringResult);
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
            log.error("#PowerAiTaskListener.addTask# taskMqInfo={}", taskMqInfo, e);
        }
    }

    @Override
    public void taskChange(PowerMeterFlightDataOutDO powerMeterFlightDataOutDO, PowerTaskStateEnum powerTaskStateEnum) {

        String dataId = powerMeterFlightDataOutDO.getDataId();
        List<String> dataIdList = Lists.newArrayList(dataId);
        List<PowerMeterFlightDetailInfraredOutDO> infraredOutDOList = null;
        // 更新
        if (PowerTaskStateEnum.TASK_INIT == powerTaskStateEnum) {
            infraredOutDOList = powerMeterFlightDetailInfraredManager.queryByDataIdCollection(dataIdList);
            powerMeterFlightDetailInfraredManager.taskInit(dataId);
        } else if (PowerTaskStateEnum.TASK_PAUSE == powerTaskStateEnum) {
            powerMeterFlightDetailInfraredManager.taskPause(dataId);
            infraredOutDOList = powerMeterFlightDetailInfraredManager.queryByDataIdCollection(dataIdList);
        } else if (PowerTaskStateEnum.TASK_PRE == powerTaskStateEnum) {
            powerMeterFlightDetailInfraredManager.taskUnpause(dataId);
            infraredOutDOList = powerMeterFlightDetailInfraredManager.queryByDataIdCollection(dataIdList);
        } else if (PowerTaskStateEnum.TASK_STOP == powerTaskStateEnum) {
            // 终止的话，需要先取之前的状态（终止会初始化任务状态）
            infraredOutDOList = powerMeterFlightDetailInfraredManager.queryByDataIdCollection(dataIdList);
            for (PowerMeterFlightDetailInfraredOutDO powerMeterFlightDetailInfraredOutDO : infraredOutDOList) {
                if (powerMeterFlightDetailInfraredOutDO.getTaskState() == PowerTaskStateEnum.TASK_PRE.getCode() ||
                        powerMeterFlightDetailInfraredOutDO.getTaskState() == PowerTaskStateEnum.TASK_ING.getCode() ||
                        powerMeterFlightDetailInfraredOutDO.getTaskState() == PowerTaskStateEnum.TASK_PAUSE.getCode()) {
                    powerMeterFlightDetailInfraredOutDO.setTaskState(PowerTaskStateEnum.TASK_STOP.getCode());
                }
            }
            powerMeterFlightDetailInfraredManager.taskStop(dataId);
        }
        send(powerMeterFlightDataOutDO, infraredOutDOList, false, powerTaskStateEnum);
    }

    @Override
    public void manualSendWs(String orgCode) {

        List<String> stringList = powerMeterFlightDetailInfraredManager.selectDataIdByOrgCode(orgCode);
        for (String s : stringList) {
            this.sendWs(s, false);
        }
    }

    @Override
    public void sendWs(String dataId, boolean initialized) {

        log.info("#PowerInfraredServiceImpl.sendWs# dataId={}", dataId);
        List<String> dataIdList = Lists.newArrayList(dataId);
        List<PowerMeterFlightDataOutDO> dataOutDOList = powerMeterDataManager.selectListByDataIdList(dataIdList);
        if (CollUtil.isEmpty(dataOutDOList)) {
            return;
        }
        PowerMeterFlightDataOutDO powerMeterFlightDataOutDO = dataOutDOList.get(0);
        List<PowerMeterFlightDetailInfraredOutDO> infraredOutDOList = powerMeterFlightDetailInfraredManager
                .queryByDataIdCollection(dataIdList);
        log.info("#PowerInfraredServiceImpl.sendWs# dataId={}, infraredOutDOList={}", dataId, infraredOutDOList);
        send(powerMeterFlightDataOutDO, infraredOutDOList, initialized, null);
    }

    private void send(PowerMeterFlightDataOutDO powerMeterFlightDataOutDO,
                      List<PowerMeterFlightDetailInfraredOutDO> infraredOutDOList,
                      boolean initialized, PowerTaskStateEnum powerTaskStateEnum) {

        infraredOutDOList = infraredOutDOList.stream()
                .filter(bean -> bean.getTaskState() != PowerTaskStateEnum.TASK_INIT.getCode())
                .collect(Collectors.toList());
        log.info("#PowerInfraredServiceImpl.send# infraredOutDOList={}", infraredOutDOList);
        if (CollUtil.isEmpty(infraredOutDOList)) {
            return;
        }
        // 查询任务信息
        TaskInfoDTO taskInfoDTO = powerTaskService.getTaskInfoByDataId(powerMeterFlightDataOutDO.getDataId());
        if (taskInfoDTO == null) {
            log.info("#PowerInfraredServiceImpl.send# task not exists");
            return;
        }

        LocalDateTime taskStartTime = LocalDateTime.now();
        int successCount = 0;
        int failureCount = 0;
        int runningCount = 0;
        int pauseCount = 0;
        for (PowerMeterFlightDetailInfraredOutDO powerMeterFlightDetailInfraredOutDO : infraredOutDOList) {

            if (powerMeterFlightDetailInfraredOutDO.getTaskStartTime() != null &&
                    taskStartTime.isAfter(powerMeterFlightDetailInfraredOutDO.getTaskStartTime())) {
                taskStartTime = powerMeterFlightDetailInfraredOutDO.getTaskStartTime();
            }
            if (powerMeterFlightDetailInfraredOutDO.getTaskState() == PowerTaskStateEnum.TASK_END.getCode()) {
                successCount ++;
            } else if (powerMeterFlightDetailInfraredOutDO.getTaskState() == PowerTaskStateEnum.TASK_PAUSE.getCode()) {
                pauseCount ++;
            } else if (powerMeterFlightDetailInfraredOutDO.getTaskState() == PowerTaskStateEnum.TASK_PRE.getCode() ||
                    powerMeterFlightDetailInfraredOutDO.getTaskState() == PowerTaskStateEnum.TASK_ING.getCode()) {
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

        aiAnalysisTaskDataOutDTO.setImageTotalCounts(infraredOutDOList.size());
        aiAnalysisTaskDataOutDTO.setImageSuccessCounts(successCount);
        aiAnalysisTaskDataOutDTO.setImageFailedCounts(failureCount);
        aiAnalysisTaskDataOutDTO.setCostSecs((int) ((System.currentTimeMillis() - taskStart) / 1000));
        aiAnalysisTaskDataOutDTO.setCenterBaseId(powerMeterFlightDataOutDO.getDataId());
        aiAnalysisTaskDataOutDTO.setTaskId(String.valueOf(powerMeterFlightDataOutDO.getTaskId()));
        aiAnalysisTaskDataOutDTO.setTagName(powerMeterFlightDataOutDO.getTagName());
        aiAnalysisTaskDataOutDTO.setInitialized(initialized);
        aiAnalysisTaskDataOutDTO.setAuto(taskInfoDTO.getSystem());
        aiAnalysisTaskDataOutDTO.setAiTaskType(AIAnalysisTaskTypeEnum.INFRARED.getType());
        List<AIAnalysisTaskDataOutDTO.AnalysisPhotoRecord> changedRecords = Lists.newArrayList();
        aiAnalysisTaskDataOutDTO.setChangedRecords(changedRecords);
        for (PowerMeterFlightDetailInfraredOutDO powerMeterFlightDetailInfraredOutDO : infraredOutDOList) {
            AIAnalysisTaskDataOutDTO.AnalysisPhotoRecord analysisPhotoRecord = new AIAnalysisTaskDataOutDTO.AnalysisPhotoRecord();
            analysisPhotoRecord.setRecordId(powerMeterFlightDetailInfraredOutDO.getDetailId());
            analysisPhotoRecord.setPhotoName(powerMeterFlightDetailInfraredOutDO.getPhotoName());
            analysisPhotoRecord.setState(powerMeterFlightDetailInfraredOutDO.getTaskState());
            changedRecords.add(analysisPhotoRecord);
        }
        log.info("#PowerInfraredServiceImpl.send# aiAnalysisTaskDataOutDTO={}", aiAnalysisTaskDataOutDTO);
        powerMeterDataService.sendWs(aiAnalysisTaskDataOutDTO, taskInfoDTO.getAccountIdSet());
    }

    @Override
    public void delete(String infraredRecordId) {

        PowerMeterInfraredRecordOutDO recordOutDO = powerMeterInfraredRecordManager
                .selectListByInfraredRecordId(infraredRecordId);
        if (recordOutDO == null) {
            return;
        }
        List<String> detailIdList = Lists.newArrayList(recordOutDO.getDetailId());
        checkRunning(detailIdList, true);
        powerMeterInfraredRecordManager.deleteByInfraredRecordId(infraredRecordId);

        // 查询记录，更新图片状态
        List<PowerMeterInfraredRecordOutDO> recordOutDOList = powerMeterInfraredRecordManager.selectListByDetailIds(detailIdList);
        if (CollUtil.isEmpty(recordOutDOList)) {

            powerMeterFlightDetailInfraredManager.init(detailIdList);
        } else {
            PowerMeterInfraredRecordOutDO outDO = recordOutDOList.stream()
                    .max(Comparator.comparing(PowerMeterInfraredRecordOutDO::getMaxTemperature)).orElse(null);
            this.updateInfrared(recordOutDO.getDetailId()
                    , UosConstants.SYSTEM_ACCOUNT_ID, outDO == null ? null : outDO.getMaxTemperature());
        }
    }
}
