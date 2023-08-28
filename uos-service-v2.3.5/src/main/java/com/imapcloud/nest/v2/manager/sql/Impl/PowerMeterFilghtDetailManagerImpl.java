package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.imapcloud.nest.v2.dao.entity.PowerMeterFlightDetailEntity;
import com.imapcloud.nest.v2.dao.entity.PowerMeterReadingValueEntity;
import com.imapcloud.nest.v2.dao.mapper.PowerMeterFlightDetailMapper;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerHomeAlarmStatisticsInDO;
import com.imapcloud.nest.v2.manager.sql.PowerMeterDataManager;
import com.imapcloud.nest.v2.manager.sql.PowerMeterFilghtDetailManager;
import com.imapcloud.nest.v2.service.dto.out.InspectionQueryPageOutDTO;
import com.imapcloud.nest.v2.service.dto.out.MeterDataDetailInfoOutDTO;
import com.imapcloud.sdk.utils.StringUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PowerMeterFilghtDetailManagerImpl implements PowerMeterFilghtDetailManager {

    @Resource
    private PowerMeterFlightDetailMapper powerMeterFlightDetailMapper;

    @Resource
    private PowerMeterDataManager powerMeterDataManager;

    @Override
    public List<MeterDataDetailInfoOutDTO> queryByCondition(PowerHomeAlarmStatisticsInDO build) {
        LambdaQueryWrapper<PowerMeterFlightDetailEntity> wrapper = Wrappers.<PowerMeterFlightDetailEntity>lambdaQuery()
                .eq(PowerMeterFlightDetailEntity::getOrgCode, build.getOrgCode())
                .eq(PowerMeterFlightDetailEntity::getDeleted, false)
                .in(CollectionUtil.isNotEmpty(build.getDeviceState()), PowerMeterFlightDetailEntity::getDeviceState, build.getDeviceState())
                .eq(StringUtils.isNotBlank(build.getVerifiyState()), PowerMeterFlightDetailEntity::getVerificationStatus, build.getVerifiyState())
                .gt(StringUtils.isNotEmpty(build.getBeginTime()), PowerMeterFlightDetailEntity::getShootingTime, build.getBeginTime())
                .lt(StringUtils.isNotEmpty(build.getEndTime()), PowerMeterFlightDetailEntity::getShootingTime, build.getEndTime());
        List<MeterDataDetailInfoOutDTO> result = new ArrayList<>();
        List<PowerMeterFlightDetailEntity> powerMeterFlightDetailEntities = powerMeterFlightDetailMapper.selectList(wrapper);
        if (CollectionUtil.isEmpty(powerMeterFlightDetailEntities)) {
            return result;
        }
        List<String> biaojiIds = powerMeterFlightDetailEntities.stream().map(e -> {
            return e.getDetailId();
        }).collect(Collectors.toList());
        List<PowerMeterReadingValueEntity> powerMeterReadingValueEntities = powerMeterDataManager.selectReadValueByDetailIds(biaojiIds);
        Map<String, List<PowerMeterReadingValueEntity>> biaojiGroup = powerMeterReadingValueEntities.stream().collect(Collectors.groupingBy(PowerMeterReadingValueEntity::getDetailId));

        List<MeterDataDetailInfoOutDTO> collect = powerMeterFlightDetailEntities.stream().map(e -> {
            MeterDataDetailInfoOutDTO dataDetailInfoOutDTO = new MeterDataDetailInfoOutDTO();
            dataDetailInfoOutDTO.setDataId(e.getDataId());
            dataDetailInfoOutDTO.setDetailId(e.getDetailId());
            dataDetailInfoOutDTO.setDetailName(e.getDetailName());
            dataDetailInfoOutDTO.setOriginalPicUrl(e.getOriginalPicUrl());
            dataDetailInfoOutDTO.setShootingTime(e.getShootingTime());
            dataDetailInfoOutDTO.setDeviceState(e.getDeviceState());
            dataDetailInfoOutDTO.setReadingState(e.getReadingState());
            dataDetailInfoOutDTO.setDiscernPicUrl(e.getDiscernPicUrl());
            dataDetailInfoOutDTO.setAreaLayerName(e.getAreaLayerName());
            dataDetailInfoOutDTO.setSubAreaLayerName(e.getSubAreaLayerName());
            dataDetailInfoOutDTO.setUnitLayerName(e.getUnitLayerName());
            Integer alarmReason = e.getAlarmReason();
            if (ObjectUtils.isNotEmpty(alarmReason)) {
                dataDetailInfoOutDTO.setAlarmReason(alarmReason.toString());
            }
            dataDetailInfoOutDTO.setDeviceLayerName(e.getDeviceLayerName());
            dataDetailInfoOutDTO.setComponentId(e.getComponentId());
            dataDetailInfoOutDTO.setVerificationStatus(e.getVerificationStatus());
            List<PowerMeterReadingValueEntity> entities = biaojiGroup.get(e.getDetailId());
            if (CollectionUtil.isNotEmpty(entities)) {
                List<MeterDataDetailInfoOutDTO.ReadingInfo> infos = entities.stream().map(biaojiReading -> {
                    MeterDataDetailInfoOutDTO.ReadingInfo readingInfo = new MeterDataDetailInfoOutDTO.ReadingInfo();
                    readingInfo.setReadingValue(biaojiReading.getReadingValue());
                    readingInfo.setRuleName(biaojiReading.getReadingRuleName());
                    readingInfo.setValid(biaojiReading.getValid());
                    return readingInfo;
                }).collect(Collectors.toList());
                dataDetailInfoOutDTO.setReadingInfos(infos);
            }
            dataDetailInfoOutDTO.setComponentName(e.getComponentName());
            dataDetailInfoOutDTO.setOrgCode(e.getOrgCode());
            return dataDetailInfoOutDTO;
        }).collect(Collectors.toList());

        return collect;
    }
}
