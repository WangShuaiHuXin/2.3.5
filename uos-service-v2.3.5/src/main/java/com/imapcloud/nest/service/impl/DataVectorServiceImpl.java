package com.imapcloud.nest.service.impl;

import com.imapcloud.nest.model.DataVectorEntity;
import com.imapcloud.nest.mapper.DataVectorMapper;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.service.DataVectorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 矢量数据表 服务实现类
 * </p>
 *
 * @author hc
 * @since 2021-06-16
 */
@Service
public class DataVectorServiceImpl extends ServiceImpl<DataVectorMapper, DataVectorEntity> implements DataVectorService {

    @Override
    public List<MissionRecordsEntity> getMissionRecords(String startTime, String endTime) {
        return baseMapper.getMissionRecords(startTime,endTime);
    }
}
