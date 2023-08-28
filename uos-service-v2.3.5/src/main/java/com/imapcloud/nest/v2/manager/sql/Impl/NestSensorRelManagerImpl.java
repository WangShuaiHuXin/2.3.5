package com.imapcloud.nest.v2.manager.sql.Impl;

import com.google.common.collect.Lists;
import com.imapcloud.nest.mapper.NestSensorRelMapper;
import com.imapcloud.nest.model.NestSensorRelEntity;
import com.imapcloud.nest.v2.manager.dataobj.in.NestSensorRelInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.NestSensorRelOutDO;
import com.imapcloud.nest.v2.manager.sql.NestSensorRelManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 传感器
 *
 * @author boluo
 * @date 2022-08-25
 */
@Component
public class NestSensorRelManagerImpl implements NestSensorRelManager {

    @Resource
    private NestSensorRelMapper nestSensorRelMapper;

    private NestSensorRelOutDO.NestSensorRelEntityOutDO toNestSensorRelEntityOutDO(NestSensorRelEntity nestSensorRelEntity) {
        NestSensorRelOutDO.NestSensorRelEntityOutDO nestSensorRelEntityOutDO =  new NestSensorRelOutDO.NestSensorRelEntityOutDO();
        nestSensorRelEntityOutDO.setSensorId(nestSensorRelEntity.getSensorId());
        nestSensorRelEntityOutDO.setCreatorId(nestSensorRelEntity.getCreatorId());
        nestSensorRelEntityOutDO.setBaseNestId(nestSensorRelEntity.getBaseNestId());
        return nestSensorRelEntityOutDO;
    }

    @Override
    public List<NestSensorRelOutDO.NestSensorRelEntityOutDO> selectListByNestId(String nestId) {

        List<NestSensorRelOutDO.NestSensorRelEntityOutDO> nestSensorRelEntityOutDOList = Lists.newLinkedList();

        List<NestSensorRelEntity> sensorRelEntityList = nestSensorRelMapper.getListByNestId(nestId);
        for (NestSensorRelEntity nestSensorRelEntity : sensorRelEntityList) {
            NestSensorRelOutDO.NestSensorRelEntityOutDO nestSensorRelEntityOutDO = toNestSensorRelEntityOutDO(nestSensorRelEntity);
            nestSensorRelEntityOutDOList.add(nestSensorRelEntityOutDO);
        }
        return nestSensorRelEntityOutDOList;
    }

    @Override
    public int deleteBySensorIdList(List<Integer> sensorIdList) {

        return nestSensorRelMapper.deleteBySensorIdList(sensorIdList, LocalDateTime.now());
    }

    @Override
    public int batchInsert(List<NestSensorRelInDO.NestSensorRelEntityInDO> nestSensorRelEntityInDOList) {
        List<NestSensorRelEntity> sensorRelEntityList = Lists.newLinkedList();
        LocalDateTime localDateTime = LocalDateTime.now();
        for (NestSensorRelInDO.NestSensorRelEntityInDO nestSensorRelEntityInDO : nestSensorRelEntityInDOList) {
            NestSensorRelEntity nestSensorRelEntity = new NestSensorRelEntity();
            nestSensorRelEntity.setSensorId(nestSensorRelEntityInDO.getSensorId());
            nestSensorRelEntity.setBaseNestId(nestSensorRelEntityInDO.getBaseNestId());
            nestSensorRelEntity.setCreatorId(Long.parseLong(nestSensorRelEntityInDO.getCreatorId()));
            nestSensorRelEntity.setCreateTime(localDateTime);
            nestSensorRelEntity.setModifyTime(localDateTime);
            sensorRelEntityList.add(nestSensorRelEntity);
        }
        return nestSensorRelMapper.batchInsert(sensorRelEntityList);
    }

    @Override
    public int deleteByNestId(String nestId) {
        return nestSensorRelMapper.deleteByBaseNestId(nestId);
    }
}
