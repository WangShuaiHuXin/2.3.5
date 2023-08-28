package com.imapcloud.nest.service.impl;

import com.imapcloud.nest.model.DefectInfoEntity;
import com.imapcloud.nest.mapper.DefectInfoMapper;
import com.imapcloud.nest.service.DefectInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 缺陷的详细信息表 服务实现类
 * </p>
 *
 * @author zheng
 * @since 2021-02-23
 */
@Service
public class DefectInfoServiceImpl extends ServiceImpl<DefectInfoMapper, DefectInfoEntity> implements DefectInfoService {

    @Override
    public List<DefectInfoEntity> getByPhotoId(Long photoId) {
        return baseMapper.getByPhotoId(photoId);
    }

    @Override
    public List<DefectInfoEntity> getDefectInfoList(Long photoId, List<Integer> defectTypeList) {
        return baseMapper.getDefectInfoList(photoId, defectTypeList);
    }
}
