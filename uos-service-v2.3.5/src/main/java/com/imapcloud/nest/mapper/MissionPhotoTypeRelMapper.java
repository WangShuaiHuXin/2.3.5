package com.imapcloud.nest.mapper;

import com.imapcloud.nest.model.MissionPhotoTypeRelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 照片和识别类型关系表 Mapper 接口
 * </p>
 *
 * @author zheng
 * @since 2021-03-24
 */
public interface MissionPhotoTypeRelMapper extends BaseMapper<MissionPhotoTypeRelEntity> {

    void deleteByTypeIdAndPhotoIdList(@Param("typeList") List<Integer> typeList, @Param("photoIdList") List<Integer>photoIdList);
}
