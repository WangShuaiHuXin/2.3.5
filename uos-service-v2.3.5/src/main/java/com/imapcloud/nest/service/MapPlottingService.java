package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.MapPlottingEntity;
import com.imapcloud.nest.pojo.dto.MapPlottingDto;
import com.imapcloud.nest.pojo.vo.MapPlottingVO;

import java.util.List;
import java.util.Map;

/**
 * 地图标绘 业务层
 */
public interface MapPlottingService extends IService<MapPlottingEntity> {

    IPage<MapPlottingVO> queryPage(Map<String, Object> params);

    MapPlottingVO info(Integer id);

    List<MapPlottingVO> listByUser();

    boolean savePlotting(MapPlottingDto dto);

    boolean updatePlotting(MapPlottingDto dto);
}
