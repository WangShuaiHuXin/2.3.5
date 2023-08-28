package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imapcloud.nest.mapper.NestParamMapper;
import com.imapcloud.nest.model.NestParamEntity;
import com.imapcloud.nest.service.NestParamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 机巢信息 实现类
 *
 * @author: zhengxd
 * @create: 2020/9/25
 **/
@Service
@Slf4j
public class NestParamServiceImpl extends ServiceImpl<NestParamMapper, NestParamEntity> implements NestParamService {


    @Override
    public void deleteByNestId(Integer nestId) {
        baseMapper.deleteByNestId(nestId);
    }

    @Override
    public Double selectStartStopPointAltitudeByNestId(Integer nestId) {
        if (nestId != null) {
            return baseMapper.selectStartStopAltitude();
        }
        return 0.0;
    }
}
