package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.model.GisdataFileRouteEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 地理信息文件路径表 Mapper 接口
 * </p>
 *
 * @author root
 * @since 2020-09-23
 */
public interface GisdataFileRouteMapper extends BaseMapper<GisdataFileRouteEntity> {

    List<GisdataFileRouteEntity> getGisDataFileRouteList(@Param("visibleOrgCode") String visibleOrgCode, @Param("type") Integer type, @Param("orgCode") String orgCode,
                                                         @Param("checkStatus") Integer checkStatus, @Param("name") String name);
}
