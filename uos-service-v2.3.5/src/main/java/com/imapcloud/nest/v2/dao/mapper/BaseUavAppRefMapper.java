package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.BaseUavAppRefEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 移动终端无人机关联表 Mapper 接口
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 */
public interface BaseUavAppRefMapper extends BaseMapper<BaseUavAppRefEntity> {

    @Update("UPDATE base_uav_app_ref SET deleted = #{deleted} WHERE app_id = #{appId} AND uav_id = #{uavId}")
    int updateDeletedByAppIdAndUavId(@Param("appId") String appId, @Param("uavId") String uavId, @Param("deleted") Integer deleted);
}
