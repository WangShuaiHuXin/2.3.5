package com.imapcloud.nest.service;

import com.imapcloud.nest.model.MissionPhotoTypeRelEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 照片和识别类型关系表 服务类
 * </p>
 *
 * @author zheng
 * @since 2021-03-24
 */
public interface MissionPhotoTypeRelService extends IService<MissionPhotoTypeRelEntity> {
    void deleteByTypeIdAndPhotoIdList(@Param("typeList") List<Integer> typeList, @Param("photoIdList") List<Integer> photoIdList);
}
