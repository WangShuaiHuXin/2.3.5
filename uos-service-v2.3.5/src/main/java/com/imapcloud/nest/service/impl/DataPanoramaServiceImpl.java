package com.imapcloud.nest.service.impl;

import com.imapcloud.nest.model.DataPanoramaEntity;
import com.imapcloud.nest.mapper.DataPanoramaMapper;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.service.DataPanoramaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 全景数据表 服务实现类
 * </p>
 *
 * @author hc
 * @since 2021-06-16
 */
@Service
public class DataPanoramaServiceImpl extends ServiceImpl<DataPanoramaMapper, DataPanoramaEntity> implements DataPanoramaService {

    @Override
    public List<MissionRecordsEntity> getMissionRecords(String startTime, String endTime) {
        return baseMapper.getMissionRecords(startTime,endTime);
    }
}
