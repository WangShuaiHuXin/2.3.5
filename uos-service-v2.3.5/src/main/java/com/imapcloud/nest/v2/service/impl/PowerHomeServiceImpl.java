package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.google.common.collect.Lists;
import com.imapcloud.nest.utils.DataUtil;
import com.imapcloud.nest.v2.common.enums.*;
import com.imapcloud.nest.v2.dao.entity.PowerEquipmentLegerInfoEntity;
import com.imapcloud.nest.v2.dao.entity.PowerInspectionReportValueRelEntity;
import com.imapcloud.nest.v2.dao.entity.PowerMeterFlightDetailInfraredEntity;
import com.imapcloud.nest.v2.dao.entity.PowerMeterReadingValueEntity;
import com.imapcloud.nest.v2.dao.mapper.PowerInspectionReportValueRelMapper;
import com.imapcloud.nest.v2.dao.po.in.PowerInspcetionReportInfoPO;
import com.imapcloud.nest.v2.dao.po.in.PowerMeterFlightDetailInfraredInDTO;
import com.imapcloud.nest.v2.dao.po.out.PowerMeterFlightDetailInfraredOutDTO;
import com.imapcloud.nest.v2.manager.dataobj.in.DataEquipmentPointQueryInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerHomeAlarmStatisticsInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerHomeBaseSettingInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.*;
import com.imapcloud.nest.v2.manager.sql.*;
import com.imapcloud.nest.v2.service.PowerHomeService;
import com.imapcloud.nest.v2.service.PowerMeterFlightDetailInfraredService;
import com.imapcloud.nest.v2.service.dto.in.InspectionEquipmentInDTO;
import com.imapcloud.nest.v2.service.dto.in.InspectionStatisticsInDTO;
import com.imapcloud.nest.v2.service.dto.out.*;
import com.imapcloud.nest.v2.web.vo.req.PowerHomeInspectionQueryByReqVO;
import com.imapcloud.nest.v2.web.vo.resp.PowerHomeInspectionStatisticsRespVO;
import com.imapcloud.sdk.utils.StringUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import scala.Int;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PowerHomeServiceImpl implements PowerHomeService {
    @Resource
    private PowerHomeManager powerHomeManager;

    @Resource
    private PowerEquipmentLegerInfoManager powerEquipmentLegerInfoManager;

    @Resource
    private PowerInspectionReportManager powerInspectionReportManager;

    @Resource
    private DataEquipmentPointManager dataEquipmentPointManager;

    @Resource
    private PowerMeterFilghtDetailManager powerMeterFilghtDetailManager;

    @Resource
    private PowerMeterFlightDetailInfraredManager powerMeterFlightDetailInfraredManager;

    @Resource
    private PowerMeterFlightDetailInfraredService powerMeterFlightDetailInfraredService;

    @Resource
    private PowerMeterDataManager powerMeterDataManager;

    @Resource
    private PowerMeterInfraredRecordManager powerMeterInfraredRecordManager;

    @Resource
    private PowerInspectionReportValueRelManager powerInspectionReportValueRelManager;

    @Resource
    private PowerMeterFlightDetailDefectManager powerMeterFlightDetailDefectManager;

    @Override
    public PowerHomeSettingOutDTO queryDataStatic(String orgCode) {

        PowerHomeBaseSettingInfoOutDO outDO = powerHomeManager.queryByOrg(orgCode);
        PowerHomeSettingOutDTO dto = new PowerHomeSettingOutDTO();
        BeanUtils.copyProperties(outDO, dto);
        return dto;
    }

    @Override
    public List<PowerInspectionEquipmentListOutDTO> inspectionEquipmentList(InspectionEquipmentInDTO inDTO) {
        PowerInspcetionReportInfoPO build = PowerInspcetionReportInfoPO.builder()
                .orgCode(inDTO.getOrgCode())
                .equipmentType(inDTO.getEquipmentType())
                .pageNo(inDTO.getPageNo())
                .pageSize(inDTO.getPageSize()).build();
        if (StringUtils.isNotEmpty(inDTO.getBeginTime())) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat tdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date begin = sdf.parse(inDTO.getBeginTime());
                Date date = sdf.parse(inDTO.getEndTime());
                Date end = DateUtils.addDays(date, 1);
                build.setBeginTime(tdf.format(begin));
                build.setEndTime(tdf.format(end));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        PowerInspectionReportOutDO dos = powerInspectionReportManager.queryByCondition(build);
        List<PowerInspectionReportOutDO.PowerInspectionReportInfoOut> infoOutList = dos.getInfoOutList();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return infoOutList.stream().map(e -> {
            PowerInspectionEquipmentListOutDTO dto = new PowerInspectionEquipmentListOutDTO();
            dto.setInspectionConclusion(e.getInspectionConclusion().toString());
            dto.setInspectionReportId(e.getInspcetionReportId());
            dto.setEquipmentName(e.getEquipmentName());
            dto.setPhotographyTime(e.getPhotographyTime().format(fmt));
            dto.setInspectionResults(e.getInspectionResult());
            dto.setInspectionType(e.getInsepctionType());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public PowerEquipmentTreeOutDTO equipmentTree(String orgCode, String keyWord) {
        List<PowerEquipmentInfoOutDO> dos = powerHomeManager.equipmentTree(orgCode);
        PowerEquipmentTreeOutDTO dto = new PowerEquipmentTreeOutDTO();
        List<PowerEquipmentTreeOutDTO.PowerEquipmentInfo> collect = dos.stream().map(e -> {
            PowerEquipmentTreeOutDTO.PowerEquipmentInfo info = new PowerEquipmentTreeOutDTO.PowerEquipmentInfo();
            info.setEquipmentId(e.getEquipmentId());
            info.setSpacingUnit(e.getSpacingUnitName());
            info.setStationName(e.getSubstationName());
            info.setVoltageLevel(e.getVoltageLevel());
            info.setEquipmentType(e.getEquipmentType());
            info.setEquipmentName(e.getEquipmentName());
            return info;
        }).collect(Collectors.toList());
        dto.setTotal((long) dos.size());
        //台账树查询
        if (StringUtils.isNotEmpty(keyWord)) {
            collect.forEach(e -> {
                if (StringUtils.isNotEmpty(e.getEquipmentName()) && e.getEquipmentName().contains(keyWord)) {
                    e.setTypeFlag(true);
                    e.setSpacingFlag(true);
                    e.setVoltageFlag(true);
                    return;
                }
                if (StringUtils.isNotEmpty(e.getEquipmentType()) && e.getEquipmentType().contains(keyWord)) {
                    e.setSpacingFlag(true);
                    e.setVoltageFlag(true);
                    return;
                }
                if (StringUtils.isNotEmpty(e.getSpacingUnit()) && e.getSpacingUnit().contains(keyWord)) {
                    e.setVoltageFlag(true);
                    return;
                }
            });
        }
        dto.setInfoList(collect);
        return dto;
    }

    @Override
    public PowerHomeInspectionQueryByOutDTO inspectionQueryBy(PowerHomeInspectionQueryByReqVO vo) {

        PowerInspectionReportOutDO powerInspectionReportOutDO = powerHomeManager.inspectionQueryBy(vo);

        PowerHomeInspectionQueryByOutDTO dto = new PowerHomeInspectionQueryByOutDTO();
        if (powerInspectionReportOutDO.getTotal() == 0) {
            dto.setTotal(powerInspectionReportOutDO.getTotal());
            return dto;
        }
        List<String> equipmentIds = powerInspectionReportOutDO.getInfoOutList().stream().map(e -> {
            String equipmentId = e.getEquipmentId();
            return equipmentId;
        }).collect(Collectors.toList());
        //biaoji
        List<String> reportIds = powerInspectionReportOutDO.getInfoOutList().stream().map(e -> e.getInspcetionReportId()).collect(Collectors.toList());
        List<PowerInspectionReportValueRelEntity> reportValueRelEntities = powerInspectionReportValueRelManager.selectByReportIds(reportIds);
        List<String> valueIds = reportValueRelEntities.stream().map(e -> e.getValueId()).collect(Collectors.toList());
        List<PowerMeterReadingValueEntity> powerMeterReadingValueEntities = powerMeterDataManager.selectReadValueByValueIds(valueIds);
        Map<String, List<PowerMeterReadingValueEntity>> biaojiGroup = powerMeterReadingValueEntities.stream().collect(Collectors.groupingBy(PowerMeterReadingValueEntity::getDetailId));

        //honwai
        List<PowerMeterInfraredRecordOutDO> powerMeterInfraredRecordOutDOS = powerMeterInfraredRecordManager.selectMaxTempByValueIdsNotDelete(valueIds);
        Map<String, PowerMeterInfraredRecordOutDO> honwaiMap = powerMeterInfraredRecordOutDOS.stream().collect(Collectors.toMap(PowerMeterInfraredRecordOutDO::getDetailId, q -> q));


        List<PowerEquipmentLegerInfoEntity> powerEquipmentLegerInfoEntities = powerEquipmentLegerInfoManager.queryEquipmentByIds(equipmentIds);
        Map<String, PowerEquipmentLegerInfoEntity> equipmentLegerInfoEntityMap = powerEquipmentLegerInfoEntities.stream().collect(Collectors.toMap(PowerEquipmentLegerInfoEntity::getEquipmentId, q -> q));
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<PowerHomeInspectionQueryByOutDTO.PowerHomeInspectionQueryByInfo> resultInfo = powerInspectionReportOutDO.getInfoOutList().stream().map(e -> {
            PowerHomeInspectionQueryByOutDTO.PowerHomeInspectionQueryByInfo info = new PowerHomeInspectionQueryByOutDTO.PowerHomeInspectionQueryByInfo();
            PowerEquipmentLegerInfoEntity powerEquipmentLegerInfoEntity = equipmentLegerInfoEntityMap.get(e.getEquipmentId());
            if (ObjectUtils.isNotEmpty(powerEquipmentLegerInfoEntity)) {
                info.setInspectionPoints(powerEquipmentLegerInfoEntity.getVoltageLevel() + "-" + powerEquipmentLegerInfoEntity.getEquipmentType() + "-" + powerEquipmentLegerInfoEntity.getEquipmentName());
            }
            info.setAnalysisType(e.getInsepctionType());
            info.setAnalysisConclusion(e.getInspectionConclusion().toString());
            info.setAnalysisResult(e.getInspectionResult());
            info.setPhotographyTime(e.getPhotographyTime().format(fmt));
            info.setUrl(e.getScreenshootUrl());
            if (PowerDsicernTypesEnum.BIAOJI.getType().equals(e.getInsepctionType())) {
                StringBuffer buffer = new StringBuffer();
                List<PowerMeterReadingValueEntity> readingValueEntities = biaojiGroup.get(e.getRegionRelId());
                if (CollectionUtil.isNotEmpty(readingValueEntities)) {
                    readingValueEntities.forEach(item -> {
                        buffer.append(ObjectUtils.isNotEmpty(item.getReadingRuleName()) ? item.getReadingRuleName() + " :" : " " + " :");
                        buffer.append(item.getReadingValue() + "  ");
                    });
                }
                info.setAnalysisResult(buffer.toString());
            } else if (PowerDsicernTypesEnum.HONGWAI.getType().equals(e.getInsepctionType())) {
                PowerMeterInfraredRecordOutDO outDO = honwaiMap.get(e.getRegionRelId());
                StringBuffer buffer = new StringBuffer();
                if (ObjectUtils.isNotEmpty(outDO)) {
                    buffer.append("MAX:").append(outDO.getMaxTemperature()).append("℃");
                    buffer.append(" MIN:").append(outDO.getMinTemperature()).append("℃");
                    buffer.append(" AVG:").append(outDO.getAvgTemperature()).append("℃");
                }
                info.setAnalysisResult(buffer.toString());
            } else if (PowerDsicernTypesEnum.QUEXIAN.getType().equals(e.getInsepctionType())) {
                // 是缺陷的则为 有缺陷/否则为无缺陷
                info.setAnalysisResult(e.getInspectionResult());
            }
            info.setAlarmReason(e.getAlarmReason());
            return info;
        }).collect(Collectors.toList());
        dto.setTotal(powerInspectionReportOutDO.getTotal());
        //按照拍摄时间倒序排序
        resultInfo.sort(new Comparator<PowerHomeInspectionQueryByOutDTO.PowerHomeInspectionQueryByInfo>() {
            @Override
            public int compare(PowerHomeInspectionQueryByOutDTO.PowerHomeInspectionQueryByInfo o1, PowerHomeInspectionQueryByOutDTO.PowerHomeInspectionQueryByInfo o2) {
                return Convert.toLocalDateTime(o2.getPhotographyTime()).isAfter(Convert.toLocalDateTime(o1.getPhotographyTime())) ? 1 : -1;

            }
        });
        dto.setInfoList(resultInfo);
        return dto;
    }

    @Override
    public List<PowerHomeInspectionStatisticsOutDTO> inspcetionStatistics(String orgCode, String begintTime, String endTime) {
        PowerHomeInspectionQueryByReqVO vo = new PowerHomeInspectionQueryByReqVO();
        vo.setOrgCode(orgCode);
        if (StringUtils.isNotEmpty(begintTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat tdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date begin = sdf.parse(begintTime);
                Date date = sdf.parse(endTime);
                Date end = DateUtils.addDays(date, 1);
                vo.setBeginTime(tdf.format(begin));
                vo.setEndTime(tdf.format(end));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //返回的结果值
        List<PowerHomeInspectionStatisticsOutDTO> dtos = this.inspcetionStatisticsDefault();
        PowerInspectionReportOutDO powerInspectionReportOutDO = powerHomeManager.inspectionQueryBy(vo);
        List<PowerHomeInspectionStatisticsOutDTO> dtoList = new ArrayList<>();
        Long total = powerInspectionReportOutDO.getTotal();
        Map<Integer, List<PowerInspectionReportOutDO.PowerInspectionReportInfoOut>> collect = powerInspectionReportOutDO.getInfoOutList().stream().collect(Collectors.groupingBy(PowerInspectionReportOutDO.PowerInspectionReportInfoOut::getInspectionConclusion));
        Set<Integer> integers = collect.keySet();
        Iterator<Integer> iterator = integers.iterator();
        DecimalFormat format = new DecimalFormat("#0.00");
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            List<PowerInspectionReportOutDO.PowerInspectionReportInfoOut> powerInspectionReportInfoOuts = collect.get(next);
            PowerHomeInspectionStatisticsOutDTO dto = new PowerHomeInspectionStatisticsOutDTO();
            dto.setKey(next.toString());
            dto.setValue(String.valueOf(powerInspectionReportInfoOuts.size()));
            dto.setPer(format.format((double) powerInspectionReportInfoOuts.size() / total.doubleValue()));
            dtoList.add(dto);
        }
        Map<String, PowerHomeInspectionStatisticsOutDTO> valueMap = dtoList.stream().collect(Collectors.toMap(PowerHomeInspectionStatisticsOutDTO::getKey, q -> q));
        if (CollectionUtil.isEmpty(dtoList)) {
            return dtos;
        }
        dtos.stream().forEach(e -> {
            PowerHomeInspectionStatisticsOutDTO dto = valueMap.get(e.getKey());
            if (ObjectUtils.isNotEmpty(dto)) {
                e.setPer(dto.getPer());
                e.setValue(dto.getValue());
            }
        });
        return dtos;
    }

    @Override
    public List<PowerHomeEquipmentTypeInspcetionOutDTO> equipmentTypeInspectionStatistics(String orgCode, String beginTime, String endTime) {
        PowerInspcetionReportInfoPO build = PowerInspcetionReportInfoPO.builder().orgCode(orgCode).build();
        if (StringUtils.isNotEmpty(beginTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat tdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date begin = sdf.parse(beginTime);
                Date date = sdf.parse(endTime);
                Date end = DateUtils.addDays(date, 1);
                build.setBeginTime(tdf.format(begin));
                build.setEndTime(tdf.format(end));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        List<PowerHomeEquipmentTypeInspcetionOutDTO> result = new ArrayList<>();
        PowerInspectionReportOutDO powerInspectionReportOutDO = powerInspectionReportManager.queryByCondition(build);
        if (powerInspectionReportOutDO.getTotal() == 0) {
            return result;
        }
        List<PowerInspectionReportOutDO.PowerInspectionReportInfoOut> infoOutList = powerInspectionReportOutDO.getInfoOutList();
        //根据设备类型分组
        Map<String, List<PowerInspectionReportOutDO.PowerInspectionReportInfoOut>> map = infoOutList.stream().filter(e -> StringUtils.isNotEmpty(e.getEquipmentType())).collect(Collectors.groupingBy(PowerInspectionReportOutDO.PowerInspectionReportInfoOut::getEquipmentType));
        //统计分组总数
        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            List<PowerInspectionReportOutDO.PowerInspectionReportInfoOut> powerInspectionReportInfoOuts = map.get(next);
            PowerHomeEquipmentTypeInspcetionOutDTO dto = new PowerHomeEquipmentTypeInspcetionOutDTO();
            int total = powerInspectionReportInfoOuts.size();
            long errCount = powerInspectionReportInfoOuts.stream().filter(e -> e.getInspectionConclusion() == PowerDeviceStateEnum.SERUIYS_DEFECTS.getCode() ||
                    e.getInspectionConclusion() == PowerDeviceStateEnum.CRITICAL_DEFECTS.getCode() ||
                    e.getInspectionConclusion() == PowerDeviceStateEnum.GNERMAL_DEFECTS.getCode())
                    .count();
            dto.setEquipmentType(next);
            dto.setErrInspection(errCount);
            dto.setTotalInspcetion((long) total);
            result.add(dto);
        }
        List<PowerHomeEquipmentTypeInspcetionOutDTO> collect = result.stream().sorted(new Comparator<PowerHomeEquipmentTypeInspcetionOutDTO>() {
            @Override
            public int compare(PowerHomeEquipmentTypeInspcetionOutDTO o1, PowerHomeEquipmentTypeInspcetionOutDTO o2) {
                return (int) (o2.getTotalInspcetion() - o1.getTotalInspcetion());
            }
        }).limit(7).collect(Collectors.toList());
        return collect;
    }

    @Override
    public boolean inspectionStatisticsEdit(InspectionStatisticsInDTO build) {
        PowerHomeBaseSettingInfoOutDO powerHomeBaseSettingInfoOutDO = powerHomeManager.queryByOrg(build.getOrgCode());
        PowerHomeBaseSettingInDO inDO = new PowerHomeBaseSettingInDO();
        BeanUtils.copyProperties(build, inDO);
        String accountId = TrustedAccessTracerHolder.get().getAccountId();
        inDO.setCreatorId(accountId);
        inDO.setModifierId(accountId);
        if (StringUtils.isEmpty(powerHomeBaseSettingInfoOutDO.getOrgCode())) {
            //新增
            return powerHomeManager.saveStatisticsOne(inDO);
        } else {
            //编辑
            return powerHomeManager.updateStatisticsOne(inDO);
        }
    }

    @Override
    public PowerHomeInspectionStatisticsOutDTO.PowerHomeAlarmStatisticsOutDTO alarmStatistics(String orgCode, String beginTime, String endTime) {
        //已处理：统计设备状态为“一般缺陷/严重缺陷/危急缺陷”，核实状态为“已核实/误报”的数据
        int processed = 0;
        //待处理：统计设备状态为“一般缺陷/严重缺陷/危急缺陷”，核实状态为“待核实”的数据量
        int pending = 0;
        //查询表计数据
        List<String> states = Lists.newLinkedList();
        states.add(PowerDeviceStateEnum.GNERMAL_DEFECTS.getCode().toString());
        states.add(PowerDeviceStateEnum.SERUIYS_DEFECTS.getCode().toString());
        states.add(PowerDeviceStateEnum.CRITICAL_DEFECTS.getCode().toString());
        PowerHomeAlarmStatisticsInDO build = PowerHomeAlarmStatisticsInDO.builder().orgCode(orgCode)
                .deviceState(states).build();
        if (StringUtils.isNotEmpty(beginTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat tdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date begin = sdf.parse(beginTime);
                Date date = sdf.parse(endTime);
                Date end = DateUtils.addDays(date, 1);
                build.setBeginTime(tdf.format(begin));
                build.setEndTime(tdf.format(end));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        List<MeterDataDetailInfoOutDTO> dtos = powerMeterFilghtDetailManager.queryByCondition(build);
        //表计根据核实状态分组
        if (CollectionUtil.isNotEmpty(dtos)) {
            Map<String, List<MeterDataDetailInfoOutDTO>> collect = dtos.stream().filter(e -> e.getDeviceState() == PowerDeviceStateEnum.GNERMAL_DEFECTS.getCode() ||
                    e.getDeviceState() == PowerDeviceStateEnum.SERUIYS_DEFECTS.getCode() ||
                    e.getDeviceState() == PowerDeviceStateEnum.CRITICAL_DEFECTS.getCode()).collect(Collectors.groupingBy(MeterDataDetailInfoOutDTO::getVerificationStatus));
            //表计待处理的数据
            List<MeterDataDetailInfoOutDTO> remove = collect.remove(InspectionVerifyStateEnum.DAIHESHI.getType());
            if (ObjectUtils.isNotEmpty(remove)) {
                pending += remove.size();
            }
            List<MeterDataDetailInfoOutDTO> yiheshi = collect.get(InspectionVerifyStateEnum.YIHESHI.getType());
            if (CollectionUtil.isNotEmpty(yiheshi)) {
                processed += yiheshi.size();
            }
            List<MeterDataDetailInfoOutDTO> wubao = collect.get(InspectionVerifyStateEnum.WUBAO.getType());
            if (CollectionUtil.isNotEmpty(wubao)) {
                processed += wubao.size();
            }
        }
        //查询红外测温数据
        List<PowerMeterFlightDetailInfraredOutDO> powerMeterFlightDetailInfraredOutDOS = powerMeterFlightDetailInfraredManager.queryByDeviceStateCondition(build);
        if (CollectionUtil.isNotEmpty(powerMeterFlightDetailInfraredOutDOS)) {
            //根据核实状态分组
            Map<Integer, Long> collect = powerMeterFlightDetailInfraredOutDOS.stream().collect(Collectors.groupingBy(PowerMeterFlightDetailInfraredOutDO::getVerificationState, Collectors.counting()));
            pending += ObjectUtils.isNotEmpty(collect.get(Integer.valueOf(InspectionVerifyStateEnum.DAIHESHI.getType()))) ? collect.get(Integer.valueOf(InspectionVerifyStateEnum.DAIHESHI.getType())) : 0;
            processed += ObjectUtils.isNotEmpty(collect.get(Integer.valueOf(InspectionVerifyStateEnum.WUBAO.getType()))) ? collect.get(Integer.valueOf(InspectionVerifyStateEnum.WUBAO.getType())) : 0;
            processed += ObjectUtils.isNotEmpty(collect.get(Integer.valueOf(InspectionVerifyStateEnum.YIHESHI.getType()))) ? collect.get(Integer.valueOf(InspectionVerifyStateEnum.YIHESHI.getType())) : 0;
        }
        //查询缺陷识别数据
        List<PowerMeterFlightDetailDefectOutDO.StatisticsOutDO> statisticsOutDOS = powerMeterFlightDetailDefectManager.statisticsTotal(build);
        if (CollectionUtil.isNotEmpty(statisticsOutDOS)) {
            Map<Integer, Long> numMap = statisticsOutDOS.stream().collect(Collectors.toMap(PowerMeterFlightDetailDefectOutDO.StatisticsOutDO::getDeviceState, PowerMeterFlightDetailDefectOutDO.StatisticsOutDO::getNum));
            pending += balanceLong(numMap.get(InspectionVerifyStateEnum.DAIHESHI.getTypeInt()));
            processed += balanceLong(numMap.get(InspectionVerifyStateEnum.WUBAO.getTypeInt()));
            processed += balanceLong(numMap.get(InspectionVerifyStateEnum.YIHESHI.getTypeInt()));
        }
        PowerHomeInspectionStatisticsOutDTO.PowerHomeAlarmStatisticsOutDTO dto = new PowerHomeInspectionStatisticsOutDTO.PowerHomeAlarmStatisticsOutDTO();
        dto.setAlarmProcessed(processed);
        dto.setAlarmPending(pending);
        return dto;
    }

    @Override
    public Integer homeInspectionAlarmEventsCount(String orgCode) {
        //查询表计读数所有状态为待核实的数据
        List<String> devices = Lists.newLinkedList();
        devices.add(PowerDeviceStateEnum.GNERMAL_DEFECTS.getCode().toString());
        devices.add(PowerDeviceStateEnum.CRITICAL_DEFECTS.getCode().toString());
        devices.add(PowerDeviceStateEnum.SERUIYS_DEFECTS.getCode().toString());
        PowerHomeAlarmStatisticsInDO build = PowerHomeAlarmStatisticsInDO.builder().orgCode(orgCode)
                .verifiyState(InspectionVerifyStateEnum.DAIHESHI.getType())
                .deviceState(devices).build();
        //查询表记数据
        List<MeterDataDetailInfoOutDTO> dtos = powerMeterFilghtDetailManager.queryByCondition(build);
        //查询红外数据
        List<PowerMeterFlightDetailInfraredOutDO> powerMeterFlightDetailInfraredOutDOS = powerMeterFlightDetailInfraredManager.queryByDeviceStateCondition(build);
        //查询缺陷识别数据
        List<PowerMeterFlightDetailDefectOutDO> outDOS = powerMeterFlightDetailDefectManager.queryByDevicesState(build);
        return dtos.size() + powerMeterFlightDetailInfraredOutDOS.size() + outDOS.size();
    }

    public Long balanceLong(Long balance) {
        if (balance == null) {
            return 0L;
        }
        return balance;
    }

    @Override
    public List<PowerHomeInspectionAlarmEventsOutDTO> homeInspectionAlarmEvents(String orgCode) {
        //查询表计读数所有状态为待核实的数据
        List<String> devices = Lists.newLinkedList();
        devices.add(PowerDeviceStateEnum.GNERMAL_DEFECTS.getCode().toString());
        devices.add(PowerDeviceStateEnum.CRITICAL_DEFECTS.getCode().toString());
        devices.add(PowerDeviceStateEnum.SERUIYS_DEFECTS.getCode().toString());
        List<PowerHomeInspectionAlarmEventsOutDTO> dtoList = new ArrayList<>();
        PowerHomeAlarmStatisticsInDO build = PowerHomeAlarmStatisticsInDO.builder().orgCode(orgCode)
                .verifiyState(InspectionVerifyStateEnum.DAIHESHI.getType())
                .deviceState(devices).build();
        //表计
        List<MeterDataDetailInfoOutDTO> dtos = powerMeterFilghtDetailManager.queryByCondition(build);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (CollectionUtil.isNotEmpty(dtos)) {
            List<PowerHomeInspectionAlarmEventsOutDTO> collect = dtos.stream().map(e -> {
                PowerHomeInspectionAlarmEventsOutDTO dto = new PowerHomeInspectionAlarmEventsOutDTO();
                dto.setDefectStatus(e.getDeviceState());
                if (!StringUtils.isAnyEmpty(e.getDeviceLayerName(), e.getComponentName())) {
                    dto.setEquipmentName(e.getDeviceLayerName() + "-" + e.getComponentName());
                }
                if (CollectionUtil.isNotEmpty(e.getReadingInfos())) {
                    StringBuffer stringBuffer = new StringBuffer();
                    List<MeterDataDetailInfoOutDTO.ReadingInfo> readingInfos = e.getReadingInfos();
                    readingInfos.stream().forEach(item -> {
                        stringBuffer.append(item.getRuleName() + ":");
                        stringBuffer.append(item.getReadingValue());
                    });
                    dto.setAnalysisResult(stringBuffer.toString());
                }
                if (StringUtils.isNotBlank(e.getAlarmReason())) {
                    dto.setAlarmReson(InspectionResonStatusEnum.getValueByCode(e.getAlarmReason()));
                }
                dto.setPhotographyTime(fmt.format(e.getShootingTime()));
                dto.setAlarmId(e.getDetailId());
                dto.setUrl(e.getDiscernPicUrl());
                dto.setAnalysisType(PowerDsicernTypesEnum.BIAOJI.getType());
                dto.setOrgCode(e.getOrgCode());
                dto.setDataId(e.getDataId());
                return dto;
            }).collect(Collectors.toList());
            dtoList.addAll(collect);
        }
        //红外
        PowerMeterFlightDetailInfraredInDTO dto = new PowerMeterFlightDetailInfraredInDTO();
        dto.setOrgCode(orgCode);
        dto.setVerificationState(Integer.valueOf(InspectionVerifyStateEnum.DAIHESHI.getType()));
        dto.setPageNo(-1);
        dto.setPageSize(-1);
        dto.setDeviceStates(devices);
        PageResultInfo<PowerMeterFlightDetailInfraredOutDTO> infraredDtos = powerMeterFlightDetailInfraredService.listPages(dto);
        if (CollectionUtils.isNotEmpty(infraredDtos.getRecords())) {
            List<PowerHomeInspectionAlarmEventsOutDTO> collect = infraredDtos.getRecords().stream().map(e -> {
                PowerHomeInspectionAlarmEventsOutDTO infraredDto = new PowerHomeInspectionAlarmEventsOutDTO();
                infraredDto.setDefectStatus(e.getDeviceState());
                if (!StringUtils.isAnyEmpty(e.getDeviceLayerName(), e.getComponentName())) {
                    infraredDto.setEquipmentName(e.getDeviceLayerName() + "-" + e.getComponentName());
                }
                if (ObjectUtils.isNotEmpty(e.getMaxTemperature())) {
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append(PowerMeterInfraredDescEnum.MAX.getDesc() + e.getMaxTemperature() + "℃ ");
                    stringBuffer.append(PowerMeterInfraredDescEnum.MIN.getDesc() + e.getMinTemperature() + "℃ ");
                    stringBuffer.append(PowerMeterInfraredDescEnum.AVG.getDesc() + e.getAvgTemperature() + "℃ ");
                    infraredDto.setAnalysisResult(stringBuffer.toString());
                }
                infraredDto.setAlarmReson(e.getReason());
                infraredDto.setPhotographyTime(fmt.format(e.getShootingTime()));
                infraredDto.setAlarmId(e.getDetailId());
                infraredDto.setUrl(e.getInfratedUrl());
                infraredDto.setAnalysisType(PowerDsicernTypesEnum.HONGWAI.getType());
                infraredDto.setOrgCode(e.getOrgCode());
                infraredDto.setDataId(e.getDataId());
                return infraredDto;
            }).collect(Collectors.toList());
            dtoList.addAll(collect);
        }
        List<PowerMeterFlightDetailDefectOutDO> outDOS = powerMeterFlightDetailDefectManager.queryByDevicesState(build);
        if (CollectionUtils.isNotEmpty(outDOS)) {
            for (PowerMeterFlightDetailDefectOutDO outDO : outDOS) {
                PowerHomeInspectionAlarmEventsOutDTO infraredDto = new PowerHomeInspectionAlarmEventsOutDTO();
                infraredDto.setDefectStatus(outDO.getDeviceState());
                if (!StringUtils.isAnyEmpty(outDO.getDeviceLayerName(), outDO.getComponentName())) {
                    infraredDto.setEquipmentName(outDO.getDeviceLayerName() + "-" + outDO.getComponentName());
                }
                infraredDto.setAlarmReson(outDO.getReason());
                if (StringUtils.isNotEmpty(outDO.getReason())) {
                    infraredDto.setAnalysisResult(PowerDefectStateEnum.DEFECT_YES.getMsg());
                } else {
                    infraredDto.setAnalysisResult(PowerDefectStateEnum.DEFECT_NO.getMsg());
                }
                infraredDto.setPhotographyTime(fmt.format(outDO.getShootingTime()));
                infraredDto.setAlarmId(outDO.getDetailId());
                infraredDto.setUrl(outDO.getPictureUrl());
                infraredDto.setAnalysisType(PowerDsicernTypesEnum.QUEXIAN.getType());
                infraredDto.setOrgCode(outDO.getOrgCode());
                infraredDto.setDataId(outDO.getDataId());
                dtoList.add(infraredDto);
            }
        }
        if (CollectionUtil.isNotEmpty(dtoList)) {
            dtoList = dtoList.stream().sorted(Comparator.comparing(PowerHomeInspectionAlarmEventsOutDTO::getPhotographyTime, Comparator.nullsFirst(Comparator.naturalOrder())).reversed()).collect(Collectors.toList());
        }
        return dtoList;
    }

    @Override
    public List<PowerHomePointQueryOutDTO> homePointQuery(String orgCode) {
        DataEquipmentPointQueryInDO build = DataEquipmentPointQueryInDO.builder().orgCode(orgCode).build();
        DataEquipmentPointQueryOutDO queryOutDO = dataEquipmentPointManager.queryByCondition(build);
        List<PowerHomePointQueryOutDTO> dtoList = new ArrayList<>();
        if (queryOutDO.getTotal() > 0) {
            List<DataEquipmentPointQueryOutDO.DataEquipmentPointQueryInfoOutDO> dtos = queryOutDO.getDtos();
            //查询设备点关联的设备信息
            List<String> list = Lists.newLinkedList();
            dtos.stream().forEach(e -> {
                List<DataEquipmentPointQueryOutDO.DataEquipmentInfoOutDO> equipmentList = e.getEquipmentList();
                if (CollectionUtil.isNotEmpty(equipmentList)) {
                    List<String> collect = equipmentList.stream().map(DataEquipmentPointQueryOutDO.DataEquipmentInfoOutDO::getEquipmentId).collect(Collectors.toList());
                    list.addAll(collect);
                }
            });
            //查询对应设备id在巡检报告的所有数据
            PowerInspcetionReportInfoPO infoPO = PowerInspcetionReportInfoPO.builder().equipmentIds(list).build();

            //所有巡检报告项
            //产品固定要求这里写死查一个月的数据
            LocalDateTime firstDay = LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()), LocalTime.MIN);
            LocalDateTime lastDay = LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.lastDayOfYear()), LocalTime.MAX);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            infoPO.setBeginTime(firstDay.format(dtf));
            infoPO.setEndTime(lastDay.format(dtf));
            PowerInspectionReportOutDO powerInspectionReportOutDO = powerInspectionReportManager.queryByCondition(infoPO);

            List<PowerInspectionReportOutDO.PowerInspectionReportInfoOut> infoOutList = powerInspectionReportOutDO.getInfoOutList();
            Map<String, List<PowerInspectionReportOutDO.PowerInspectionReportInfoOut>> collect = infoOutList.stream().collect(Collectors.groupingBy(PowerInspectionReportOutDO.PowerInspectionReportInfoOut::getEquipmentId));
            dtoList = dtos.stream().map(item -> {
                PowerHomePointQueryOutDTO dto = new PowerHomePointQueryOutDTO();
                dto.setGroundDis(item.getGroundDis());
                dto.setPointName(item.getPointName());
                dto.setPointId(item.getPointId());
                dto.setHeight(item.getHeight());
                dto.setLat(item.getLat());
                dto.setLng(item.getLng());
                dto.setPanoramaDis(item.getPanoramaDis());
                List<DataEquipmentPointQueryOutDO.DataEquipmentInfoOutDO> equipmentList = item.getEquipmentList();
                if (CollectionUtil.isNotEmpty(equipmentList)) {
                    List<Integer> conclusionId = new LinkedList<>();
                    equipmentList.stream().forEach(bean -> {
                        List<PowerInspectionReportOutDO.PowerInspectionReportInfoOut> powerInspectionReportInfoOuts = collect.get(bean.getEquipmentId());
                        if (CollectionUtil.isNotEmpty(powerInspectionReportInfoOuts)) {
                            Set<Integer> conclusionIdSet = collect.get(bean.getEquipmentId()).stream().map(PowerInspectionReportOutDO.PowerInspectionReportInfoOut::getInspectionConclusion).collect(Collectors.toSet());
                            conclusionId.addAll(conclusionIdSet);
                        }
                    });
                    if (CollectionUtil.isNotEmpty(conclusionId)) {
                        conclusionId.sort(new Comparator<Integer>() {
                            @Override
                            public int compare(Integer o1, Integer o2) {
                                return o2 - o1;
                            }
                        });
                        dto.setDefectStatus(String.valueOf(conclusionId.get(0)));
                    } else {
                        //不是异常就是都正常
                        dto.setDefectStatus(PowerDeviceStateEnum.NORMAL.getCode().toString());
                    }
                } else {
                    dto.setDefectStatus(PowerDeviceStateEnum.NORMAL.getCode().toString());
                }
                return dto;
            }).collect(Collectors.toList());

        }
        return dtoList;
    }

    @Override
    public List<PowerHomePointQueryByOutDTO> homePointQueryBy(String pointId, String orgCode) {
        DataEquipmentPointQueryInDO build = DataEquipmentPointQueryInDO.builder().pointId(pointId)
                .orgCode(orgCode).build();
        DataEquipmentPointQueryOutDO queryOutDO = dataEquipmentPointManager.queryByCondition(build);
        List<PowerHomePointQueryByOutDTO> dtoList = new ArrayList<>();
        if (queryOutDO.getTotal() > 0) {
            List<DataEquipmentPointQueryOutDO.DataEquipmentPointQueryInfoOutDO> dtos = queryOutDO.getDtos();
            if (CollectionUtil.isNotEmpty(dtos)) {
                dtos.stream().forEach(e -> {
                    List<DataEquipmentPointQueryOutDO.DataEquipmentInfoOutDO> equipmentList = e.getEquipmentList();
                    if (CollectionUtil.isNotEmpty(equipmentList)) {
                        equipmentList.stream().forEach(item -> {
                            PowerHomePointQueryByOutDTO dto = new PowerHomePointQueryByOutDTO();
                            dto.setEquipmentId(item.getEquipmentId());
                            dto.setEquipmentName(item.getEquipmentName());
                            dtoList.add(dto);
                        });
                    }
                });
            }
        }
        return dtoList;
    }

    @Override
    public List<PowerHomeInspectionStatisticsOutDTO> inspcetionStatisticsDefault() {
        List<String> list = new ArrayList<>();
        list.add(PowerDeviceStateEnum.NORMAL.getCode().toString());
        list.add(PowerDeviceStateEnum.GNERMAL_DEFECTS.getCode().toString());
        list.add(PowerDeviceStateEnum.SERUIYS_DEFECTS.getCode().toString());
        list.add(PowerDeviceStateEnum.CRITICAL_DEFECTS.getCode().toString());
        List<PowerHomeInspectionStatisticsOutDTO> collect = list.stream().map(e -> {
            PowerHomeInspectionStatisticsOutDTO vo = new PowerHomeInspectionStatisticsOutDTO();
            vo.setKey(e);
            vo.setValue("0");
            vo.setPer("0");
            return vo;
        }).collect(Collectors.toList());
        return collect;
    }


}
