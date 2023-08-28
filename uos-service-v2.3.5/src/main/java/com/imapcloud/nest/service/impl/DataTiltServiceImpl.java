package com.imapcloud.nest.service.impl;

import com.imapcloud.nest.model.DataTiltEntity;
import com.imapcloud.nest.mapper.DataTiltMapper;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.service.DataTiltService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 倾斜数据表 服务实现类
 * </p>
 *
 * @author hc
 * @since 2021-06-16
 */
@Service
public class DataTiltServiceImpl extends ServiceImpl<DataTiltMapper, DataTiltEntity> implements DataTiltService {

    @Override
    public List<MissionRecordsEntity> getMissionRecords(String startTime, String endTime) {
        return baseMapper.getMissionRecords(startTime,endTime);
    }
}
