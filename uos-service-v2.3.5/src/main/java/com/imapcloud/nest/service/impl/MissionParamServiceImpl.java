package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.imapcloud.nest.mapper.MissionParamMapper;
import com.imapcloud.nest.model.MissionParamEntity;
import com.imapcloud.nest.service.MissionParamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 架次参数表 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
@Service
public class MissionParamServiceImpl extends ServiceImpl<MissionParamMapper, MissionParamEntity> implements MissionParamService {

    @Autowired
    private MissionParamMapper missionParamMapper;

    @Override
    public int batchSoftDeleteByIds(List<Integer> idList) {
        if (idList != null && idList.size() > 0) {
            return missionParamMapper.batchSoftDeleteByIds(idList);
        }
        return 0;
    }

    @Override
    public MissionParamEntity queryMissionParamInfo(Integer id) {
        LambdaQueryWrapper<MissionParamEntity> condition = Wrappers.lambdaQuery(MissionParamEntity.class)
                .eq(MissionParamEntity::getId, id);
        return missionParamMapper.selectOne(condition);
    }


}
