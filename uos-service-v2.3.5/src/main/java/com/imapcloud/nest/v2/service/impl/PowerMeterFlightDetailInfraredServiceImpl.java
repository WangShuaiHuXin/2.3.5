package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.geoai.common.core.bean.PageResultInfo;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.common.enums.PowerDeviceStateEnum;
import com.imapcloud.nest.v2.dao.entity.PowerMeterFlightDetailInfraredEntity;
import com.imapcloud.nest.v2.dao.mapper.PowerMeterFlightDetailInfraredMapper;
import com.imapcloud.nest.v2.dao.po.in.PowerMeterFlightDetailInfraredInDTO;
import com.imapcloud.nest.v2.dao.po.out.DeviceStateStatisticPO;
import com.imapcloud.nest.v2.dao.po.out.PowerMeterFlightDetailInfraredOutDTO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerMeterFlightDetailInfraredOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerMeterInfraredRecordOutDO;
import com.imapcloud.nest.v2.manager.sql.PowerInspectionReportManager;
import com.imapcloud.nest.v2.manager.sql.PowerMeterFlightDetailInfraredManager;
import com.imapcloud.nest.v2.manager.sql.PowerMeterInfraredRecordManager;
import com.imapcloud.nest.v2.service.PowerInfraredService;
import com.imapcloud.nest.v2.service.PowerMeterFlightDetailInfraredService;
import com.imapcloud.nest.v2.service.dto.out.MeterDetailPhotoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.PowerMeterInfraredRecordOutDTO;
import com.imapcloud.sdk.utils.StringUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 红外测温-飞行数据详情表 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2022-12-28
 */
@Service
public class PowerMeterFlightDetailInfraredServiceImpl implements PowerMeterFlightDetailInfraredService {

    @Resource
    private PowerMeterFlightDetailInfraredMapper powerMeterFlightDetailInfraredMapper;

    @Resource
    private PowerMeterFlightDetailInfraredManager powerMeterFlightDetailInfraredManager;

    @Resource
    private PowerMeterInfraredRecordManager powerMeterInfraredRecordManager;

    @Resource
    private PowerInspectionReportManager powerInspectionReportManager;

    @Override
    public PageResultInfo<PowerMeterFlightDetailInfraredOutDTO> listPages(PowerMeterFlightDetailInfraredInDTO dto) {
        PageResultInfo<PowerMeterFlightDetailInfraredOutDTO> resultInfo = new PageResultInfo<>();

        LambdaQueryWrapper<PowerMeterFlightDetailInfraredEntity> eq = Wrappers.lambdaQuery(PowerMeterFlightDetailInfraredEntity.class)
                .eq(StringUtils.isNotBlank(dto.getDataId()), PowerMeterFlightDetailInfraredEntity::getDataId, dto.getDataId())
                .eq(ObjectUtils.isNotEmpty(dto.getDeviceState()), PowerMeterFlightDetailInfraredEntity::getDeviceState, dto.getDeviceState())
                .eq(ObjectUtils.isNotEmpty(dto.getVerificationState()), PowerMeterFlightDetailInfraredEntity::getVerificationState, dto.getVerificationState())
                .eq(ObjectUtils.isNotEmpty(dto.getTemperatureState()), PowerMeterFlightDetailInfraredEntity::getTemperatureState, dto.getTemperatureState())
                .eq(StringUtils.isNotEmpty(dto.getOrgCode()), PowerMeterFlightDetailInfraredEntity::getOrgCode, dto.getOrgCode())
                .in(CollectionUtils.isNotEmpty(dto.getDeviceStates()), PowerMeterFlightDetailInfraredEntity::getDeviceState, dto.getDeviceStates())
                .orderByDesc(PowerMeterFlightDetailInfraredEntity::getComponentName, PowerMeterFlightDetailInfraredEntity::getId);

        Page<PowerMeterFlightDetailInfraredEntity> page = new Page<>(dto.getPageNo(), dto.getPageSize());

        Page<PowerMeterFlightDetailInfraredEntity> pageRes = powerMeterFlightDetailInfraredMapper.selectPage(page, eq);
        long total = pageRes.getTotal();
        List<PowerMeterFlightDetailInfraredEntity> records = pageRes.getRecords();
        if (CollUtil.isEmpty(records)) {
            resultInfo.setTotal(0);
            resultInfo.setRecords(Collections.emptyList());
            return resultInfo;
        }
        List<String> detailIdList = records.stream().map(PowerMeterFlightDetailInfraredEntity::getDetailId).collect(Collectors.toList());
        List<PowerMeterInfraredRecordOutDO> powerMeterInfraredRecordOutDOList = powerMeterInfraredRecordManager.selectListByDetailIds(detailIdList);
        Map<String, List<PowerMeterInfraredRecordOutDO>> recordMap = powerMeterInfraredRecordOutDOList.stream()
                .collect(Collectors.groupingBy(PowerMeterInfraredRecordOutDO::getDetailId));
        List<PowerMeterFlightDetailInfraredOutDTO> poList = Lists.newLinkedList();
        for (PowerMeterFlightDetailInfraredEntity record : records) {
            PowerMeterFlightDetailInfraredOutDTO infraredOutDTO = new PowerMeterFlightDetailInfraredOutDTO();
            BeanUtils.copyProperties(record, infraredOutDTO);
            if (StringUtil.isEmpty(infraredOutDTO.getComponentName())) {
                infraredOutDTO.setComponentName(record.getPhotoName());
            }

            List<PowerMeterInfraredRecordOutDO> recordOutDOList = recordMap.get(record.getDetailId());
            if (CollUtil.isNotEmpty(recordOutDOList)) {
                PowerMeterInfraredRecordOutDO powerMeterInfraredRecordOutDO = recordOutDOList.stream()
                        .max(Comparator.comparing(PowerMeterInfraredRecordOutDO::getMaxTemperature))
                        .orElse(null);
                if (powerMeterInfraredRecordOutDO != null) {
                    infraredOutDTO.setMaxTemperature(powerMeterInfraredRecordOutDO.getMaxTemperature());
                    infraredOutDTO.setMinTemperature(powerMeterInfraredRecordOutDO.getMinTemperature());
                    infraredOutDTO.setAvgTemperature(powerMeterInfraredRecordOutDO.getAvgTemperature());
                }
            }
            poList.add(infraredOutDTO);
            infraredOutDTO.setOrgCode(record.getOrgCode());
            infraredOutDTO.setDataId(record.getDataId());
        }
        resultInfo.setRecords(poList);
        resultInfo.setTotal(total);
        return resultInfo;
    }

    @Override
    public List<PowerMeterInfraredRecordOutDTO> getResultDetails(String detailId) {
        List<PowerMeterInfraredRecordOutDO> doList = powerMeterInfraredRecordManager.selectListByDetailId(detailId);
        List<PowerMeterInfraredRecordOutDTO> resultList = Lists.newLinkedList();
        if (!CollectionUtils.isEmpty(doList)) {
            for (PowerMeterInfraredRecordOutDO powerMeterInfraredRecordOutDO : doList) {
                PowerMeterInfraredRecordOutDTO outDTO = new PowerMeterInfraredRecordOutDTO();
                BeanUtils.copyProperties(powerMeterInfraredRecordOutDO, outDTO);
                resultList.add(outDTO);
            }
        }
        return resultList;
    }

    @Override
    public void batchDeleteByDataIdList(List<String> dataIdList, String accountId) {

        List<PowerMeterFlightDetailInfraredOutDO> detailInfraredOutDOList = powerMeterFlightDetailInfraredManager
                .queryByDataIdCollection(dataIdList);
        List<String> detailIdList = detailInfraredOutDOList.stream()
                .map(PowerMeterFlightDetailInfraredOutDO::getDetailId)
                .collect(Collectors.toList());
        batchDelete(detailIdList, accountId);
    }

    @Resource
    private PowerInfraredService powerInfraredService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean batchDelete(List<String> detailIdList, String accountId) {
        if (CollectionUtils.isEmpty(detailIdList) || StringUtils.isEmpty(accountId)) {
            return false;
        }
        // 检测是否在运行
        powerInfraredService.checkRunning(detailIdList, false);

        int i = powerMeterFlightDetailInfraredManager.batchDeleteByDetailIdList(detailIdList, accountId);
        powerMeterInfraredRecordManager.deleteByDetailIdList(detailIdList, accountId);

        // 删除报告
        powerInspectionReportManager.deleteRelBatch(detailIdList);
        return i > 0;
    }

    @Override
    public List<MeterDetailPhotoOutDTO> batchSavePowerMeterInfraredRecord() {
        return null;
    }

    @Override
    public Map<String, Integer> deviceStateStatistics(String dataId) {
        if (Objects.nonNull(dataId)) {
            List<DeviceStateStatisticPO> poList = powerMeterFlightDetailInfraredMapper.countGroupByDeviceStateByDataId(dataId);
            if (!CollectionUtils.isEmpty(poList)) {
                Map<String, Integer> statistics = poList.stream().collect(Collectors.toMap(DeviceStateStatisticPO::getDeviceState, DeviceStateStatisticPO::getCounts));
                for (PowerDeviceStateEnum e : PowerDeviceStateEnum.values()) {
                    Integer v = statistics.get(String.valueOf(e.getCode()));
                    if (Objects.isNull(v)) {
                        statistics.put(String.valueOf(e.getCode()), 0);
                    }
                }
                return statistics;
            }
        }
        Map<String, Integer> statistics = new HashMap<>(8);
        for (PowerDeviceStateEnum e : PowerDeviceStateEnum.values()) {
            statistics.put(String.valueOf(e.getCode()), 0);
        }
        return statistics;
    }
}
