package com.imapcloud.nest.service.impl;

import com.imapcloud.nest.model.NestSensorRelEntity;
import com.imapcloud.nest.mapper.NestSensorRelMapper;
import com.imapcloud.nest.model.SensorEntity;
import com.imapcloud.nest.service.NestSensorRelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 传感器表 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2020-09-22
 */
@Service
public class NestSensorRelServiceImpl extends ServiceImpl<NestSensorRelMapper, NestSensorRelEntity> implements NestSensorRelService {

    @Override
    public void deleteNestSensor(String nestId, List<Integer> delSensorIdList) {
        baseMapper.deleteNestSensor(nestId, delSensorIdList);
    }

    @Override
    public List<NestSensorRelEntity> getSensorByNestId(String nestId) {
        return baseMapper.getSensorByNestId(nestId);
    }

    @Override
    public NestSensorRelEntity getSpeakerSensorByNestId(Integer nestId) {
        return baseMapper.getSpeakerSensorByNestId(nestId);
    }

    @Override
    public void deleteByNestId(String nestId) {
        baseMapper.deleteByNestId(nestId);
    }

    @Override
    public List<Integer> getNestSensorId(Integer nestId, List<Integer> idList) {
        return baseMapper.getNestSensorId(nestId, idList);
    }

    @Override
    public int updateVolumeByNestId(Integer volume, String nestId) {
        return baseMapper.updateVolumeByNestId(volume, nestId);
    }

    @Override
    public int updateRepeatByNestId(Integer repeat, Integer nestId) {
        return baseMapper.updateRepeatByNestId(repeat, nestId);
    }
}
