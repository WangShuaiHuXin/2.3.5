package com.imapcloud.nest.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.imapcloud.nest.v2.common.enums.NestFirmwareInstallStatusEnum;
import com.imapcloud.nest.v2.dao.entity.NestFirmwarePackageEntity;
import com.imapcloud.nest.v2.dao.mapper.NestFirmwarePackageMapper;
import com.imapcloud.nest.v2.dao.po.NestFirmwarePackageQueryCriteriaPO;
import com.imapcloud.nest.v2.service.UosNestFirmwareService;
import com.imapcloud.nest.v2.service.converter.FirmwarePackageConverter;
import com.imapcloud.nest.v2.service.dto.in.NestFirmwarePackageInDTO;
import com.imapcloud.nest.v2.service.dto.in.NestFirmwareVersionInDTO;
import com.imapcloud.nest.v2.service.dto.out.FirmwarePackageBasicOutDTO;
import com.imapcloud.nest.v2.service.dto.out.NestFirmwarePackageInfoOutDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 基站固件业务接口实现
 * @author Vastfy
 * @date 2022/7/12 16:21
 * @since 1.9.7
 */
@Service
public class UosNestFirmwareServiceImpl implements UosNestFirmwareService {

    @Resource
    private NestFirmwarePackageMapper nestFirmwarePackageMapper;

    @Resource
    private FirmwarePackageConverter firmwarePackageConverter;

    @Override
    public PageResultInfo<NestFirmwarePackageInfoOutDTO> pageNestFirmwarePackageInfos(NestFirmwareVersionInDTO condition) {
        NestFirmwarePackageQueryCriteriaPO queryCriteria = buildNestFirmwarePackageCriteria(condition);
        long total = nestFirmwarePackageMapper.countByCondition(queryCriteria);
        List<NestFirmwarePackageEntity> rows = null;
        if(total > 0){
            rows = nestFirmwarePackageMapper.selectByCondition(queryCriteria, PagingRestrictDo.getPagingRestrict(condition));
        }
        return PageResultInfo.of(total, rows)
                .map(r -> {
                    NestFirmwarePackageInfoOutDTO convert = firmwarePackageConverter.convert(r);
                    convert.setId(r.getPackageId().toString());
                    convert.setName(r.getPackageName());
                    convert.setVersion(r.getPackageVersion());
                    convert.setType(r.getPackageType());
                    convert.setState(r.getState());
                    convert.setModifiedTime(r.getModifiedTime());
                    convert.setUavWhich(r.getUavWhich());
                    return convert;
                });
    }

    private NestFirmwarePackageQueryCriteriaPO buildNestFirmwarePackageCriteria(NestFirmwareVersionInDTO condition) {
        return NestFirmwarePackageQueryCriteriaPO.builder()
                .baseNestId(condition.getNestId())
                .name(condition.getApkName())
                .type(condition.getType())
                .uavWhich(condition.getUavWhich())
                .build();
    }

    @Override
    public Map<String, List<FirmwarePackageBasicOutDTO>> getNestLatestFirmwareVersionInfos(Collection<String> nestIds) {
        List<Long> recordIds = nestFirmwarePackageMapper.listNestLatestFprIds(nestIds);
        if(!CollectionUtils.isEmpty(recordIds)){
            List<NestFirmwarePackageEntity> updateRecords = nestFirmwarePackageMapper.selectBatchIds(recordIds);
            if(!CollectionUtils.isEmpty(updateRecords)){
                Map<String, List<FirmwarePackageBasicOutDTO>> results = new HashMap<>();
                for (NestFirmwarePackageEntity updateRecord : updateRecords) {
                    results.computeIfAbsent(updateRecord.getBaseNestId(), r -> new ArrayList<>());
                    List<FirmwarePackageBasicOutDTO> values = results.get(updateRecord.getBaseNestId());
                    FirmwarePackageBasicOutDTO value = new FirmwarePackageBasicOutDTO();
                    value.setId(updateRecord.getPackageId().toString());
                    value.setType(updateRecord.getPackageType());
                    value.setName(updateRecord.getPackageName());
                    value.setVersion(updateRecord.getPackageVersion());
                    value.setUavWhich(updateRecord.getUavWhich());
                    values.add(value);
                }
                return results;
            }
        }
        return Collections.emptyMap();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long incrNestFirmwarePackageUpdateRecord(NestFirmwarePackageInDTO nfpInfo) {
        NestFirmwarePackageEntity record = new NestFirmwarePackageEntity();
        record.setPackageId(Long.valueOf(nfpInfo.getPackageId()));
        record.setBaseNestId(nfpInfo.getNestId());
        record.setPackageType(nfpInfo.getType());
        record.setPackageVersion(nfpInfo.getVersion());
        record.setPackageName(nfpInfo.getApkFileName());
        record.setState(NestFirmwareInstallStatusEnum.INSTALLING.getStatus());
        record.setUavWhich(nfpInfo.getUavWhich());
        nestFirmwarePackageMapper.insert(record);
        return record.getId();
    }

    @Override
    public boolean updateNestFirmwarePackageInstallState(Long updateRecordId, Integer nfpInstallState, String updaterId) {
        NestFirmwarePackageEntity record = new NestFirmwarePackageEntity();
        record.setState(nfpInstallState);
        if(StringUtils.hasText(updaterId)){
            record.setModifierId(updaterId);
        }
        record.setModifiedTime(LocalDateTime.now());
        LambdaQueryWrapper<NestFirmwarePackageEntity> condition = Wrappers.lambdaQuery(NestFirmwarePackageEntity.class)
                .eq(NestFirmwarePackageEntity::getId, updateRecordId);
        int affect = nestFirmwarePackageMapper.update(record, condition);
        return affect > 0;
    }

}
