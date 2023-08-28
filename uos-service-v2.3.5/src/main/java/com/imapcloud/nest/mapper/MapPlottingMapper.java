package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.imapcloud.nest.model.MapPlottingEntity;
import com.imapcloud.nest.pojo.vo.MapPlottingVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 地图标绘 dao层
 */
public interface MapPlottingMapper extends BaseMapper<MapPlottingEntity> {

    IPage<MapPlottingVO> queryPage(@Param("page") IPage<MapPlottingEntity> page, @Param("params") Map<String ,Object> params);
    List<MapPlottingVO> getList(@Param("params") Map<String ,Object> params);
    List<MapPlottingVO> listByUser(@Param("userId") Long userId);

    MapPlottingVO byId(@Param("id") Integer id, @Param("userId") Long userId);

}
