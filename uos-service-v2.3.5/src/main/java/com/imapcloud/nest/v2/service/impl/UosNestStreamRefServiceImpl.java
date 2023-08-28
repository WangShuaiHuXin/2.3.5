package com.imapcloud.nest.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.imapcloud.nest.pojo.dto.UosNestStreamRefDTO;
import com.imapcloud.nest.v2.dao.entity.UosNestStreamRefEntity;
import com.imapcloud.nest.v2.dao.mapper.UosNestStreamRefMapper;
import com.imapcloud.nest.v2.service.UosNestStreamRefService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class UosNestStreamRefServiceImpl implements UosNestStreamRefService {

    @Resource
    private UosNestStreamRefMapper uosNestStreamRefMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStreamIdByNestId(String nestId, String streamId, Integer streamUse) {
        LambdaQueryWrapper<UosNestStreamRefEntity> condition = Wrappers.lambdaQuery(UosNestStreamRefEntity.class)
                .eq(UosNestStreamRefEntity::getNestId, nestId);
        uosNestStreamRefMapper.delete(condition);

        UosNestStreamRefEntity entity = new UosNestStreamRefEntity();
        entity.setNestId(nestId);
        entity.setStreamId(streamId);
        entity.setStreamUse(streamUse);
        entity.setCreatorId(TrustedAccessTracerHolder.get().getAccountId());
        entity.setModifierId(TrustedAccessTracerHolder.get().getAccountId());
        uosNestStreamRefMapper.insert(entity);

    }

    @Override
    public UosNestStreamRefDTO findByNestId(String nestId, Integer streamUse) {
        return uosNestStreamRefMapper.findByNestId(nestId, streamUse);
    }

    @Override
    public List<UosNestStreamRefDTO> listByNestId(String nestId) {
        return uosNestStreamRefMapper.listByNestId(nestId);
    }

    @Override
    public void deleteNestStreamRef(String nestId) {
        LambdaQueryWrapper<UosNestStreamRefEntity> condition = Wrappers.lambdaQuery(UosNestStreamRefEntity.class)
                .eq(UosNestStreamRefEntity::getNestId, nestId);
        uosNestStreamRefMapper.delete(condition);
    }

    @Override
    public String getStreamIdByNestId(String nestId, Integer streamUse) {
        if(StringUtils.hasText(nestId)){
            LambdaQueryWrapper<UosNestStreamRefEntity> con = Wrappers.lambdaQuery(UosNestStreamRefEntity.class)
                    .eq(UosNestStreamRefEntity::getNestId, nestId)
                    .eq(UosNestStreamRefEntity::getStreamUse, streamUse);
            List<UosNestStreamRefEntity> entities = uosNestStreamRefMapper.selectList(con);
            if(!CollectionUtils.isEmpty(entities)){
                if(entities.size() > 1){
                    log.warn("基站推流信息存在重复值 ==> nestId={},streamUse={}", nestId, streamUse);
                }
                return entities.get(0).getStreamId();
            }
        }
        return null;
    }
}
