package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imapcloud.nest.mapper.DataMultispectralMapper;
import com.imapcloud.nest.model.DataMultispectralDirectorEntity;
import com.imapcloud.nest.model.DataMultispectralEntity;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.service.DataMultispectralDirectorService;
import com.imapcloud.nest.service.DataMultispectralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 正射数据表 服务实现类
 * </p>
 *
 * @author hc
 * @since 2021-06-16
 */
@Service
public class DataMultispectralServiceImpl extends ServiceImpl<DataMultispectralMapper, DataMultispectralEntity> implements DataMultispectralService {

    @Autowired
    private DataMultispectralDirectorService dataMultispectralDirectorService;

    @Override
    public List<MissionRecordsEntity> getMissionRecords(String startTime, String endTime) {
        return baseMapper.getMissionRecords(startTime,endTime);
    }

    @Override
    public Map getAllMap(Integer id) {
        DataMultispectralEntity dataMultispectralEntity = this.getById(id);
        List<DataMultispectralDirectorEntity> list = dataMultispectralDirectorService.lambdaQuery().eq(DataMultispectralDirectorEntity::getMultispectralId, dataMultispectralEntity.getId()).list();
        dataMultispectralEntity.setRecordUrls(list);
        Map map = new HashMap();
        map.put("multispectral",dataMultispectralEntity);
        return map;
    }
}
