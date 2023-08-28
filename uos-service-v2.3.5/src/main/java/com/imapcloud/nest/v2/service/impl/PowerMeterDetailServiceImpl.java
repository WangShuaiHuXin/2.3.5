package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.web.exception.BizParameterException;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.v2.common.enums.DialAlarmReasonTypeEnum;
import com.imapcloud.nest.v2.common.enums.DialDeviceTypeEnum;
import com.imapcloud.nest.v2.common.enums.DialReadingTypeEnum;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.dao.entity.PowerMeterFlightDetailEntity;
import com.imapcloud.nest.v2.dao.entity.PowerMeterReadingValueEntity;
import com.imapcloud.nest.v2.dao.mapper.PowerMeterFlightDetailMapper;
import com.imapcloud.nest.v2.dao.mapper.PowerMeterReadingValueMapper;
import com.imapcloud.nest.v2.dao.po.MeterDataDetailQueryCriteriaPO;
import com.imapcloud.nest.v2.dao.po.out.MeterDeviceStateStatsOutPO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerComponentRuleInfoOutDO;
import com.imapcloud.nest.v2.manager.sql.PowerComponentRuleInfoManager;
import com.imapcloud.nest.v2.manager.sql.PowerInspectionReportManager;
import com.imapcloud.nest.v2.service.PowerMeterDetailService;
import com.imapcloud.nest.v2.service.converter.PowerDataConverter;
import com.imapcloud.nest.v2.service.dto.in.MeterDataDetailQueryDTO;
import com.imapcloud.nest.v2.service.dto.in.MeterReadingDiscernInfoInDTO;
import com.imapcloud.nest.v2.service.dto.in.MeterReadingInfoInDTO;
import com.imapcloud.nest.v2.service.dto.out.MeterDataDetailInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.MeterDeviceStateStatsOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 电力表计数据详情业务接口实现
 *
 * @author Vastfy
 * @date 2022/12/5 9:54
 * @since 2.1.5
 */
@Slf4j
@Service
public class PowerMeterDetailServiceImpl implements PowerMeterDetailService {

    @Resource
    private PowerMeterFlightDetailMapper powerMeterFlightDetailMapper;

    @Resource
    private PowerMeterReadingValueMapper powerMeterReadingValueMapper;

    @Resource
    private PowerComponentRuleInfoManager powerComponentRuleInfoManager;

    @Resource
    private PowerInspectionReportManager powerInspectionReportManager;

    @Override
    public List<MeterDeviceStateStatsOutDTO> getMeterDeviceStateStatistics(String dataId) {
        List<MeterDeviceStateStatsOutDTO> results = Arrays.stream(DialReadingTypeEnum.values())
                .map(r -> {
                    MeterDeviceStateStatsOutDTO stat = new MeterDeviceStateStatsOutDTO();
                    stat.setDeviceState(Integer.valueOf(r.getStatus()).toString());
                    stat.setTotal(0);
                    return stat;
                })
                .collect(Collectors.toList());
        List<MeterDeviceStateStatsOutPO> stats = powerMeterFlightDetailMapper.groupByDeviceState(dataId);
        if(!CollectionUtils.isEmpty(stats)){
            Map<String, Integer> map = stats.stream()
                    .collect(Collectors.toMap(MeterDeviceStateStatsOutPO::getDeviceState, MeterDeviceStateStatsOutPO::getTotal));
            for (MeterDeviceStateStatsOutDTO result : results) {
                if(map.containsKey(result.getDeviceState())){
                    result.setTotal(map.get(result.getDeviceState()));
                }
            }
        }
        return results;
    }

    @Override
    public PageResultInfo<MeterDataDetailInfoOutDTO> queryMeterDataDetails(MeterDataDetailQueryDTO condition) {
        MeterDataDetailQueryCriteriaPO queryCriteria = buildMeterDataQueryQueryCriteria(condition);
        long total = powerMeterFlightDetailMapper.countByCondition(queryCriteria);
        List<PowerMeterFlightDetailEntity> rows = null;
        Map<String, List<PowerMeterReadingValueEntity>> detailValueMap = null;
        if (total > 0) {
            rows = powerMeterFlightDetailMapper.selectByCondition(queryCriteria, PagingRestrictDo.getPagingRestrict(condition));
            if(!CollectionUtils.isEmpty(rows)){
                Set<String> detailIds = rows.stream()
                        .map(PowerMeterFlightDetailEntity::getDetailId)
                        .collect(Collectors.toSet());
                LambdaQueryWrapper<PowerMeterReadingValueEntity> con = Wrappers.lambdaQuery(PowerMeterReadingValueEntity.class)
                        .in(PowerMeterReadingValueEntity::getDetailId, detailIds);
                List<PowerMeterReadingValueEntity> readingValueEntities = powerMeterReadingValueMapper.selectList(con);
                if(!CollectionUtils.isEmpty(readingValueEntities)){
                    detailValueMap = readingValueEntities.stream()
                            .collect(Collectors.groupingBy(PowerMeterReadingValueEntity::getDetailId, Collectors.toList()));
                }
            }
        }
        Map<String, List<PowerMeterReadingValueEntity>> finalDetailValueMap = detailValueMap;
        return PageResultInfo.of(total, rows)
                .map(r -> {
                    MeterDataDetailInfoOutDTO convert = PowerDataConverter.INSTANCE.convert(r);
                    convert.setVerificationStatus(r.getVerificationStatus());
                    // 兼容性处理
                    if(StringUtils.hasText(convert.getAlarmReason())){
                        try {
                            if(Integer.parseInt(convert.getAlarmReason()) < 0){
                                convert.setAlarmReason(null);
                            }
                        }catch (Exception e){
                            convert.setAlarmReason(null);
                        }
                    }
                    if(!CollectionUtils.isEmpty(finalDetailValueMap) && finalDetailValueMap.containsKey(r.getDetailId())){
                        List<PowerMeterReadingValueEntity> readingValueEntities = finalDetailValueMap.get(r.getDetailId());
                        if(!CollectionUtils.isEmpty(readingValueEntities)){
                            List<MeterDataDetailInfoOutDTO.ReadingInfo> readingInfos = readingValueEntities.stream()
                                    .map(k -> {
                                        MeterDataDetailInfoOutDTO.ReadingInfo readingInfo = new MeterDataDetailInfoOutDTO.ReadingInfo();
                                        readingInfo.setReadingValue(k.getReadingValue());
                                        readingInfo.setRuleId(k.getReadingRuleId());
                                        readingInfo.setRuleName(k.getReadingRuleName());
                                        readingInfo.setValid(k.getValid());
                                        return readingInfo;
                                    })
                                    .collect(Collectors.toList());
                            convert.setReadingInfos(readingInfos);
                        }
                    }
                    return convert;
                });
    }

    @Override
    public List<PowerMeterFlightDetailEntity> getMeterDetails(Collection<String> meterDetailIds) {
        if(!CollectionUtils.isEmpty(meterDetailIds)){
            LambdaQueryWrapper<PowerMeterFlightDetailEntity> condition = Wrappers.lambdaQuery(PowerMeterFlightDetailEntity.class)
                    .in(PowerMeterFlightDetailEntity::getDetailId, meterDetailIds);
            return powerMeterFlightDetailMapper.selectList(condition);
        }
        return Collections.emptyList();
    }

    @Override
    public void batchDeleteByDataIdList(List<String> dataIdList) {

        LambdaQueryWrapper<PowerMeterFlightDetailEntity> queryWrapper = Wrappers
                .lambdaQuery(PowerMeterFlightDetailEntity.class)
                .in(PowerMeterFlightDetailEntity::getDataId, dataIdList)
                .eq(PowerMeterFlightDetailEntity::getDeleted, false);
        List<PowerMeterFlightDetailEntity> entityList = powerMeterFlightDetailMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(entityList)) {
            return;
        }
        List<String> detailIdList = entityList.stream()
                .map(PowerMeterFlightDetailEntity::getDetailId)
                .collect(Collectors.toList());
        deleteMeterDetails(detailIdList);
    }

    private void checkRunning(List<String> detailIdList) {

        LambdaQueryWrapper<PowerMeterFlightDetailEntity> queryWrapper = Wrappers
                .lambdaQuery(PowerMeterFlightDetailEntity.class)
                .in(PowerMeterFlightDetailEntity::getDetailId, detailIdList)
                .eq(PowerMeterFlightDetailEntity::getDeleted, 0);
        List<PowerMeterFlightDetailEntity> entityList = powerMeterFlightDetailMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(entityList)) {
            return;
        }
        for (PowerMeterFlightDetailEntity powerMeterFlightDetailEntity : entityList) {
            if (powerMeterFlightDetailEntity.getReadingState() == DialReadingTypeEnum.RECOGNIZING.getStatus()) {
                throw new BizException(MessageUtils
                        .getMessage(MessageEnum.GEOAI_UOS_UNABLE_TO_OPERATE_THE_PHOTO_DATA_IN_AI_RECOGNITION.getContent()));
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteMeterDetails(List<String> meterDetailIds) {
        if(!CollectionUtils.isEmpty(meterDetailIds)){
            checkRunning(meterDetailIds);
            LambdaQueryWrapper<PowerMeterFlightDetailEntity> condition = Wrappers.lambdaQuery(PowerMeterFlightDetailEntity.class)
                    .in(PowerMeterFlightDetailEntity::getDetailId, meterDetailIds)
                    // 不能删除识别中的图片
                    .ne(PowerMeterFlightDetailEntity::getReadingState, DialReadingTypeEnum.RECOGNIZING.getStatus());
            powerMeterFlightDetailMapper.delete(condition);

            // 删除巡检报告
            powerInspectionReportManager.deleteRelBatch(meterDetailIds);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void modifyMeterReadingInfo(String meterDetailId, MeterReadingInfoInDTO info) {
        // 详情存在、状态合法性校验
        if(!StringUtils.hasText(meterDetailId)){
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_METER_READING_DETAIL_ID_CANNOT_BE_BLANK.getContent()));
//            throw new BizParameterException("表计读数详情ID不能为空");
        }
        LambdaQueryWrapper<PowerMeterFlightDetailEntity> condition = Wrappers.lambdaQuery(PowerMeterFlightDetailEntity.class)
                .eq(PowerMeterFlightDetailEntity::getDetailId, meterDetailId);
        PowerMeterFlightDetailEntity entity = powerMeterFlightDetailMapper.selectOne(condition);
        if(Objects.isNull(entity)){
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_METER_READING_DETAIL_INFO_NOT_EXISTS.getContent()));
//            throw new BizException("表计读数详情信息不存在");
        }
        // 【识别中】不允许修改
        if(DialReadingTypeEnum.RECOGNIZING.matchEquals(entity.getReadingState())){
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_UNABLE_TO_OPERATE_THE_PHOTO_DATA_IN_AI_RECOGNITION.getContent()));
//            throw new BizException("不允许操作AI识别中的照片数据");
        }
        if(!StringUtils.hasText(entity.getComponentId())){
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_UNABLE_TO_OPERATE_PHOTO_DATA_WITHOUT_ASSOCIATED_PART_INFO.getContent()));
//            throw new BizException("不允许操作未关联部件信息的照片数据");
        }
        // 存在表计规则时，需要校验表计读取规则是否存在
        List<PowerComponentRuleInfoOutDO> componentInfos = powerComponentRuleInfoManager.selectByComponentId(entity.getComponentId());
        if(CollectionUtils.isEmpty(componentInfos)){
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_UNABLE_TO_OPERATE_PHOTO_DATA_WITHOUT_READING_RULES.getContent()));
//            throw new BizException("不允许操作未设置读数规则的图片数据");
        }
        List<MeterReadingInfoInDTO.MeterReadingValue> readingValues = info.getReadingValues();
        if(CollectionUtils.isEmpty(readingValues)){
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_METER_READ_VALUE_CANNOT_BE_EMPTY.getContent()));
//            throw new BizException("读数值不能为空");
        }
        // 过滤配置的读数项
        Map<String, PowerComponentRuleInfoOutDO> ruleMap = componentInfos.stream()
                .collect(Collectors.toMap(PowerComponentRuleInfoOutDO::getComponentRuleId, r -> r));
        readingValues = readingValues.stream()
                .filter(r -> ruleMap.containsKey(r.getReadingRuleId())).collect(Collectors.toList());
        if(readingValues.size() != ruleMap.size()){
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_METER_READ_VALUE_IS_INCOMPLETE.getContent()));
//            throw new BizException("表计读数项填写不完整");
        }
        // 更新读数项信息
        Map<String, PowerMeterReadingValueEntity> map = readingValues.stream()
                .collect(Collectors.toMap(MeterReadingInfoInDTO.MeterReadingValue::getReadingRuleId, r -> {
                    PowerMeterReadingValueEntity readingValue = new PowerMeterReadingValueEntity();
                    readingValue.setValueId(BizIdUtils.snowflakeIdStr());
                    readingValue.setDetailId(entity.getDetailId());
                    readingValue.setReadingRuleId(r.getReadingRuleId());
                    readingValue.setValid(true);
                    readingValue.setReadingValue(r.getReadingValue());
                    if(ruleMap.containsKey(r.getReadingRuleId())){
                        PowerComponentRuleInfoOutDO ruleInfo = ruleMap.get(r.getReadingRuleId());
                        if(Objects.equals(ruleInfo.getAlarmStatus(), 1)){
                            BigDecimal bigDecimal = new BigDecimal(readingValue.getReadingValue());
                            if(bigDecimal.compareTo(ruleInfo.getAlarmMax()) > 0 || bigDecimal.compareTo(ruleInfo.getAlarmMin()) < 0){
                                readingValue.setValid(false);
                            }
                        }
                        readingValue.setReadingRuleName(ruleInfo.getComponentRuleName());
                    }
                    return readingValue;
                }));
        // 更新表计截图为人工上传截图URL
        PowerMeterFlightDetailEntity updateInfo = new PowerMeterFlightDetailEntity();
        updateInfo.setId(entity.getId());
        if (StringUtils.hasText(info.getScreenshotPath())) {
            updateInfo.setDiscernPicUrl(info.getScreenshotPath());
        }

        updateInfo.setReadingState(DialReadingTypeEnum.WITH_READING.getStatus());
        // 有一个不合法，即严重告警
        if(map.values().stream().anyMatch(r -> Objects.equals(r.getValid(), Boolean.FALSE))){
            updateInfo.setDeviceState(DialDeviceTypeEnum.DEFECT_ALARM.getStatus());
            updateInfo.setAlarmReason(DialAlarmReasonTypeEnum.ABNORMAL_READING.getType());
        }
        // 全部合法：正常
        else{
            updateInfo.setDeviceState(DialDeviceTypeEnum.NORMAL.getStatus());
            updateInfo.setAlarmReason(DialAlarmReasonTypeEnum.NON.getType());
        }
        powerMeterFlightDetailMapper.updateById(updateInfo);
        LambdaQueryWrapper<PowerMeterReadingValueEntity> wrapper = Wrappers.lambdaQuery(PowerMeterReadingValueEntity.class)
                .eq(PowerMeterReadingValueEntity::getDetailId, meterDetailId)
                .in(PowerMeterReadingValueEntity::getReadingRuleId, map.keySet());
        powerMeterReadingValueMapper.delete(wrapper);
        powerMeterReadingValueMapper.saveBatch(new ArrayList<>(map.values()));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateMeterReadingDiscernInfo(String detailId, MeterReadingDiscernInfoInDTO info) {
        log.info("#PowerMeterDetailServiceImpl.updateMeterReadingDiscernInfo# info={}", JSONUtil.toJsonStr(info));
        // 更新表计截图为人工上传截图URL
        PowerMeterFlightDetailEntity updateInfo = new PowerMeterFlightDetailEntity();
        // 算法侧截图URL
        updateInfo.setAlgorithmPicUrl(info.getAlgorithmPicUrl());
        updateInfo.setDiscernPicUrl(info.getDiscernPicPath());
        updateInfo.setReadingState(info.getReadingState());
        updateInfo.setDeviceState(info.getDeviceState());
        updateInfo.setAlarmReason(info.getAlarmReasonType() != null ? info.getAlarmReasonType() : DialAlarmReasonTypeEnum.NON.getType());
        log.info("#PowerMeterDetailServiceImpl.updateMeterReadingDiscernInfo# info={}", JSONUtil.toJsonStr(updateInfo));
        LambdaQueryWrapper<PowerMeterFlightDetailEntity> con = Wrappers.lambdaQuery(PowerMeterFlightDetailEntity.class)
                .eq(PowerMeterFlightDetailEntity::getDetailId, detailId);
        powerMeterFlightDetailMapper.update(updateInfo, con);
        List<MeterReadingDiscernInfoInDTO.MeterReadingValue> readingValues = info.getReadingValues();
        // 更新读数项信息
        if(!CollectionUtils.isEmpty(readingValues)){
            // 删除历史读数记录
            LambdaQueryWrapper<PowerMeterReadingValueEntity> wrapper = Wrappers.lambdaQuery(PowerMeterReadingValueEntity.class)
                    .eq(PowerMeterReadingValueEntity::getDetailId, detailId);
            powerMeterReadingValueMapper.delete(wrapper);
            List<PowerMeterReadingValueEntity> readingValueEntities = readingValues.stream()
                    .map(r -> {
                        PowerMeterReadingValueEntity readingValue = new PowerMeterReadingValueEntity();
                        readingValue.setValueId(BizIdUtils.snowflakeIdStr());
                        readingValue.setDetailId(detailId);
                        readingValue.setReadingRuleId(r.getReadingRuleId());
                        readingValue.setReadingRuleName(r.getReadingRuleName());
                        readingValue.setReadingValue(r.getReadingValue());
                        readingValue.setValid(r.getValid());
                        readingValue.setRemark(r.getRemark());
                        return readingValue;
                    })
                    .collect(Collectors.toList());
            powerMeterReadingValueMapper.saveBatch(readingValueEntities);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateMeterReadingInfo(Collection<String> detailIds, MeterReadingDiscernInfoInDTO readingInfo
            , DialReadingTypeEnum dialReadingTypeEnum) {
        if (CollUtil.isEmpty(detailIds)) {
            return;
        }
        PowerMeterFlightDetailEntity updateInfo = new PowerMeterFlightDetailEntity();
        updateInfo.setReadingState(readingInfo.getReadingState());
        updateInfo.setDeviceState(readingInfo.getDeviceState());
        updateInfo.setAlarmReason(readingInfo.getAlarmReasonType());
        LambdaQueryWrapper<PowerMeterFlightDetailEntity> con = Wrappers.lambdaQuery(PowerMeterFlightDetailEntity.class)
                .in(PowerMeterFlightDetailEntity::getDetailId, detailIds);
        if (dialReadingTypeEnum != null) {
            con.eq(PowerMeterFlightDetailEntity::getReadingState, dialReadingTypeEnum.getStatus());
        }
        powerMeterFlightDetailMapper.update(updateInfo, con);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateMeterDetailReadingState(List<String> detailIds, int expectReadingState) {
        if (CollUtil.isEmpty(detailIds)) {
            return;
        }
        PowerMeterFlightDetailEntity entity = new PowerMeterFlightDetailEntity();
        entity.setReadingState(expectReadingState);
        LambdaQueryWrapper<PowerMeterFlightDetailEntity> con = Wrappers.lambdaQuery(PowerMeterFlightDetailEntity.class)
                .in(PowerMeterFlightDetailEntity::getDetailId, detailIds);
        powerMeterFlightDetailMapper.update(entity, con);
    }

    private MeterDataDetailQueryCriteriaPO buildMeterDataQueryQueryCriteria(MeterDataDetailQueryDTO condition) {
        return MeterDataDetailQueryCriteriaPO.builder()
                .visibleOrgCode(TrustedAccessTracerHolder.get().getOrgCode())
                .dataId(condition.getDataId())
                .deviceState(condition.getDeviceState())
                .readingState(condition.getReadingState())
                .verificationStatus(condition.getVerificationStatus())
                .build();
    }

    @Override
    public void init(List<String> detailIds) {
        if (CollUtil.isEmpty(detailIds)) {
            return;
        }
        powerMeterFlightDetailMapper.updateByDetailIdList(detailIds);
        powerMeterReadingValueMapper.deleteByDetailIdList(detailIds);
    }
}
