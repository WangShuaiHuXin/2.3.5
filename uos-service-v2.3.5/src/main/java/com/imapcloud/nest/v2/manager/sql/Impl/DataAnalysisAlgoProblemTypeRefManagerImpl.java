package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisAlgoProblemTypeRefEntity;
import com.imapcloud.nest.v2.dao.mapper.*;
import com.imapcloud.nest.v2.manager.dataobj.out.DataAnalysisAlgoProblemTypeRefOutDO;
import com.imapcloud.nest.v2.manager.sql.DataAnalysisAlgoProblemTypeRefManager;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisAlgoProblemTypeRefOutDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 专题
 *
 * @author boluo
 * @date 2022-07-14
 */
@Service
public class DataAnalysisAlgoProblemTypeRefManagerImpl implements DataAnalysisAlgoProblemTypeRefManager {

    @Resource
    private DataAnalysisAlgoProblemTypeRefMapper dataAnalysisAlgoProblemTypeRefMapper;

    @Override
    public List<DataAnalysisAlgoProblemTypeRefOutDTO> getDataAnalysisAlgoProblemTypeRefOutDTO(
            List<String> dataAnalysisTopicProblemPageList) {
        LambdaQueryWrapper<DataAnalysisAlgoProblemTypeRefEntity> condition = Wrappers
                .lambdaQuery(DataAnalysisAlgoProblemTypeRefEntity.class)
                .in(DataAnalysisAlgoProblemTypeRefEntity::getTypeId, dataAnalysisTopicProblemPageList);
        List<DataAnalysisAlgoProblemTypeRefEntity> refs = dataAnalysisAlgoProblemTypeRefMapper.selectList(condition);
        return refs.stream().map(e->{
            DataAnalysisAlgoProblemTypeRefOutDTO d = new DataAnalysisAlgoProblemTypeRefOutDTO();
            BeanUtil.copyProperties(e,d);
            return d;
        }).collect(Collectors.toList());
    }

    @Override
    public List<DataAnalysisAlgoProblemTypeRefOutDO> selectListByTypeRelationIdList(Collection<String> typeRelationIdCollection) {
        if (CollUtil.isEmpty(typeRelationIdCollection)) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<DataAnalysisAlgoProblemTypeRefEntity> queryWrapper = Wrappers.lambdaQuery(DataAnalysisAlgoProblemTypeRefEntity.class)
                .in(DataAnalysisAlgoProblemTypeRefEntity::getTypeRelationId, typeRelationIdCollection)
                .eq(DataAnalysisAlgoProblemTypeRefEntity::getDeleted, false);
        List<DataAnalysisAlgoProblemTypeRefEntity> entityList = dataAnalysisAlgoProblemTypeRefMapper.selectList(queryWrapper);

        if (CollUtil.isEmpty(entityList)) {
            return Collections.emptyList();
        }
        return entityList.stream().map(this::toDataAnalysisAlgoProblemTypeRefOutDO).collect(Collectors.toList());
    }

    private DataAnalysisAlgoProblemTypeRefOutDO toDataAnalysisAlgoProblemTypeRefOutDO(DataAnalysisAlgoProblemTypeRefEntity entity) {

        DataAnalysisAlgoProblemTypeRefOutDO outDTO = new DataAnalysisAlgoProblemTypeRefOutDO();
        outDTO.setTypeId(entity.getTypeId());
        outDTO.setStorageId(entity.getStorageId());
        outDTO.setStorageName(entity.getStorageName());
        outDTO.setFunctionId(entity.getFunctionId());
        outDTO.setFunctionName(entity.getFunctionName());
        outDTO.setTypeRelationId(entity.getTypeRelationId());
        outDTO.setTypeRelationName(entity.getTypeRelationName());
        return outDTO;
    }
}
