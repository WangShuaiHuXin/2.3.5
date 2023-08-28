package com.imapcloud.nest.service.impl;

import com.imapcloud.nest.model.MissionPhotoTypeRelEntity;
import com.imapcloud.nest.mapper.MissionPhotoTypeRelMapper;
import com.imapcloud.nest.service.MissionPhotoTypeRelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 照片和识别类型关系表 服务实现类
 * </p>
 *
 * @author zheng
 * @since 2021-03-24
 */
@Service
public class MissionPhotoTypeRelServiceImpl extends ServiceImpl<MissionPhotoTypeRelMapper, MissionPhotoTypeRelEntity> implements MissionPhotoTypeRelService {

    @Override
    public void deleteByTypeIdAndPhotoIdList(List<Integer> typeList, List<Integer> photoIdList) {
        baseMapper.deleteByTypeIdAndPhotoIdList(typeList, photoIdList);
    }
}
