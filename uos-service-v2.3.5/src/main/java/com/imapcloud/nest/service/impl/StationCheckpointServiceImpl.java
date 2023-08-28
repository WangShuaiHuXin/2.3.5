package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.imapcloud.nest.model.StationCheckpointEntity;
import com.imapcloud.nest.mapper.StationCheckpointMapper;
import com.imapcloud.nest.model.StationDeviceEntity;
import com.imapcloud.nest.service.StationCheckpointService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 巡检点，对应拍照点坐标、角度等信息 服务实现类
 * </p>
 *
 * @author daolin
 * @since 2020-11-02
 */
@Service
public class StationCheckpointServiceImpl extends ServiceImpl<StationCheckpointMapper, StationCheckpointEntity> implements StationCheckpointService {

    @Autowired
    StationCheckpointMapper stationCheckpointMapper;


    @Override
    public void deleteByNestId(String nestId) {
        stationCheckpointMapper.deleteByNestId(nestId);
    }

    @Override
    public Integer insert(StationCheckpointEntity point) {
        return stationCheckpointMapper.insert(point);
    }

    @Override
    public List<StationCheckpointEntity> selectInDeviceIdList(List<Integer> deviceIds) {
        QueryWrapper<StationCheckpointEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("device_id",deviceIds);
        return stationCheckpointMapper.selectList(queryWrapper);
    }

    @Override
    public Integer updateByUuid(StationCheckpointEntity checkPoint) {
        return baseMapper.update(checkPoint, new QueryWrapper<StationCheckpointEntity>().eq("uuid",checkPoint.getUuid()));
    }
}
