package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.dao.entity.NestCodeOperationRecordsEntity;
import com.imapcloud.nest.v2.dao.mapper.NestCodeOperationRecordsMapper;
import com.imapcloud.nest.v2.service.NestCodeOperationRecordsService;
import com.imapcloud.nest.v2.service.converter.NestCodeOperationConverter;
import com.imapcloud.nest.v2.service.dto.in.NestCodeOperationInDTO;
import com.imapcloud.nest.v2.service.dto.out.NestCodeOperationOutDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class NestCodeOperationRecordsServiceImpl implements NestCodeOperationRecordsService {

    @Resource
    private NestCodeOperationRecordsMapper nestCodeOperationRecordsMapper;

    @Override
    public boolean batchSaveRecords(List<NestCodeOperationInDTO> dtoList) {
        if (CollectionUtil.isNotEmpty(dtoList)) {
            List<NestCodeOperationRecordsEntity> collect = dtoList.stream().map(NestCodeOperationConverter.INSTANCES::convert).collect(Collectors.toList());
            int insert = 0;
            for(NestCodeOperationRecordsEntity ent : collect) {
                 insert = nestCodeOperationRecordsMapper.insert(ent);
            }
            return insert > 0;
        }
        return false;
    }

    @Override
    public PageResultInfo<NestCodeOperationOutDTO> listRecordsPage(String nestId, Integer currPage, Integer pageSize) {
        Page<NestCodeOperationRecordsEntity> page = new Page<>(currPage, pageSize);
        LambdaQueryWrapper<NestCodeOperationRecordsEntity> eq = Wrappers.lambdaQuery(NestCodeOperationRecordsEntity.class).eq(NestCodeOperationRecordsEntity::getNestId, nestId).orderByDesc(NestCodeOperationRecordsEntity::getCreatorTime);
        Page<NestCodeOperationRecordsEntity> dataPage = nestCodeOperationRecordsMapper.selectPage(page, eq);
        PageResultInfo<NestCodeOperationOutDTO> pageResultInfo = new PageResultInfo<>();
        List<NestCodeOperationRecordsEntity> records = dataPage.getRecords();
        if (CollectionUtil.isNotEmpty(records)) {
            List<NestCodeOperationOutDTO> collect = records.stream().map(NestCodeOperationConverter.INSTANCES::convertEnt2OutDTO).collect(Collectors.toList());
            pageResultInfo.setRecords(collect);
            pageResultInfo.setTotal(dataPage.getTotal());
        }
        return pageResultInfo;
    }
}
