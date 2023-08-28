package com.imapcloud.nest.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.web.exception.BizParameterException;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.dao.entity.DataInterestPointEntity;
import com.imapcloud.nest.v2.dao.mapper.DataInterestPointMapper;
import com.imapcloud.nest.v2.dao.po.DataInterestPointQueryCriteriaPO;
import com.imapcloud.nest.v2.service.DataInterestPointService;
import com.imapcloud.nest.v2.service.converter.DataInterestPointConverter;
import com.imapcloud.nest.v2.service.dto.in.DataInterestPointInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataInterestPointPageInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataInterestPointOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname DataInterestPointServiceImpl
 * @Description 全景兴趣点接口实现类
 * @Date 2022/9/26 11:37
 * @Author Carnival
 */

@Slf4j
@Service
public class DataInterestPointServiceImpl implements DataInterestPointService {

    @Resource
    private DataInterestPointMapper dataInterestPointMapper;

    @Resource
    private DataInterestPointConverter dataInterestPointConverter;

    private static final Integer Fail = 0;

    private static final String INTEREST_POINT_KEY = "geoai:panorama:interest:";

    @Override
    public Boolean addPoint(DataInterestPointInDTO dto) {
        DataInterestPointEntity entity = new DataInterestPointEntity();
        entity.setPointId(BizIdUtils.snowflakeIdStr());
        entity.setPointName(dto.getPointName());
        entity.setPointHeight(dto.getPointHeight());
        entity.setPointLongitude(dto.getPointLongitude());
        entity.setPointLatitude(dto.getPointLatitude());
        entity.setPointType(dto.getPointType());
        entity.setAddress(dto.getAddress());
        entity.setBrief(dto.getBrief());
        entity.setMapDistance(dto.getMapDistance());
        entity.setPanoramaDistance(dto.getPanoramaDistance());
        entity.setTagId(dto.getTagId());
        // 默认使用账号所在的单位
        if (StringUtils.isEmpty(dto.getOrgCode())) {
            entity.setOrgCode(TrustedAccessTracerHolder.get().getOrgCode());
        } else {
            entity.setOrgCode(dto.getOrgCode());
        }

        return dataInterestPointMapper.insert(entity) != Fail;
    }

    @Override
    public Boolean deletePoints(List<String> pointIds) {
        if (!CollectionUtils.isEmpty(pointIds)) {
            for (String pointId : pointIds) {
                Optional<DataInterestPointEntity> pointById = findPointById(pointId);
                if (pointById.isPresent()) {
                    DataInterestPointEntity entity = pointById.get();
                    dataInterestPointMapper.deleteById(entity.getId());
                }
            }
            return true;
        }
        return null;
    }

    @Override
    public Boolean updatePoint(String pointId, DataInterestPointInDTO dto) {
        Optional<DataInterestPointEntity> pointById = findPointById(pointId);
        if (!pointById.isPresent()) {
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_PANORAMIC_POINT_TO_BE_MODIFIED_DOES_NOT_EXIST.getContent()));
        }
        DataInterestPointEntity entity = pointById.get();
        entity.setPointId(BizIdUtils.snowflakeIdStr());
        entity.setPointName(dto.getPointName());
        entity.setPointHeight(dto.getPointHeight());
        entity.setPointLongitude(dto.getPointLongitude());
        entity.setPointLatitude(dto.getPointLatitude());
        entity.setPointType(dto.getPointType());
        entity.setAddress(dto.getAddress());
        entity.setBrief(dto.getBrief());
        entity.setMapDistance(dto.getMapDistance());
        entity.setPanoramaDistance(dto.getPanoramaDistance());
        entity.setModifiedTime(LocalDateTime.now());
        entity.setModifierId(TrustedAccessTracerHolder.get().getAccountId());
        entity.setTagId(dto.getTagId());

        return dataInterestPointMapper.updateById(entity) != Fail;

    }

    @Override
    public PageResultInfo<DataInterestPointOutDTO> queryPointPage(DataInterestPointPageInDTO dto) {
        DataInterestPointQueryCriteriaPO pointQueryCriteriaPO = buildPointPO(dto);
        long total = dataInterestPointMapper.countByCondition(pointQueryCriteriaPO);
        List<DataInterestPointEntity> rows = null;
        if (total > 0) {
            rows = dataInterestPointMapper.selectByCondition(pointQueryCriteriaPO,
                    PagingRestrictDo.getPagingRestrict(dto));
        }
        return PageResultInfo.of(total, rows).map(r -> dataInterestPointConverter.convert(r));

    }

    @Override
    public List<DataInterestPointOutDTO> queryAllPoints(String orgCode, String pointName, String tagId) {

        LambdaQueryWrapper<DataInterestPointEntity> con = Wrappers.lambdaQuery(DataInterestPointEntity.class)
                .eq(DataInterestPointEntity::getOrgCode, orgCode);
        if (!StringUtils.isEmpty(tagId)) {
            con.eq(DataInterestPointEntity::getTagId, tagId);
        }
        if (!StringUtils.isEmpty(pointName)) {
            con.like(DataInterestPointEntity::getPointName, pointName);
        }
        Optional<List<DataInterestPointEntity>> entityList = Optional.ofNullable(dataInterestPointMapper.selectList(con));
        if (entityList.isPresent()) {
            List<DataInterestPointEntity> entities = entityList.get();
            // 已和前端确认返回的字段
            List<DataInterestPointOutDTO> collect = entities.stream().map(r -> {
                DataInterestPointOutDTO dto = new DataInterestPointOutDTO();
                dto.setAddress(r.getAddress());
                dto.setBrief(r.getBrief());
                dto.setMapDistance(r.getMapDistance());
                dto.setPanoramaDistance(r.getPanoramaDistance());
                dto.setPointHeight(r.getPointHeight());
                dto.setPointId(r.getPointId());
                dto.setPointLatitude(r.getPointLatitude());
                dto.setPointLongitude(r.getPointLongitude());
                dto.setPointName(r.getPointName());
                dto.setPointType(r.getPointType());
                dto.setTagId(r.getTagId());
                return dto;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    @Override
    public DataInterestPointOutDTO queryPoint(String pointId) {
        Optional<DataInterestPointEntity> pointById = findPointById(pointId);
        if (pointById.isPresent()) {
            DataInterestPointEntity entity = pointById.get();
            return dataInterestPointConverter.convert(entity);
        }
        return null;
    }

    /**
     * 根据ID获取全景兴趣点
     */
    private Optional<DataInterestPointEntity> findPointById(String pointId) {
        if (StringUtils.hasText(pointId)) {
            LambdaQueryWrapper<DataInterestPointEntity> con = Wrappers.lambdaQuery(DataInterestPointEntity.class)
                    .eq(DataInterestPointEntity::getPointId, pointId);
            return Optional.ofNullable(dataInterestPointMapper.selectOne(con));
        }
        return Optional.empty();
    }

    /**
     * 建立PO
     */
    private DataInterestPointQueryCriteriaPO buildPointPO(DataInterestPointPageInDTO dto) {
        return DataInterestPointQueryCriteriaPO.builder()
                .pointName(dto.getPointName())
                .pointType(dto.getPointType())
                .orgCode(dto.getOrgCode())
                .tagId(dto.getTagId())
                .build();
    }
}
