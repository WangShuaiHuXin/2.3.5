package com.imapcloud.nest.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Stream;

import cn.hutool.core.collection.CollectionUtil;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.web.exception.BizParameterException;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.dao.entity.UosRegionEntity;
import com.imapcloud.nest.v2.dao.mapper.UosRegionMapper;
import com.imapcloud.nest.v2.dao.po.UosRegionQueryCriteriaPO;
import com.imapcloud.nest.v2.service.UosRegionService;
import com.imapcloud.nest.v2.service.converter.UosRegionConverter;
import com.imapcloud.nest.v2.service.dto.in.UosRegionCreationInDTO;
import com.imapcloud.nest.v2.service.dto.in.UosRegionModificationInDTO;
import com.imapcloud.nest.v2.service.dto.in.UosRegionPageInDTO;
import com.imapcloud.nest.v2.service.dto.out.UosRegionInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.UosRegionQueryInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.UosRegionSimpleOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Classname UosRegionServiceImpl
 * @Description 区域服务接口实现类
 * @Date 2022/8/11 10:54
 * @Author Carnival
 */
@Slf4j
@Service
public class UosRegionServiceImpl implements UosRegionService {
    @Resource
    private UosRegionMapper uosRegionMapper;

    @Resource
    private UosRegionConverter uosRegionConverter;

    private static final Integer Fail = 0;

    @Override
    public String addRegion(UosRegionCreationInDTO regionCreationInDTO) {
        Optional<UosRegionEntity> regionByName = findRegionByName(regionCreationInDTO.getRegionName());
        if (regionByName.isPresent()) {
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ALREADY_EXIST_REGION.getContent()));
        }
        UosRegionEntity entity = new UosRegionEntity();
        entity.setRegionId(BizIdUtils.snowflakeIdStr());
        entity.setRegionName(regionCreationInDTO.getRegionName());
        entity.setDescription(regionCreationInDTO.getDescription());
        uosRegionMapper.insert(entity);
        return entity.getRegionId();
    }

    @Override
    public boolean deleteRegion(String regionId) {
        Optional<UosRegionEntity> regionById = findRegionById(regionId);
        if (!regionById.isPresent()) {
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_EXIST_REGION.getContent()));
        }
        int total = uosRegionMapper.queryRegionUsed(regionId);
        if (total > 0) {
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_BE_DELETED_REGION_IS_USE.getContent()));
        }
        return uosRegionMapper.deleteById(regionById.get().getId()) != Fail;
    }

    @Override
    public List<String> deleteBatchRegion(List<String> regionIds) {
        LambdaQueryWrapper<UosRegionEntity> con = Wrappers.lambdaQuery(UosRegionEntity.class)
                .in(UosRegionEntity::getRegionId, regionIds);
        List<UosRegionEntity> entities = uosRegionMapper.selectList(con);
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }
        List<String> execRegionIds = entities.stream()
                .map(UosRegionEntity::getRegionId)
                .map(Object::toString)
                .collect(Collectors.toList());
        con = Wrappers.lambdaQuery(UosRegionEntity.class)
                .in(UosRegionEntity::getId, execRegionIds);
        uosRegionMapper.delete(con);
        return execRegionIds;
    }

    @Override
    public Boolean modifyRegionInfo(String regionId, UosRegionModificationInDTO regionModificationInDTO) {
        Optional<UosRegionEntity> regionById = findRegionById(regionId);
        if (!regionById.isPresent()) {
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_EXIST_REGION.getContent()));
        }

        UosRegionEntity uosRegionEntity = regionById.get();
        uosRegionEntity.setRegionName(regionModificationInDTO.getRegionName());
        uosRegionEntity.setDescription(regionModificationInDTO.getDescription());
        uosRegionEntity.setModifiedTime(LocalDateTime.now());
        uosRegionEntity.setModifierId(TrustedAccessTracerHolder.get().getAccountId());
        return uosRegionMapper.updateById(uosRegionEntity) != Fail;
    }

    @Override
    public UosRegionQueryInfoOutDTO queryRegionInfo(String regionId) {
        Optional<UosRegionEntity> regionById = findRegionById(regionId);
        if (!regionById.isPresent()) {
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_EXIST_REGION.getContent()));
        }
        return uosRegionConverter.convert(regionById.get());
    }

    @Override
    public PageResultInfo<UosRegionQueryInfoOutDTO> pageRegionList(UosRegionPageInDTO regionQueryInDTO) {
        UosRegionQueryCriteriaPO regionQueryCriteriaPO = buildRegionCriteria(regionQueryInDTO);
        long total = uosRegionMapper.countByCondition(regionQueryCriteriaPO);
        List<UosRegionEntity> rows = null;
        if (total > 0) {
            rows = uosRegionMapper.selectByCondition(regionQueryCriteriaPO,
                    PagingRestrictDo.getPagingRestrict(regionQueryInDTO));
        }
        return PageResultInfo.of(total, rows).map(r -> uosRegionConverter.convert(r));
    }

    @Override
    public List<UosRegionSimpleOutDTO> listRegionSimpleInfo() {
        List<UosRegionSimpleOutDTO> collect = Collections.emptyList();
        Optional<List<UosRegionEntity>> uosRegionEntities = Optional.ofNullable(uosRegionMapper.selectList(null));
        if (uosRegionEntities.isPresent()) {
            collect = uosRegionEntities.get().stream().map(r -> {
                UosRegionSimpleOutDTO dto = new UosRegionSimpleOutDTO();
                dto.setRegionId(r.getRegionId());
                dto.setRegionName(r.getRegionName());
                return dto;
            }).collect(Collectors.toList());
        }
        return collect;
    }

    @Override
    public PageResultInfo<UosRegionSimpleOutDTO> pageRegionSimpleInfo(UosRegionPageInDTO uosRegionPageInDTO) {
        UosRegionQueryCriteriaPO uosRegionQueryCriteriaPO = buildRegionCriteria(uosRegionPageInDTO);
        long total = uosRegionMapper.countByCondition(uosRegionQueryCriteriaPO);
        List<UosRegionEntity> rows = null;
        if (total > 0) {
            rows = uosRegionMapper.selectByCondition(uosRegionQueryCriteriaPO,
                    PagingRestrictDo.getPagingRestrict(uosRegionPageInDTO));
        }
        List<UosRegionSimpleOutDTO> collect = null;
        if (!CollectionUtils.isEmpty(rows)) {
            collect = rows.stream().map(r -> {
                UosRegionSimpleOutDTO dto = new UosRegionSimpleOutDTO();
                dto.setRegionId(r.getRegionId());
                dto.setRegionName(r.getRegionName());
                return dto;
            }).collect(Collectors.toList());
        }
        return PageResultInfo.of(total, collect);
    }

    @Override
    public List<UosRegionInfoOutDTO> listRegionInfos(List<String> regionIdList) {
        if (CollectionUtils.isEmpty(regionIdList)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<UosRegionEntity> select = Wrappers.lambdaQuery(UosRegionEntity.class).in(UosRegionEntity::getRegionId, regionIdList)
                .select(UosRegionEntity::getRegionId, UosRegionEntity::getRegionName);
        List<UosRegionEntity> uosRegionEntities = uosRegionMapper.selectList(select);
        if (CollectionUtils.isEmpty(uosRegionEntities)) {
            return Collections.emptyList();
        }
        List<UosRegionInfoOutDTO> collect = uosRegionEntities.stream().map(ent -> uosRegionConverter.convertInfo(ent)).collect(Collectors.toList());
        return collect;
    }

    @Override
    public Map<String, String> getRegionNameMap(List<String> regionIdList) {
        if (CollectionUtil.isEmpty(regionIdList)) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<UosRegionEntity> wrapper = Wrappers.lambdaQuery(UosRegionEntity.class).in(UosRegionEntity::getRegionId, regionIdList)
                .select(UosRegionEntity::getRegionId, UosRegionEntity::getRegionName);
        List<UosRegionEntity> uosRegionEntities = uosRegionMapper.selectList(wrapper);
        if (CollectionUtil.isNotEmpty(uosRegionEntities)) {
            Map<String, String> map = uosRegionEntities.stream().collect(Collectors.toMap(UosRegionEntity::getRegionId, UosRegionEntity::getRegionName));
            return map;
        }
        return Collections.emptyMap();
    }

    private Optional<UosRegionEntity> findRegionByName(String name) {
        if (StringUtils.hasText(name)) {
            LambdaQueryWrapper<UosRegionEntity> con = Wrappers.lambdaQuery(UosRegionEntity.class)
                    .eq(UosRegionEntity::getRegionName, name);
            return Optional.ofNullable(uosRegionMapper.selectOne(con));
        }
        return Optional.empty();
    }

    private Optional<UosRegionEntity> findRegionById(String RegionId) {
        if (StringUtils.hasText(RegionId)) {
            LambdaQueryWrapper<UosRegionEntity> con = Wrappers.lambdaQuery(UosRegionEntity.class)
                    .eq(UosRegionEntity::getRegionId, RegionId);
            return Optional.ofNullable(uosRegionMapper.selectOne(con));
        }
        return Optional.empty();
    }

    private UosRegionQueryCriteriaPO buildRegionCriteria(UosRegionPageInDTO regionQueryInDTO) {
        return UosRegionQueryCriteriaPO.builder()
                .regionName(regionQueryInDTO.getRegionName())
                .build();
    }

}
