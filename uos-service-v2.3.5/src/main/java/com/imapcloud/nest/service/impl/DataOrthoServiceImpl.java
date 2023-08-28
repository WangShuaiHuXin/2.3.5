package com.imapcloud.nest.service.impl;

import com.imapcloud.nest.model.DataOrthoEntity;
import com.imapcloud.nest.mapper.DataOrthoMapper;
import com.imapcloud.nest.model.DataOrthoJsonEntity;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.service.DataOrthoJsonService;
import com.imapcloud.nest.service.DataOrthoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class DataOrthoServiceImpl extends ServiceImpl<DataOrthoMapper, DataOrthoEntity> implements DataOrthoService {

    @Autowired
    private DataOrthoJsonService dataOrthoJsonService;

    @Override
    public List<MissionRecordsEntity> getMissionRecords(String startTime, String endTime) {
        return baseMapper.getMissionRecords(startTime,endTime);
    }

    @Override
    public Map getAllMap(Integer id) {
        DataOrthoEntity dataOrthoEntity = this.getById(id);
        List<DataOrthoJsonEntity> list = dataOrthoJsonService.lambdaQuery().eq(DataOrthoJsonEntity::getOrthoId, dataOrthoEntity.getId()).list();
        dataOrthoEntity.setRecordUrls(list);
        Map map = new HashMap();
        map.put("multispectral",dataOrthoEntity);
        return map;
    }
}
