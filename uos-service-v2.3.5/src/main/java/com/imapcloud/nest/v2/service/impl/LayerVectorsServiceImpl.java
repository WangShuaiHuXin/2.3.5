package com.imapcloud.nest.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.utils.ParseVectorFileUtil;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.dao.entity.LayerVectorsEntity;
import com.imapcloud.nest.v2.dao.mapper.LayerVectorsMapper;
import com.imapcloud.nest.v2.service.LayerVectorsService;
import com.imapcloud.nest.v2.service.converter.LayerVectorsConverter;
import com.imapcloud.nest.v2.service.dto.out.LayerVectorsOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Classname LayerVectorsServiceImpl
 * @Description 矢量图层实现类
 * @Date 2022/12/14 19:45
 * @Author Carnival
 */

@Slf4j
@Service
public class LayerVectorsServiceImpl implements LayerVectorsService {

    @Resource
    private LayerVectorsConverter layerVectorsConverter;

    @Resource
    private LayerVectorsMapper layerVectorsMapper;

    private static final Integer Fail = 0;

    @Override
    public Boolean uploadVectors(MultipartFile file, String orgCode, String layerVectorName) {
//        Optional<LayerVectorsEntity> layerVectors = findLayerVectorsByName(layerVectorName, orgCode);
//        if (layerVectors.isPresent()) {
//            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_GRID_SERVICE_07.getContent()));
//        }
        if (layerVectorName.length() > 50) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_GRID_SERVICE_08.getContent()));
        }

        if (file == null) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_GRID_SERVICE_01.getContent()));
        }
        if (!(Objects.requireNonNull(file.getOriginalFilename()).endsWith(".json") || file.getOriginalFilename().endsWith(".kml"))){
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_GRID_SERVICE_02.getContent()));
        }
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        String result = "";
        LayerVectorsEntity entity = new LayerVectorsEntity();
        entity.setLayerVectorId(BizIdUtils.snowflakeIdStr());
        entity.setLayerVectorName(layerVectorName);
        entity.setOrgCode(orgCode);
        try {
            // kml解析
            if (".kml".equals(suffixName)) {
                result = ParseVectorFileUtil.parseKmlFile(file);
            }
            // json解析
            if (".json".equals(suffixName)) {
                result = ParseVectorFileUtil.parseJsonFile(file);
            }
        } catch (Exception e) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_GRID_SERVICE_09.getContent()));
        }
        if (!StringUtils.hasText(result)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_GRID_SERVICE_09.getContent()));
        } else {
            entity.setLayerVectorJson(result);
            int res = layerVectorsMapper.insert(entity);
            return res != Fail;
        }
    }

    @Override
    public List<LayerVectorsOutDTO> queryVectors(String orgCode) {
        LambdaQueryWrapper<LayerVectorsEntity> conData = Wrappers.lambdaQuery(LayerVectorsEntity.class)
                .eq(LayerVectorsEntity::getOrgCode, orgCode);
        List<LayerVectorsEntity> layerVectorsEntities = layerVectorsMapper.selectList(conData);
        if (!CollectionUtils.isEmpty(layerVectorsEntities)) {
            return layerVectorsEntities.stream().map(r -> layerVectorsConverter.convert(r)).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public Boolean updateVectors(String layerVectorId, String name) {
        if (name.length() > 50) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_GRID_SERVICE_08.getContent()));
        }
        Optional<LayerVectorsEntity> layerVectorsById = findLayerVectorsById(layerVectorId);
        if (layerVectorsById.isPresent()) {
            LayerVectorsEntity entityById = layerVectorsById.get();
//            Optional<LayerVectorsEntity> layerVectors = findLayerVectorsByName(name, entityById.getOrgCode());
//            if (layerVectors.isPresent()) {
//                throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_GRID_SERVICE_07.getContent()));
//            }
            entityById.setLayerVectorName(name);
            layerVectorsMapper.updateById(entityById);
            return true;
        }
        return false;
    }

    @Override
    public Boolean deleteVectors(String layerVectorId) {
        Optional<LayerVectorsEntity> layerVectorsById = findLayerVectorsById(layerVectorId);
        if (layerVectorsById.isPresent()) {
            LayerVectorsEntity entity = layerVectorsById.get();
            int res = layerVectorsMapper.deleteById(entity.getId());
            return res != Fail;
        }
        return false;
    }



    /**
     * 根据ID查询区域网格
     */
    private Optional<LayerVectorsEntity> findLayerVectorsById(String layerVectorsId) {
        if (StringUtils.hasText(layerVectorsId)) {
            LambdaQueryWrapper<LayerVectorsEntity> con = Wrappers.lambdaQuery(LayerVectorsEntity.class)
                    .eq(LayerVectorsEntity::getLayerVectorId, layerVectorsId);
            return Optional.ofNullable(layerVectorsMapper.selectOne(con));
        }
        return Optional.empty();
    }

    private Optional<LayerVectorsEntity> findLayerVectorsByName(String name, String orgCode) {
        if (StringUtils.hasText(name)) {
            LambdaQueryWrapper<LayerVectorsEntity> con = Wrappers.lambdaQuery(LayerVectorsEntity.class)
                    .eq(LayerVectorsEntity::getLayerVectorName, name)
                    .eq(LayerVectorsEntity::getOrgCode, orgCode);
            return Optional.ofNullable(layerVectorsMapper.selectOne(con));
        }
        return Optional.empty();
    }
}
