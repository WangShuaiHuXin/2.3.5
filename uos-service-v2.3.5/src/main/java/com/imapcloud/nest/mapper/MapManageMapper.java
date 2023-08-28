package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.imapcloud.nest.model.MapManageEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 地图管理 dao层
 */
public interface MapManageMapper extends BaseMapper<MapManageEntity> {

    IPage<MapManageEntity> queryPage(@Param("page") IPage<MapManageEntity> page, @Param("params")Map<String, Object> params);

    List<MapManageEntity> listAll(@Param("params")Map<String, Object> params);

    MapManageEntity byId(@Param("id")Integer id);

}
