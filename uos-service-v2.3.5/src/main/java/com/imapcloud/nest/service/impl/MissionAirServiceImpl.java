package com.imapcloud.nest.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.imapcloud.nest.model.MissionAirEntity;
import com.imapcloud.nest.mapper.MissionAirMapper;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.service.MissionAirService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imapcloud.nest.service.MissionRecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hc
 * @since 2021-04-21
 */
@Service
public class MissionAirServiceImpl extends ServiceImpl<MissionAirMapper, MissionAirEntity> implements MissionAirService {
    @Autowired
    private MissionRecordsService missionRecordsService;

    @Override
    public Map getDataByMissionId(Integer missionRecordsId) {
        MissionRecordsEntity missionRecordsEntity = missionRecordsService.getById(missionRecordsId);
        List<MissionAirEntity> list = this.lambdaQuery().eq(MissionAirEntity::getMissionId, missionRecordsEntity.getMissionId()).eq(MissionAirEntity::getMissionRecordsId, missionRecordsId).list();
        Map map = new HashMap();
        if (list != null && list.size() > 0) {
            List<JSONObject> dataList = list.stream().map(e -> JSONObject.parseObject(e.getData())).collect(Collectors.toList());
            map.put("dataList", dataList);
        }
        return map;
    }

    @Override
    public List<MissionRecordsEntity> getMissionRecords(String startTime, String endTime) {
        return baseMapper.getMissionRecords(startTime, endTime);
    }

    @Override
    public int selectNum(Integer missionRecordsId, String orgCode) {
        Integer count = this.lambdaQuery().eq(MissionAirEntity::getMissionRecordsId, missionRecordsId)
                .likeRight(MissionAirEntity::getOrgCode, orgCode).count();
        return count;
    }
}
