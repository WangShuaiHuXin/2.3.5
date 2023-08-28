package com.imapcloud.nest.service.impl;

import com.imapcloud.nest.model.MissionPhotoTagRelEntity;
import com.imapcloud.nest.mapper.MissionPhotoTagRelMapper;
import com.imapcloud.nest.service.MissionPhotoTagRelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 照片和标签关系表 服务实现类
 * </p>
 *
 * @author zheng
 * @since 2021-03-24
 */
@Service
public class MissionPhotoTagRelServiceImpl extends ServiceImpl<MissionPhotoTagRelMapper, MissionPhotoTagRelEntity> implements MissionPhotoTagRelService {

    @Override
    public List<MissionPhotoTagRelEntity> getMissionPhotoTagList() {
        return baseMapper.getMissionPhotoTagList();
    }

    @Override
    public void deleteByTagIdAndPhotoIdList(Integer tagId, List<Integer> photoIdList) {
        baseMapper.deleteByTagIdAndPhotoIdList(tagId, photoIdList);
    }
}
