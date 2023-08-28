package com.imapcloud.nest.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.imapcloud.nest.v2.dao.entity.UosNestDeviceRefEntity;
import com.imapcloud.nest.v2.dao.entity.UosNestStreamRefEntity;
import com.imapcloud.nest.v2.dao.mapper.UosNestDeviceRefMapper;
import com.imapcloud.nest.v2.dao.mapper.UosNestStreamRefMapper;
import com.imapcloud.nest.v2.service.UosNestDeviceRefService;
import com.imapcloud.nest.v2.service.dto.out.UosNestDeviceRefOutDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Classname UosNestDeviceRefServiceImpl
 * @Description 基站设备关联实现类
 * @Date 2023/4/7 14:53
 * @Author Carnival
 */
@Service
public class UosNestDeviceRefServiceImpl implements UosNestDeviceRefService {

    @Resource
    private UosNestDeviceRefMapper uosNestDeviceRefMapper;

    @Resource
    private UosNestStreamRefMapper uosNestStreamRefMapper;

    @Override
    public List<UosNestDeviceRefEntity> listNestDeviceRef(String nestId) {
        LambdaQueryWrapper<UosNestDeviceRefEntity> con = Wrappers.lambdaQuery(UosNestDeviceRefEntity.class)
                .eq(UosNestDeviceRefEntity::getNestId, nestId)
                .eq(UosNestDeviceRefEntity::getDeleted, 0);
        return uosNestDeviceRefMapper.selectList(con);
    }

    @Override
    public List<UosNestDeviceRefOutDTO> findByNestId(String nestId) {
        return uosNestDeviceRefMapper.findByNestId(nestId);
    }

    @Override
    public void deleteNestDeviceRef(String nestId) {
        LambdaQueryWrapper<UosNestDeviceRefEntity> con = Wrappers.lambdaQuery(UosNestDeviceRefEntity.class)
                .eq(UosNestDeviceRefEntity::getNestId, nestId);
        uosNestDeviceRefMapper.delete(con);
    }

    @Override
    public boolean insertNestDeviceRef(List<UosNestDeviceRefEntity> newEntityList) {
        return uosNestDeviceRefMapper.batchInsert(newEntityList) != 0;
    }

    @Override
    public List<UosNestStreamRefEntity> listNestStreamRef(String nestId) {
        LambdaQueryWrapper<UosNestStreamRefEntity> con = Wrappers.lambdaQuery(UosNestStreamRefEntity.class)
                .eq(UosNestStreamRefEntity::getNestId, nestId);
        return uosNestStreamRefMapper.selectList(con);
    }


}
