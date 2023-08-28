package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.DataScenePhotoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 现场取证
 *
 * @author boluo
 * @date 2022-07-13
 */
@Mapper
public interface DataScenePhotoMapper extends BaseMapper<DataScenePhotoEntity> {
    /**
     * 插入实体
     *
     * @param dataScenePhotoEntity 数据场景照片实体
     * @return int
     */
    int insertEntity(DataScenePhotoEntity dataScenePhotoEntity);
}
