package com.imapcloud.nest.service.impl;

import com.imapcloud.nest.model.DataPointCloudEntity;
import com.imapcloud.nest.mapper.DataPointCloudMapper;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.service.DataPointCloudService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 点云数据表 服务实现类
 * </p>
 *
 * @author hc
 * @since 2021-06-16
 */
@Service
public class DataPointCloudServiceImpl extends ServiceImpl<DataPointCloudMapper, DataPointCloudEntity> implements DataPointCloudService {

    @Override
    public List<MissionRecordsEntity> getMissionRecords(String startTime, String endTime) {
        return baseMapper.getMissionRecords(startTime,endTime);
    }

    @Override
    public String getLasUrlById(Integer id) {
        return baseMapper.getLasUrlById(id);
    }

    @Override
    public void updateStateById(Integer beforeId, Integer afterId, Integer state) {
        baseMapper.updateStateById(beforeId, afterId, state);
    }
}
