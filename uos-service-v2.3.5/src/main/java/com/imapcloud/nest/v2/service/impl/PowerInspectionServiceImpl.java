package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.constant.SymbolConstants;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.utils.MinIoUnit;
import com.imapcloud.nest.v2.common.enums.*;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.common.utils.DrawImageUtils;
import com.imapcloud.nest.v2.dao.entity.PowerEquipmentLegerInfoEntity;
import com.imapcloud.nest.v2.dao.entity.PowerInspectionReportValueRelEntity;
import com.imapcloud.nest.v2.dao.entity.PowerMeterFlightDetailEntity;
import com.imapcloud.nest.v2.dao.entity.PowerMeterReadingValueEntity;
import com.imapcloud.nest.v2.dao.po.in.PowerInspcetionReportInfoPO;
import com.imapcloud.nest.v2.manager.dataobj.in.CommonFileInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerHomeAlarmStatisticsInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerInspectionReportInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerInspectionReportValueRelInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.*;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.nest.v2.manager.rest.UploadManager;
import com.imapcloud.nest.v2.manager.sql.*;
import com.imapcloud.nest.v2.service.PowerDefectService;
import com.imapcloud.nest.v2.service.PowerInfraredService;
import com.imapcloud.nest.v2.service.PowerInspectionService;
import com.imapcloud.nest.v2.service.dto.in.InspectionInfraredVerificationInDTO;
import com.imapcloud.nest.v2.service.dto.in.InspectionQueryPageInDTO;
import com.imapcloud.nest.v2.service.dto.in.InspectionVerificationBatchInDTO;
import com.imapcloud.nest.v2.service.dto.out.InspectionQueryPageOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PowerInspectionServiceImpl implements PowerInspectionService {


    @Resource
    private PowerMeterDataManager powerMeterDataManager;

    @Resource
    private PowerEquipmentLegerInfoManager powerEquipmentLegerInfoManager;

    @Resource
    private PowerInspectionReportManager powerInspectionReportManager;

    @Resource
    private PowerMeterInfraredRecordManager powerMeterInfraredRecordManager;

    @Resource
    private PowerMeterFlightDetailInfraredManager powerMeterFlightDetailInfraredManager;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private MissionPhotoManager missionPhotoManager;

    @Resource
    private PowerInspectionReportValueRelManager powerInspectionReportValueRelManager;

    @Resource
    private PowerMeterFlightDetailDefectManager powerMeterFlightDetailDefectManager;

    @Resource
    private PowerMeterDefectMarkManager powerMeterDefectMarkManager;

    @Resource
    private ExecutorService executorService;

    @Resource
    private PowerInfraredService powerInfraredService;

    @Resource
    private PowerDefectService powerDefectService;

    @Resource
    private FileManager fileManager;

    @Resource
    private UploadManager uploadManager;

    @Override
    public boolean verificationBatch(InspectionVerificationBatchInDTO inDTO) {
        Map<String, Object> resultMap = new HashMap<>();
        //批量查询需要核实的表计读数数据
        List<String> batchIds = inDTO.getIds();
        //表计核实
        if (inDTO.getAnalysisType().equals(PowerDsicernTypesEnum.BIAOJI.getType())) {
            resultMap = MeterReadingVerification(inDTO);
            powerMeterDataManager.updatePushState(batchIds, inDTO.getVerificationStatus().toString());
            saveMap(resultMap);
        }
        //红外
        if (inDTO.getAnalysisType().equals(PowerDsicernTypesEnum.HONGWAI.getType())) {
            resultMap = InfraredReadingVerification(inDTO);
            saveMap(resultMap);
            int i = powerMeterFlightDetailInfraredManager.updatePushState(batchIds, inDTO.getVerificationStatus().toString());
            if(i>0) {
                CompletableFuture.runAsync(() -> {
                    syncPhoto(inDTO);
                }, executorService);
            }
        }
        //缺陷
        if (inDTO.getAnalysisType().equals(PowerDsicernTypesEnum.QUEXIAN.getType())) {
            if (InspectionVerifyStateEnum.YIHESHI.getType().equals(inDTO.getVerificationStatus().toString())) {
                resultMap = DefectRecoVerification(inDTO);
                saveMap(resultMap);
            }
            powerMeterFlightDetailDefectManager.updatePushState(batchIds, inDTO.getVerificationStatus(), TrustedAccessTracerHolder.get().getAccountId());
            CompletableFuture.runAsync(() -> {
                syncDefectPhoto(inDTO);
            }, executorService);
        }
        return true;
    }

    public void syncDefectPhoto(InspectionVerificationBatchInDTO inDTO) {
        List<PowerMeterFlightDetailDefectOutDO> powerMeterFlightDetailDefectOutDOS = powerMeterFlightDetailDefectManager.selectListByDetailIdList(inDTO.getIds());
        Optional<List<PowerMeterFlightDetailDefectOutDO>> optional = Optional.ofNullable(powerMeterFlightDetailDefectOutDOS);
        optional.map(items -> {
            List<PowerMeterDefectMarkOutDO> powerMeterDefectMarkOutDOS = powerMeterDefectMarkManager.selectListByDetailIdList(inDTO.getIds());
            Map<String, List<PowerMeterDefectMarkOutDO>> markMap = powerMeterDefectMarkOutDOS.stream().collect(Collectors.groupingBy(PowerMeterDefectMarkOutDO::getDetailId));
            items.stream().forEach(item -> {
                if (CollectionUtil.isNotEmpty(powerMeterDefectMarkOutDOS) && CollectionUtil.isNotEmpty(markMap.get(item.getDetailId()))) {
                    //需要更新的数据
                    String detailId = item.getDetailId();
                    //需要核实的标注
                    List<PowerMeterDefectMarkOutDO> powerMeterDefectMarkOutDO = markMap.get(item.getDetailId());
                    Map<String, LocalDateTime> shootTimeMap = powerMeterFlightDetailDefectOutDOS.stream().collect(Collectors.toMap(PowerMeterFlightDetailDefectOutDO::getDetailId, PowerMeterFlightDetailDefectOutDO::getShootingTime));
                    try(InputStream inputSteam = fileManager.getInputSteam(item.getPictureUrl())){
//                        String descPath = DrawImageUtils.drawImgsDefect(powerMeterDefectMarkOutDO, item.getPictureUrl(), geoaiUosProperties.getStore().getOriginPath(), shootTimeMap.get(item.getDetailId()));
                        if(Objects.nonNull(inputSteam)){
                            String filenameExtension = org.springframework.util.StringUtils.getFilenameExtension(item.getPictureUrl());
                            try(InputStream is = DrawImageUtils.drawDefectImage(powerMeterDefectMarkOutDO, inputSteam, filenameExtension, shootTimeMap.get(item.getDetailId()))){
                                if (Objects.nonNull(is)) {
                                    CommonFileInDO commonFileInDO = new CommonFileInDO();
                                    commonFileInDO.setFileName(BizIdUtils.snowflakeIdStr());
                                    commonFileInDO.setInputStream(is);
                                    Optional<FileStorageOutDO> storage = uploadManager.uploadFile(commonFileInDO);
                                    storage.ifPresent(r -> {
                                        powerInspectionReportManager.fixUrl(detailId, r.getStoragePath() + SymbolConstants.SLASH_LEFT + r.getFilename());
                                    });
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    powerInspectionReportManager.fixUrl(item.getDetailId(), item.getPictureUrl());
                }
            });
            return "";
        });
    }

    private void saveMap(Map<String, Object> resultMap) {
        List<PowerInspectionReportInfoInDO> reportInfoInDOS = (List<PowerInspectionReportInfoInDO>) resultMap.get("report");
        List<PowerInspectionReportValueRelInDO> resultList = (List<PowerInspectionReportValueRelInDO>) resultMap.get("rel");
        if (ObjectUtils.isNotEmpty(reportInfoInDOS)) {
            powerInspectionReportManager.saveBatch(reportInfoInDOS);
        }
        if (ObjectUtils.isNotEmpty(resultList)) {
            powerInspectionReportValueRelManager.saveBatch(resultList);
        }
    }

    private Map<String, Object> DefectRecoVerification(InspectionVerificationBatchInDTO inDTO) {
        Map<String, Object> map = new HashMap<>();
        if (inDTO.getVerificationStatus().toString().equals(InspectionVerifyStateEnum.YIHESHI.getType())) {
            //查询缺陷识别记录
            List<PowerMeterFlightDetailDefectOutDO> powerMeterFlightDetailDefectOutDOS = powerMeterFlightDetailDefectManager.selectListByDetailIdList(inDTO.getIds());
            List<PowerInspectionReportInfoInDO> reportInfoInDOS = new ArrayList<>();
            Set<String> collect = powerMeterFlightDetailDefectOutDOS.stream().map(PowerMeterFlightDetailDefectOutDO::getPmsId).collect(Collectors.toSet());
            List<PowerEquipmentLegerInfoEntity> powerEquipmentLegerInfoEntities = powerEquipmentLegerInfoManager.queryEquipmentByPmsIdsAndOrg(collect, powerMeterFlightDetailDefectOutDOS.get(0).getOrgCode());
            Map<String, PowerEquipmentLegerInfoEntity> equipmentLegerInfoEntityMap = powerEquipmentLegerInfoEntities.stream().collect(Collectors.toMap(PowerEquipmentLegerInfoEntity::getPmsId, q -> q));
            powerDefectService.checkRunning(inDTO.getIds(), true);
            for (PowerMeterFlightDetailDefectOutDO outDO : powerMeterFlightDetailDefectOutDOS) {
                PowerInspectionReportInfoInDO inDO = new PowerInspectionReportInfoInDO();
                inDO.setOrgCode(outDO.getOrgCode());
                inDO.setDeleted(false);
                inDO.setInspectionUrl(outDO.getPictureUrl());
                //非问题的，需要设置设备状态为正常
                if (!PowerDeviceStateEnum.isDefect(outDO.getDefectState())) {
                    inDO.setScreenshootUrl(outDO.getPictureUrl());
                    inDO.setInspectionConclusion(PowerDeviceStateEnum.NORMAL.getCode());
                } else {
                    inDO.setInspectionConclusion(outDO.getDeviceState());
                }
                //没缩略图的设置为原图
                if (StringUtils.isNotEmpty(outDO.getThumbnailUrl())) {
                    inDO.setThumbnailUrl(outDO.getThumbnailUrl());
                } else {
                    inDO.setThumbnailUrl(outDO.getPictureUrl());
                }
                inDO.setInspectionPhotoId(outDO.getPhotoId());
                inDO.setComponentId(outDO.getComponentId());
                inDO.setInsepctionType(PowerDsicernTypesEnum.QUEXIAN.getType());
                inDO.setAlarmReason(outDO.getReason());
                inDO.setPhotographyTime(outDO.getShootingTime());
                inDO.setRegionRelId(outDO.getDetailId());
                inDO.setComponentName(outDO.getComponentName());
                if (equipmentLegerInfoEntityMap.containsKey(outDO.getPmsId())) {
                    PowerEquipmentLegerInfoEntity powerEquipmentLegerInfoEntity = equipmentLegerInfoEntityMap.get(outDO.getPmsId());
                    inDO.setEquipmentId(powerEquipmentLegerInfoEntity.getEquipmentId());
                    inDO.setEquipmentName(powerEquipmentLegerInfoEntity.getEquipmentName());
                    inDO.setEquipmentType(powerEquipmentLegerInfoEntity.getEquipmentType());
                    inDO.setSpacingUnitName(powerEquipmentLegerInfoEntity.getSpacingUnitName());
                    inDO.setVoltageName(powerEquipmentLegerInfoEntity.getVoltageLevel());
                }
                inDO.setCreatorId(TrustedAccessTracerHolder.get().getAccountId());
                inDO.setModifierId(TrustedAccessTracerHolder.get().getAccountId());
                inDO.setCreatedTime(LocalDateTime.now());
                inDO.setModifiedTime(LocalDateTime.now());
                inDO.setInspectionReportId(BizIdUtils.snowflakeIdStr());
                //识别状态当作缺陷识别结果存储
                if (PowerDeviceStateEnum.isDefect(outDO.getDeviceState())) {
                    inDO.setInspectionResult(PowerDefectStateEnum.DEFECT_YES.getMsg());
                } else {
                    inDO.setInspectionResult(PowerDefectStateEnum.DEFECT_NO.getMsg());
                }
                reportInfoInDOS.add(inDO);
            }
            map.put("report", reportInfoInDOS);
        }
        return map;
    }


    private Map<String, Object> InfraredReadingVerification(InspectionVerificationBatchInDTO inDTO) {
        Map<String, Object> map = new HashMap<>();
        List<PowerInspectionReportValueRelInDO> resultList = new ArrayList<>();
        PowerHomeAlarmStatisticsInDO build = PowerHomeAlarmStatisticsInDO.builder().detailIds(inDTO.getIds())
                .verifiyState(InspectionVerifyStateEnum.DAIHESHI.getType()).build();
        List<PowerMeterFlightDetailInfraredOutDO> powerMeterFlightDetailInfraredOutDOS = powerMeterFlightDetailInfraredManager.queryByDeviceStateCondition(build);

        if (CollectionUtil.isEmpty(powerMeterFlightDetailInfraredOutDOS)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERINSPECTIONSERVICEIMPL_005.getContent()));
        }
        powerInfraredService.checkRunning(inDTO.getIds(), true);

        powerMeterFlightDetailInfraredOutDOS.stream().forEach(e -> {
            if (e.getDeviceState().equals(DialDeviceTypeEnum.UNKNOWN.getStatus())) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERINSPECTIONSERVICEIMPL_004.getContent()));
            }
        });

        if (inDTO.getVerificationStatus().toString().equals(InspectionVerifyStateEnum.YIHESHI.getType())) {
            List<PowerMeterInfraredRecordOutDO> powerMeterInfraredRecordOutDOS = powerMeterInfraredRecordManager.selectListByDetailIds(inDTO.getIds());
            Map<String, List<PowerMeterInfraredRecordOutDO>> infraredGroup = powerMeterInfraredRecordOutDOS.stream().collect(Collectors.groupingBy(PowerMeterInfraredRecordOutDO::getDetailId));
            Set<String> collect = powerMeterFlightDetailInfraredOutDOS.stream().map(e -> e.getPmsId()).collect(Collectors.toSet());
            List<PowerEquipmentLegerInfoEntity> powerEquipmentLegerInfoEntities = powerEquipmentLegerInfoManager.queryEquipmentByPmsIdsAndOrg(collect, powerMeterFlightDetailInfraredOutDOS.get(0).getOrgCode());
            Map<String, PowerEquipmentLegerInfoEntity> equipmentLegerInfoEntityMap = powerEquipmentLegerInfoEntities.stream().collect(Collectors.toMap(PowerEquipmentLegerInfoEntity::getPmsId, q -> q));
            List<PowerInspectionReportInfoInDO> result = powerMeterFlightDetailInfraredOutDOS.stream().map(e -> {
                String reportId = BizIdUtils.snowflakeIdStr();
                PowerInspectionReportInfoInDO inDO = new PowerInspectionReportInfoInDO();
                inDO.setOrgCode(e.getOrgCode());
                inDO.setDeleted(false);
                inDO.setInspectionUrl(e.getPictureUrl());
                inDO.setInspectionPhotoId(e.getPhotoId());
                inDO.setComponentId(e.getComponentId());
                if (equipmentLegerInfoEntityMap.containsKey(e.getPmsId())) {
                    PowerEquipmentLegerInfoEntity powerEquipmentLegerInfoEntity = equipmentLegerInfoEntityMap.get(e.getPmsId());
                    inDO.setEquipmentType(powerEquipmentLegerInfoEntity.getEquipmentType());
                    inDO.setSpacingUnitName(powerEquipmentLegerInfoEntity.getSpacingUnitName());
                    inDO.setVoltageName(powerEquipmentLegerInfoEntity.getVoltageLevel());
                    inDO.setEquipmentId(powerEquipmentLegerInfoEntity.getEquipmentId());
                    inDO.setEquipmentName(powerEquipmentLegerInfoEntity.getEquipmentName());
                }
                inDO.setInsepctionType(PowerDsicernTypesEnum.HONGWAI.getType());
                inDO.setInspectionConclusion(e.getDeviceState());
                inDO.setAlarmReason(e.getReason());
                inDO.setPhotographyTime(e.getShootingTime());
                inDO.setRegionRelId(e.getDetailId());
                inDO.setComponentName(e.getComponentName());
                inDO.setCreatorId(TrustedAccessTracerHolder.get().getAccountId());
                inDO.setModifierId(TrustedAccessTracerHolder.get().getAccountId());
                inDO.setCreatedTime(LocalDateTime.now());
                inDO.setModifiedTime(LocalDateTime.now());
                inDO.setInspectionReportId(reportId);
                if (ObjectUtils.isNotEmpty(infraredGroup.get(e.getDetailId()))) {
                    List<PowerMeterInfraredRecordOutDO> infraredRecordOutDOS = infraredGroup.get(e.getDetailId());
                    //对应测温结果记录
                    for (PowerMeterInfraredRecordOutDO infrared : infraredRecordOutDOS) {
                        PowerInspectionReportValueRelInDO relInDO = new PowerInspectionReportValueRelInDO();
                        relInDO.setInspectionReportId(reportId);
                        relInDO.setValueId(infrared.getInfraredRecordId());
                        relInDO.setCreatorId(TrustedAccessTracerHolder.get().getAccountId());
                        relInDO.setModifierId(TrustedAccessTracerHolder.get().getAccountId());
                        relInDO.setCreatedTime(LocalDateTime.now());
                        relInDO.setModifiedTime(LocalDateTime.now());
                        relInDO.setDeleted(false);
                        resultList.add(relInDO);
                    }
                }
                if (StringUtils.isNotEmpty(e.getThumbnailUrl())) {
                    inDO.setThumbnailUrl(e.getThumbnailUrl());
                } else {
                    inDO.setThumbnailUrl(e.getPictureUrl());
                }
                return inDO;
            }).collect(Collectors.toList());
            map.put("report", result);
            map.put("rel", resultList);
        }
        return map;
    }

    private Map<String, Object> MeterReadingVerification(InspectionVerificationBatchInDTO inDTO) {
        List<PowerInspectionReportValueRelInDO> resultList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        List<String> batchIds = inDTO.getIds();
        List<PowerMeterFlightDetailEntity> powerMeterFlightDetailEntities = powerMeterDataManager.selectByDetailIds(batchIds);
        if (CollectionUtil.isEmpty(powerMeterFlightDetailEntities)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERINSPECTIONSERVICEIMPL_002.getContent()));
        }
        if (powerMeterFlightDetailEntities.size() != batchIds.size()) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERINSPECTIONSERVICEIMPL_003.getContent()));

        }
        powerMeterFlightDetailEntities.stream().forEach(e -> {
            if (e.getDeviceState().equals(DialDeviceTypeEnum.UNKNOWN.getStatus())) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERINSPECTIONSERVICEIMPL_004.getContent()));
            }
        });
        Map<Long, MissionPhotoOutDO> missionPhotoOutDOMap = new HashMap<>();
        Set<String> pmsSet = powerMeterFlightDetailEntities.stream().map(PowerMeterFlightDetailEntity::getPmsId).collect(Collectors.toSet());
        List<PowerEquipmentLegerInfoEntity> powerEquipmentLegerInfoEntities = powerEquipmentLegerInfoManager.queryEquipmentByPmsIdsAndOrg(pmsSet, powerMeterFlightDetailEntities.get(0).getOrgCode());
        Map<String, PowerEquipmentLegerInfoEntity> equipmentLegerInfoEntityMap = powerEquipmentLegerInfoEntities.stream().collect(Collectors.toMap(PowerEquipmentLegerInfoEntity::getPmsId, q -> q));
        //封装核实后的巡检报告数据
        List<Integer> photoIds = powerMeterFlightDetailEntities.stream().map(item -> {
            return item.getPhotoId().intValue();
        }).collect(Collectors.toList());
        List<MissionPhotoOutDO> missionPhotoOutDOS = missionPhotoManager.selectByPhotoIdList(photoIds);
        if (CollectionUtil.isNotEmpty(missionPhotoOutDOS)) {
            missionPhotoOutDOMap = missionPhotoOutDOS.stream().collect(Collectors.toMap(MissionPhotoOutDO::getId, q -> q));
        }
        //查询核实结果的reading_value id
        List<PowerMeterReadingValueEntity> powerMeterReadingValueEntities = powerMeterDataManager.selectReadValueByDetailIds(batchIds);
        Map<String, List<PowerMeterReadingValueEntity>> readingMap = powerMeterReadingValueEntities.stream().collect(Collectors.groupingBy(PowerMeterReadingValueEntity::getDetailId));

        if (inDTO.getVerificationStatus().toString().equals(InspectionVerifyStateEnum.YIHESHI.getType())) {
            Map<Long, MissionPhotoOutDO> finalMissionPhotoOutDOMap = missionPhotoOutDOMap;
            List<PowerInspectionReportInfoInDO> reportInfoInDOS = powerMeterFlightDetailEntities.stream().map(e -> {
                String reportId = BizIdUtils.snowflakeIdStr();
                PowerInspectionReportInfoInDO inDO = new PowerInspectionReportInfoInDO();
                BeanUtils.copyProperties(e, inDO);
                inDO.setDeleted(false);
                inDO.setInsepctionType(PowerDsicernTypesEnum.BIAOJI.getType());
                inDO.setInspectionConclusion(e.getDeviceState());
                inDO.setPhotographyTime(e.getShootingTime());
                inDO.setAlarmReason(InspectionResonStatusEnum.getValueByCode(String.valueOf(e.getAlarmReason())));
                inDO.setInspectionReportId(reportId);
                inDO.setRegionRelId(e.getDetailId());
                inDO.setInspectionPhotoId(e.getPhotoId());
                inDO.setCreatorId(TrustedAccessTracerHolder.get().getAccountId());
                inDO.setModifierId(TrustedAccessTracerHolder.get().getAccountId());
                //设备类型另外获取
                if (equipmentLegerInfoEntityMap.containsKey(e.getPmsId())) {
                    PowerEquipmentLegerInfoEntity powerEquipmentLegerInfoEntity = equipmentLegerInfoEntityMap.get(e.getPmsId());
                    inDO.setEquipmentType(powerEquipmentLegerInfoEntity.getEquipmentType());
                    inDO.setSpacingUnitName(powerEquipmentLegerInfoEntity.getSpacingUnitName());
                    inDO.setVoltageName(powerEquipmentLegerInfoEntity.getVoltageLevel());
                    inDO.setEquipmentId(powerEquipmentLegerInfoEntity.getEquipmentId());
                    inDO.setEquipmentName(powerEquipmentLegerInfoEntity.getEquipmentName());
                }
                MissionPhotoOutDO missionPhotoOutDO = finalMissionPhotoOutDOMap.get(e.getPhotoId());
                if (ObjectUtils.isNotEmpty(missionPhotoOutDO)) {
                    inDO.setThumbnailUrl(missionPhotoOutDO.getThumbnailUrl());
                } else {
                    inDO.setThumbnailUrl(e.getOriginalPicUrl());
                }
                inDO.setInspectionUrl(e.getOriginalPicUrl());
                inDO.setScreenshootUrl(e.getDiscernPicUrl());
                List<PowerMeterReadingValueEntity> readingValueEntities = readingMap.get(e.getDetailId());
                if (CollectionUtil.isNotEmpty(readingValueEntities)) {
                    List<PowerInspectionReportValueRelInDO> reportValueRelInDOS = readingValueEntities.stream().map(item -> {
                        PowerInspectionReportValueRelInDO relInDO = new PowerInspectionReportValueRelInDO();
                        relInDO.setInspectionReportId(reportId);
                        relInDO.setValueId(item.getValueId());
                        relInDO.setCreatorId(TrustedAccessTracerHolder.get().getAccountId());
                        relInDO.setModifierId(TrustedAccessTracerHolder.get().getAccountId());
                        relInDO.setCreatedTime(LocalDateTime.now());
                        relInDO.setModifiedTime(LocalDateTime.now());
                        relInDO.setDeleted(false);
                        return relInDO;
                    }).collect(Collectors.toList());
                    resultList.addAll(reportValueRelInDOS);
                }
                return inDO;
            }).collect(Collectors.toList());
            map.put("report", reportInfoInDOS);
            map.put("rel", resultList);
        }
        return map;
    }

    public String drawAchievementReact(InspectionInfraredVerificationInDTO recordOutDO, String url) throws
            IOException {

        //获取到图片文件
        boolean flag = MinIoUnit.objectExists(geoaiUosProperties.getMinio().getBucketName(), url);
        if (!flag) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERINSPECTIONSERVICEIMPL_001.getContent()));
        }
        try(InputStream inputStream = fileManager.getInputSteam(url)){
            if (Objects.isNull(inputStream)) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERINSPECTIONSERVICEIMPL_001.getContent()));
            }
            BufferedImage read = ImageIO.read(inputStream);
            //计算需要画的框的长宽
            //获取图片宽度和长度
            int width = read.getWidth();
            int height = read.getHeight();
            // 计算机绘制的左上角坐标
            BigDecimal leftX = recordOutDO.getX1();
            BigDecimal leftY = recordOutDO.getY1();
            BigDecimal rightX = recordOutDO.getX2();
            BigDecimal rightY = recordOutDO.getY2();
            Double x = leftX.doubleValue() * width;
            Double x1 = rightX.doubleValue() * width;
            Double y = leftY.doubleValue() * height;
            Double y1 = rightY.doubleValue() * height;
            Double v = x1 - x;
            Double v1 = y1 - y;
            Graphics g = read.getGraphics();
            Graphics2D graphics = (Graphics2D) g;
            graphics.setStroke(new BasicStroke(2f));
            graphics.setColor(new Color(255, 255, 255));
            graphics.drawRect(x.intValue(), y.intValue(), v.intValue(), v1.intValue());
            //绘制半透明矩形
            graphics.setColor(new Color(0, 0, 0, 150));
            //计算透明矩形的左上角点位
            int reactX;
            int reactY;
            if (x.intValue() + v.intValue() + 88 > width) {
                //框选位置过于靠近右边边框,不够位置绘制，则文字描述置于左下角
                reactX = x.intValue() - 88;
                reactY = y.intValue() + v1.intValue() - 62;
                graphics.fillRect(reactX - 2, reactY, 88, 62);
            } else {
                reactX = x.intValue() + v.intValue();
                reactY = y.intValue() + v1.intValue() - 62;
                graphics.fillRect(reactX + 2, reactY, 88, 62);
            }
            //绘制温度  MAX  AVG   MIN
            graphics.setColor(new Color(255, 255, 255));
            graphics.setFont(new Font("宋体", Font.PLAIN, 12));
            graphics.drawString("MAX: " + recordOutDO.getMax() + "℃", reactX + 8, y.intValue() + v1.intValue() - 46);
            graphics.drawString("MIN: " + recordOutDO.getMin() + "℃", reactX + 8, y.intValue() + v1.intValue() - 28);
            graphics.drawString("AVG: " + recordOutDO.getAvg() + "℃", reactX + 8, y.intValue() + v1.intValue() - 8);

            //绘制最高温圆点
            //最高温的X坐标
            BigDecimal maxx = recordOutDO.getMaxX();
            BigDecimal maxy = recordOutDO.getMaxY();

            Double MaxX = maxx.doubleValue() * width - 3;
            Double MaxY = maxy.doubleValue() * height - 3;

            //绘制圆角矩形
            graphics.setColor(new Color(0, 0, 0, 150));
        /*graphics.fillOval(MaxX.intValue() - 6, MaxY.intValue() - 6, 18, 18);
        graphics.fillRect(MaxX.intValue(), MaxY.intValue() - 6, 48, 18);
        graphics.fillOval(MaxX.intValue() + 42, MaxY.intValue() - 6, 18, 18);*/
            graphics.fillRoundRect(MaxX.intValue() - 6, MaxY.intValue() - 6, 80, 18, 15, 15);
            //最高温点
            graphics.setColor(new Color(255, 42, 29));
            graphics.fillOval(MaxX.intValue(), MaxY.intValue(), 6, 6);
            graphics.setColor(new Color(255, 255, 255));
            graphics.setFont(new Font("宋体", Font.PLAIN, 12));
            graphics.drawString(recordOutDO.getMax() + "℃", MaxX.intValue() + 12, MaxY.intValue() + 8);

            //绘制最低温圆点
            BigDecimal minx = recordOutDO.getMinX();
            BigDecimal miny = recordOutDO.getMinY();

            Double MinX = minx.doubleValue() * width - 3;
            Double MinY = miny.doubleValue() * height - 3;

            //绘制圆角矩形
            graphics.setColor(new Color(0, 0, 0, 150));
      /*  graphics.fillOval(MinX.intValue() - 6, MinY.intValue() - 6, 18, 18);
        graphics.fillRect(MinX.intValue(), MinY.intValue() - 6, 48, 18);
        graphics.fillOval(MinX.intValue() + 42, MinY.intValue() - 6, 18, 18);*/
            graphics.fillRoundRect(MinX.intValue() - 6, MinY.intValue() - 6, 80, 18, 15, 15);

            //最高温点
            graphics.setColor(new Color(28, 193, 255));
            graphics.fillOval(MinX.intValue(), MinY.intValue(), 6, 6);
            graphics.setColor(new Color(255, 255, 255));
            graphics.setFont(new Font("宋体", Font.PLAIN, 12));
            graphics.drawString(recordOutDO.getMin() + "℃", MinX.intValue() + 12, MinY.intValue() + 8);

            graphics.dispose();
            try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
                ImageIO.write(read, "jpg", outputStream);
                try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(outputStream.toByteArray())){
                    CommonFileInDO commonFileInDO = new CommonFileInDO();
                    commonFileInDO.setFileName(BizIdUtils.snowflakeIdStr() + SymbolConstants.SLASH_LEFT + org.springframework.util.StringUtils.getFilenameExtension(url));
                    commonFileInDO.setInputStream(byteArrayInputStream);
                    Optional<FileStorageOutDO> optional = uploadManager.uploadFile(commonFileInDO);
                    if(optional.isPresent()){
                        return optional.get().getStoragePath() + SymbolConstants.SLASH_LEFT + optional.get().getFilename();
                    }
                }
            }
//            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(outputStream.toByteArray());
//            String picPath = String.format("%s%s.%s", UploadTypeEnum.MINIO_COMMON_PICTURE.getPath(), BizIdUtils.snowflakeIdStr(), org.springframework.util.StringUtils.getFilenameExtension(url));
//            boolean b = MinIoUnit.putObject(picPath, byteArrayInputStream);
//            byteArrayInputStream.close();
//            if (b) {
//                return geoaiUosProperties.getStore().getOriginPath() + picPath;
//            }
        }
        return "";
    }

    @Override
    @Transactional
    public boolean withdrawalBatch(List<String> batchIds) {
        powerInspectionReportManager.deleteRelBatch(batchIds);
        powerInspectionReportValueRelManager.deleteBatch(batchIds);
        //更新表计
        powerMeterDataManager.updatePushState(batchIds, InspectionVerifyStateEnum.DAIHESHI.getType());
        return true;
    }

    @Override
    public InspectionQueryPageOutDTO inspectionQueryPage(InspectionQueryPageInDTO reqVO) {
        PowerInspcetionReportInfoPO infoPO = PowerInspcetionReportInfoPO.builder().endTime(reqVO.getEndTime())
                .beginTime(reqVO.getBeginTime())
                .equipmentName(reqVO.getEquipmentName())
                .equipmentType(reqVO.getEquipmentType())
                .orgCode(reqVO.getOrgCode())
                .pageSize(reqVO.getPageSize())
                .pageNo(reqVO.getPageNo())
                .inspcetionType(reqVO.getAnalysisType())
                .inspectionConclusion(reqVO.getAnalysisConclusion())
                .spacingUnitName(reqVO.getSpacUnit())
                .ids(reqVO.getIds())
                .componentName(reqVO.getComponentName())
                .voltageName(reqVO.getVoltageLevel()).build();
        PowerInspectionReportOutDO powerInspectionReportOutDO = powerInspectionReportManager.queryByCondition(infoPO);
        InspectionQueryPageOutDTO dto = new InspectionQueryPageOutDTO();
        dto.setTotal(powerInspectionReportOutDO.getTotal());
        if (CollectionUtil.isNotEmpty(powerInspectionReportOutDO.getInfoOutList())) {
            List<String> reportIds = powerInspectionReportOutDO.getInfoOutList().stream().map(e -> e.getInspcetionReportId()).collect(Collectors.toList());
            List<PowerInspectionReportValueRelEntity> reportValueRelEntities = powerInspectionReportValueRelManager.selectByReportIds(reportIds);
            //biaoji
            List<String> valueIds = reportValueRelEntities.stream().map(e -> e.getValueId()).collect(Collectors.toList());
            List<PowerMeterReadingValueEntity> powerMeterReadingValueEntities = powerMeterDataManager.selectReadValueByValueIds(valueIds);
            Map<String, List<PowerMeterReadingValueEntity>> biaojiGroup = powerMeterReadingValueEntities.stream().collect(Collectors.groupingBy(PowerMeterReadingValueEntity::getDetailId));
            //honwai
            List<PowerMeterInfraredRecordOutDO> powerMeterInfraredRecordOutDOS = powerMeterInfraredRecordManager.selectMaxTempByValueIdsNotDelete(valueIds);
            Map<String, PowerMeterInfraredRecordOutDO> hongwaiMap = powerMeterInfraredRecordOutDOS.stream().collect(Collectors.toMap(PowerMeterInfraredRecordOutDO::getDetailId, q -> q));
            List<PowerInspectionReportOutDO.PowerInspectionReportInfoOut> infoOutList = powerInspectionReportOutDO.getInfoOutList();
            List<InspectionQueryPageOutDTO.InspectionQueryPageOutInfo> collect = infoOutList.stream().map(e -> {
                InspectionQueryPageOutDTO.InspectionQueryPageOutInfo info = new InspectionQueryPageOutDTO.InspectionQueryPageOutInfo();
                info.setInspcetionId(e.getInspcetionReportId());
                info.setInsprctionPhotoUrl(e.getInspectionUrl());
                info.setScreenShootUrl(e.getScreenshootUrl());
                info.setComponentName(e.getComponentName());
                info.setEquipmentName(e.getEquipmentName());
                info.setEquipmentId(e.getEquipmentId());
                info.setAnalysisType(e.getInsepctionType());
                info.setAlarmReson(e.getAlarmReason());
                info.setAnalysisConclusion(e.getInspectionConclusion());
                info.setEquipmentType(e.getEquipmentType());
                info.setSpacUnit(e.getSpacunitName());
                info.setVoltageLevel(e.getVoltageLevel());
                info.setPhotographyTime(e.getPhotographyTime());
                info.setThumbnailUrl(e.getThumbnailUrl());
                //告警原因
                if (StringUtils.isNotEmpty(e.getAlarmReason())) {
                    info.setAnalysisConclusionDesc(PowerDeviceStateEnum.getValueByCode(e.getInspectionConclusion()) + "(" + e.getAlarmReason() + ")");
                } else {
                    info.setAnalysisConclusionDesc(PowerDeviceStateEnum.getValueByCode(e.getInspectionConclusion()));
                }
                //设置告警结果
                //巡检类型的通过查询readingInfo判断
                List<InspectionQueryPageOutDTO.ReadingInfo> readingInfos = new ArrayList<>();
                if (e.getInsepctionType().equals(PowerDsicernTypesEnum.BIAOJI.getType())) {
                    List<PowerMeterReadingValueEntity> biaojiReadingValue = biaojiGroup.get(e.getRegionRelId());
                    if (CollectionUtil.isNotEmpty(biaojiReadingValue)) {
                        readingInfos = biaojiReadingValue.stream().map(biaojiReading -> {
                            InspectionQueryPageOutDTO.ReadingInfo readingInfo = new InspectionQueryPageOutDTO.ReadingInfo();
                            readingInfo.setValue(biaojiReading.getReadingValue());
                            readingInfo.setKey(biaojiReading.getReadingRuleName());
                            readingInfo.setValid(biaojiReading.getValid());
                            return readingInfo;
                        }).collect(Collectors.toList());
                    }
                    info.setReadingInfos(readingInfos);
                }
                //红外的通过查询readingInfo判断  红外需要拼接多个温度返回
                if (e.getInsepctionType().equals(PowerDsicernTypesEnum.HONGWAI.getType())) {
                    PowerMeterInfraredRecordOutDO powerMeterInfraredRecordOutDO = hongwaiMap.get(e.getRegionRelId());
                    if (ObjectUtils.isNotEmpty(powerMeterInfraredRecordOutDO)) {
                        InspectionQueryPageOutDTO.ReadingInfo readingInfo = new InspectionQueryPageOutDTO.ReadingInfo();
                        readingInfo.setValue(powerMeterInfraredRecordOutDO.getMaxTemperature() + "℃");
                        readingInfo.setKey(PowerMeterInfraredDescEnum.MAX.getDesc());
                        if (e.getInspectionConclusion().equals(PowerDeviceStateEnum.NORMAL.getCode())) {
                            readingInfo.setValid(true);
                        } else {
                            readingInfo.setValid(false);
                        }
                        readingInfos.add(readingInfo);
                        readingInfo = new InspectionQueryPageOutDTO.ReadingInfo();
                        readingInfo.setValue(powerMeterInfraredRecordOutDO.getMinTemperature() + "℃");
                        readingInfo.setKey(PowerMeterInfraredDescEnum.MIN.getDesc());
                        if (e.getInspectionConclusion().equals(PowerDeviceStateEnum.NORMAL.getCode())) {
                            readingInfo.setValid(true);
                        } else {
                            readingInfo.setValid(false);
                        }
                        readingInfos.add(readingInfo);
                        readingInfo = new InspectionQueryPageOutDTO.ReadingInfo();
                        readingInfo.setValue(powerMeterInfraredRecordOutDO.getAvgTemperature() + "℃");
                        readingInfo.setKey(PowerMeterInfraredDescEnum.AVG.getDesc());
                        if (e.getInspectionConclusion().equals(PowerDeviceStateEnum.NORMAL.getCode())) {
                            readingInfo.setValid(true);
                        } else {
                            readingInfo.setValid(false);
                        }
                        readingInfos.add(readingInfo);
                    }
                    info.setReadingInfos(readingInfos);
                }
                if (e.getInsepctionType().equals(PowerDsicernTypesEnum.QUEXIAN.getType())) {
                    InspectionQueryPageOutDTO.ReadingInfo readingInfo = new InspectionQueryPageOutDTO.ReadingInfo();
                    readingInfo.setValue("");
                    readingInfo.setKey(e.getInspectionResult());
                    //判断是否为有缺陷
                    readingInfo.setValid(!PowerDeviceStateEnum.isDefect(e.getInspectionConclusion()));
                    readingInfos.add(readingInfo);
                    info.setReadingInfos(readingInfos);
                }
                return info;
            }).collect(Collectors.toList());
            dto.setInfoList(collect);
        }
        return dto;
    }


    @Override
    @Transactional
    public boolean inspectionDeleteBatch(List<String> ids) {
        powerInspectionReportManager.deleteBatch(ids);
        powerInspectionReportValueRelManager.deleteBatch(ids);
        return true;
    }

    @Override
    @Transactional
    public boolean infraredWithdrawalBatch(List<String> batchIds) {
        //删除巡检报告列表
        //修改红外核实状态
        powerInspectionReportManager.deleteRelBatch(batchIds);
        powerInspectionReportValueRelManager.deleteBatch(batchIds);
        //更新表计
        powerMeterFlightDetailInfraredManager.updatePushState(batchIds, InspectionVerifyStateEnum.DAIHESHI.getType());
        return true;
    }

    @Override
    public boolean defectWithdrawalBatch(List<String> ids) {
        //删除巡检报告列表
        powerInspectionReportManager.deleteRelBatch(ids);
        //更新缺陷识别状态
        powerMeterFlightDetailDefectManager.updatePushState(ids, InspectionVerifyStateEnum.DAIHESHI.getTypeInt(), TrustedAccessTracerHolder.get().getAccountId());
        return true;
    }

    public void syncPhoto(InspectionVerificationBatchInDTO inDTO) {
        log.info("#syncphoto,{}", inDTO.getIds());
        List<String> ids = inDTO.getIds();
        PowerHomeAlarmStatisticsInDO build = PowerHomeAlarmStatisticsInDO.builder().detailIds(ids).build();
        List<PowerMeterFlightDetailInfraredOutDO> powerMeterFlightDetailInfraredOutDOS = powerMeterFlightDetailInfraredManager.queryByDeviceStateCondition(build);
        List<PowerMeterInfraredRecordOutDO> infraredRecordOutDOS = powerMeterInfraredRecordManager.selectListByDetailIds(ids);
        Map<String, List<PowerMeterInfraredRecordOutDO>> infraredRecords = infraredRecordOutDOS.stream().collect(Collectors.groupingBy(PowerMeterInfraredRecordOutDO::getDetailId));
        powerMeterFlightDetailInfraredOutDOS.stream().forEach(item -> {
            List<PowerMeterInfraredRecordOutDO> infraredRecordResult = infraredRecords.get(item.getDetailId());
            if (CollectionUtil.isEmpty(infraredRecordResult)) {
                powerInspectionReportManager.fixUrl(item.getDetailId(), item.getInfratedUrl());
            } else {
                List<InspectionInfraredVerificationInDTO> collect = infraredRecordResult.stream().map(powerMeterInfraredRecordOutDO -> {
                    InspectionInfraredVerificationInDTO verificationDto = new InspectionInfraredVerificationInDTO();
                    verificationDto.setX1(powerMeterInfraredRecordOutDO.getSiteX1());
                    verificationDto.setX2(powerMeterInfraredRecordOutDO.getSiteX2());
                    verificationDto.setY1(powerMeterInfraredRecordOutDO.getSiteY1());
                    verificationDto.setY2(powerMeterInfraredRecordOutDO.getSiteY2());
                    verificationDto.setMax(powerMeterInfraredRecordOutDO.getMaxTemperature());
                    verificationDto.setMin(powerMeterInfraredRecordOutDO.getMinTemperature());
                    verificationDto.setAvg(powerMeterInfraredRecordOutDO.getAvgTemperature());
                    verificationDto.setMaxX(powerMeterInfraredRecordOutDO.getMaxSiteX());
                    verificationDto.setMaxY(powerMeterInfraredRecordOutDO.getMaxSiteY());
                    verificationDto.setMinX(powerMeterInfraredRecordOutDO.getMinSiteX());
                    verificationDto.setMinY(powerMeterInfraredRecordOutDO.getMinSiteY());
                    return verificationDto;
                }).collect(Collectors.toList());
                try {
                    String pathUrl = drawAchievementReacts(collect, item.getInfratedUrl());
                    log.info("获取到pathUrl信息 ==> {}", pathUrl);
                    powerInspectionReportManager.fixUrl(item.getDetailId(), pathUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    public String drawAchievementReacts(List<InspectionInfraredVerificationInDTO> inDTO, String url) throws
            IOException {
        //获取到图片文件
//        boolean flag = MinIoUnit.objectExists(geoaiUosProperties.getMinio().getBucketName(), url);
        boolean flag = fileManager.checkFileExists(url);
        if (!flag) {
            return "";
        }
//        InputStream inputStream = MinIoUnit.getObject(url);
        try (InputStream inputStream = fileManager.getInputSteam(url)){
            if (Objects.isNull(inputStream)) {
                return "";
            }
            BufferedImage read = ImageIO.read(inputStream);
            //获取图片宽度和长度
            int width = read.getWidth();
            int height = read.getHeight();
            Graphics g = read.getGraphics();
            Graphics2D graphics = (Graphics2D) g;
            graphics.setStroke(new BasicStroke(2f));
            for (InspectionInfraredVerificationInDTO recordOutDO : inDTO) {
                // 计算机绘制的左上角坐标
                BigDecimal leftX = recordOutDO.getX1();
                BigDecimal leftY = recordOutDO.getY1();
                BigDecimal rightX = recordOutDO.getX2();
                BigDecimal rightY = recordOutDO.getY2();
                Double x = leftX.doubleValue() * width;
                Double x1 = rightX.doubleValue() * width;
                Double y = leftY.doubleValue() * height;
                Double y1 = rightY.doubleValue() * height;
                Double v = x1 - x;
                Double v1 = y1 - y;
                graphics.setColor(new Color(255, 255, 255));
                graphics.drawRect(x.intValue(), y.intValue(), v.intValue(), v1.intValue());
                //绘制半透明矩形
                graphics.setColor(new Color(0, 0, 0, 150));
                //计算透明矩形的左上角点位
                int reactX;
                int reactY;
                if (x.intValue() + v.intValue() + 88 > width) {
                    //框选位置过于靠近右边边框,不够位置绘制，则文字描述置于左下角
                    reactX = x.intValue() - 88;
                    reactY = y.intValue() + v1.intValue() - 62;
                    graphics.fillRect(reactX - 2, reactY, 88, 62);
                } else {
                    reactX = x.intValue() + v.intValue();
                    reactY = y.intValue() + v1.intValue() - 62;
                    graphics.fillRect(reactX + 2, reactY, 88, 62);
                }
                //绘制温度  MAX  AVG   MIN
                graphics.setColor(new Color(255, 255, 255));
                graphics.setFont(new Font("宋体", Font.PLAIN, 12));
                graphics.drawString("MAX: " + recordOutDO.getMax() + "℃", reactX + 8, y.intValue() + v1.intValue() - 46);
                graphics.drawString("MIN: " + recordOutDO.getMin() + "℃", reactX + 8, y.intValue() + v1.intValue() - 28);
                graphics.drawString("AVG: " + recordOutDO.getAvg() + "℃", reactX + 8, y.intValue() + v1.intValue() - 8);

                //绘制最高温圆点
                //最高温的X坐标
                BigDecimal maxx = recordOutDO.getMaxX();
                BigDecimal maxy = recordOutDO.getMaxY();

                Double MaxX = maxx.doubleValue() * width - 3;
                Double MaxY = maxy.doubleValue() * height - 3;

                //绘制圆角矩形
                graphics.setColor(new Color(0, 0, 0, 150));
                graphics.fillRoundRect(MaxX.intValue() - 6, MaxY.intValue() - 6, 80, 18, 15, 15);
                //最高温点
                graphics.setColor(new Color(255, 42, 29));
                graphics.fillOval(MaxX.intValue(), MaxY.intValue(), 6, 6);
                graphics.setColor(new Color(255, 255, 255));
                graphics.setFont(new Font("宋体", Font.PLAIN, 12));
                graphics.drawString(recordOutDO.getMax() + "℃", MaxX.intValue() + 12, MaxY.intValue() + 8);

                //绘制最低温圆点
                BigDecimal minx = recordOutDO.getMinX();
                BigDecimal miny = recordOutDO.getMinY();

                Double MinX = minx.doubleValue() * width - 3;
                Double MinY = miny.doubleValue() * height - 3;

                //绘制圆角矩形
                graphics.setColor(new Color(0, 0, 0, 150));
                graphics.fillRoundRect(MinX.intValue() - 6, MinY.intValue() - 6, 80, 18, 15, 15);
                //最高温点
                graphics.setColor(new Color(28, 193, 255));
                graphics.fillOval(MinX.intValue(), MinY.intValue(), 6, 6);
                graphics.setColor(new Color(255, 255, 255));
                graphics.setFont(new Font("宋体", Font.PLAIN, 12));
                graphics.drawString(recordOutDO.getMin() + "℃", MinX.intValue() + 12, MinY.intValue() + 8);
            }
            graphics.dispose();
            String filenameExtension = org.springframework.util.StringUtils.getFilenameExtension(url);
            if(StringUtils.isEmpty(filenameExtension)){
                filenameExtension = "jpg";
            }
            try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ByteArrayInputStream bos = new ByteArrayInputStream(outputStream.toByteArray())){
                ImageIO.write(read, filenameExtension, outputStream);
                CommonFileInDO commonFileInDO = new CommonFileInDO();
                commonFileInDO.setFileName(BizIdUtils.snowflakeIdStr() + SymbolConstants.POINT + filenameExtension);
                commonFileInDO.setInputStream(bos);
                Optional<FileStorageOutDO> optional = uploadManager.uploadFile(commonFileInDO);
                if(optional.isPresent()){
                    return optional.get().getStoragePath() + SymbolConstants.SLASH_LEFT + optional.get().getFilename();
                }
            }
//            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(outputStream.toByteArray());
//            String picPath = String.format("%s%s.%s", UploadTypeEnum.MINIO_COMMON_PICTURE.getPath(), BizIdUtils.snowflakeIdStr(), filenameExtension);
//            boolean b = MinIoUnit.putObject(picPath, byteArrayInputStream);
//            inputStream.close();
//            outputStream.close();
//            byteArrayInputStream.close();
//            if (b) {
//                return geoaiUosProperties.getStore().getOriginPath() + picPath;
//            }
        }
        return "";
    }
}
