package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.BaseAppEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 终端信息表 Mapper 接口
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 */
public interface BaseAppMapper extends BaseMapper<BaseAppEntity> {

    @Update("UPDATE base_app SET deleted = #{deleted} WHERE app_id = #{appId}")
    int updateDeletedByAppId(@Param("appId") String appId, @Param("deleted") Integer deleted);

    @Update("UPDATE base_app SET show_status = #{showStatus} WHERE app_id = #{appId}")
    int updateShowStatusByAppId(@Param("appId") String appId, @Param("showStatus") Integer showStatus);

    @Update("UPDATE base_app SET show_status = #{showStatus} WHERE org_code = #{orgCode}")
    int updateShowStatusByOrgCode(@Param("orgCode") String orgCode,@Param("showStatus") Integer showStatus);
}
